package semaforo;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {

    private String posicao;
    private int tempoVerde;
    private int tempoVermelho;
    private String estadoAtual = "VERMELHO";
    private boolean mudandoParaVerde = false; // Nova variável de controle
    private CrossroadGUI gui; // Referência para a GUI
    
    @Override
    protected void setup() {
        
        Object[] parametros = getArguments();
        if (parametros != null && parametros.length == 3) {
            posicao = (String) parametros[0];
            tempoVerde = (int) parametros[1];
            tempoVermelho = (int) parametros[2];
        } else {
            System.err.println("Parâmetros inválidos para o agente semáforo!");
            doDelete();
            return;
        }
        
        System.out.println("SEMAFORO " + posicao + " INICIADO");
        
        // Obtém a instância da GUI
        gui = CrossroadGUI.getInstance();

        // Comportamento para processar mensagens recebidas
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String conteudo = msg.getContent();

                    switch (conteudo) {
                        case "VERDE":
                            estadoAtual = "VERDE";
                            mudandoParaVerde = true;
                            System.out.println("SEMAFORO " + posicao + " MUDOU PARA VERDE");
                            atualizarSemaforoGUI();
                            break;
                        case "VERMELHO":
                            estadoAtual = "VERMELHO";
                            mudandoParaVerde = false;
                            System.out.println("SEMAFORO " + posicao + " MUDOU PARA VERMELHO");
                            atualizarSemaforoGUI();
                            break;
                        case "STATUS":
                            ACLMessage resposta = msg.createReply();
                            resposta.setPerformative(ACLMessage.INFORM);
                            resposta.setContent(estadoAtual);
                            send(resposta);
                            System.out.println("STATUS ENVIADO DO SEMAFORO " + posicao + ": " + estadoAtual);
                            break;
                        default:
                            System.out.println("Mensagem desconhecida recebida pelo semáforo " + posicao + ": " + conteudo);
                            break;
                    }
                } else {
                    block();
                }
            }
        });
    }
    
    // Método para atualizar a GUI com a cor do semáforo atual
    private void atualizarSemaforoGUI() {
        if (gui != null) {
            gui.atualizarSemaforo(posicao, estadoAtual);
        } else {
            System.err.println("GUI não está inicializada.");
        }
    }
}
