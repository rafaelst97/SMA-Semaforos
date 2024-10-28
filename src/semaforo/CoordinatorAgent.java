package semaforo;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import java.util.Random;

public class CoordinatorAgent extends Agent {
    
    // CONSTANTES DO SISTEMA
    public static final int TEMPO_VERMELHO = 5000;
    public static final int TEMPO_VERDE = 3000;
    public static final int MAX_CARROS = 1;
    private Random random = new Random();
    private CrossroadGUI gui;

    // Lista dos nomes dos semáforos em sentido horário
    private String[] semaforos = {"semaforo_N", "semaforo_E", "semaforo_S", "semaforo_W"};
    private int semaforoAtual = 0;
    
    @Override
    protected void setup() {
        
        // Inicia a interface gráfica e guarda a referência
        new Thread(() -> javafx.application.Application.launch(CrossroadGUI.class)).start();
        
        try {
            Thread.sleep(2000); // Aguarda até 2 segundos para garantir a inicialização
            gui = CrossroadGUI.getInstance(); // Obtém a instância da GUI
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {
            ContainerController container = getContainerController();
            
            // Criação de parâmetros para cada agente semáforo
            Object[] parametrosSemaforo_N = new Object[] { "N", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_E = new Object[] { "E", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_S = new Object[] { "S", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_W = new Object[] { "W", TEMPO_VERDE, TEMPO_VERMELHO };

            // Criação dos agentes semáforo
            criarSemaforo(container, "semaforo_N", parametrosSemaforo_N);
            criarSemaforo(container, "semaforo_E", parametrosSemaforo_E);
            criarSemaforo(container, "semaforo_S", parametrosSemaforo_S);
            criarSemaforo(container, "semaforo_W", parametrosSemaforo_W);

            // Criação inicial dos carros
            for (int i = 0; i < MAX_CARROS; i++) {
                criarCarro(container);
            }
            
            // Criação do RadarAgent
            AgentController radar = container.createNewAgent("radar", "semaforo.RadarAgent", null);
            radar.start();

            // Adiciona o comportamento que controla os semáforos
            addBehaviour(new TickerBehaviour(this, TEMPO_VERDE + TEMPO_VERMELHO) {
                @Override
                protected void onTick() {
                    // Enviar mensagem de vermelho para todos os semáforos
                    for (String semaforo : semaforos) {
                        enviarMensagem(semaforo, "VERMELHO");
                    }

                    // Aguarda um tempo antes de enviar a mensagem de verde para permitir a troca
                    doWait(500);

                    // Enviar mensagem de verde para o semáforo atual
                    enviarMensagem(semaforos[semaforoAtual], "VERDE");

                    // Atualizar para o próximo semáforo em sentido horário
                    semaforoAtual = (semaforoAtual + 1) % semaforos.length;
                    
                    // Solicitar o status de todos os semáforos
                    solicitarStatusSemaforos();
                }
            });

            // Adiciona comportamento para receber o status dos semáforos
            addBehaviour(new CyclicBehaviour(this) {
                @Override
                public void action() {
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    if (msg != null) {
                        System.out.println("Status do semaforo " + msg.getSender().getLocalName() + ": " + msg.getContent());
                    } else {
                        block();
                    }
                }
            });

            // Adiciona comportamento para receber mensagens do RadarAgent
            addBehaviour(new CyclicBehaviour(this) {
                @Override
                public void action() {
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    if (msg != null) {
                        processarMensagemRadar(msg);
                    } else {
                        block();
                    }
                }
            });
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void criarSemaforo(ContainerController container, String nome, Object[] parametros) {
        try {
            AgentController semaforo = container.createNewAgent(nome, "semaforo.TrafficLightAgent", parametros);
            semaforo.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    
    private void criarCarro(ContainerController container) {
        try {
            int idAleatorio = random.nextInt(1001);
            String nomeCarro = "carro" + idAleatorio;
            AgentController carro = container.createNewAgent(nomeCarro, "semaforo.CarAgent", null);
            carro.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
    
    private void enviarMensagem(String semaforo, String conteudo) {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID(semaforo, AID.ISLOCALNAME));
        msg.setContent(conteudo);
        send(msg);
    }

    private void solicitarStatusSemaforos() {
        ACLMessage statusRequest = new ACLMessage(ACLMessage.REQUEST);
        for (String semaforo : semaforos) {
            statusRequest.addReceiver(new AID(semaforo, AID.ISLOCALNAME));
        }
        statusRequest.setContent("STATUS");
        send(statusRequest);
    }
    
    private void processarMensagemRadar(ACLMessage msg) {
        System.out.println("Mensagem recebida: " + msg.getContent());
        String[] conteudo = msg.getContent().split(":");
        if (conteudo[0].equals("INFRACAO")) {
            String placa = conteudo[1];
            System.out.println("Placa multada recebida: " + placa);
            
            // Aguarda a inicialização da GUI antes de adicionar a placa
            esperarGuiInicializada();
            
            // Atualiza a lista de placas multadas na GUI
            if (gui != null) {
                System.out.println("Atualizando GUI com placa: " + placa);
                gui.adicionarInfracao(placa);
            } else {
                System.out.println("GUI não está inicializada corretamente.");
            }
        }
    }

    private void esperarGuiInicializada() {
        while (gui == null || !gui.estaInicializada()) {
            try {
                Thread.sleep(100); // Espera 100 ms antes de verificar novamente
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
