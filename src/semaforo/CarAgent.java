/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.Random;

public class CarAgent extends Agent {
    private String placa;
    private boolean furaSinal;
    private String ruaNascimento;
    private String direcao;
    private int distanciaRestanteRua = 200;
    private int localSemaforo = 60;
    private int distanciaParaSemaforo = distanciaRestanteRua - localSemaforo;
    private String estadoAtual = "MOVENDO-SE";
    private Random random = new Random();

    @Override
    protected void setup() {
        
        this.geraAtributosCarro();
        System.out.println("Gerado carro com placa " + placa);
        System.out.println("Ele vai furar sinal? " + furaSinal);
        System.out.println("Rua que nasce " + ruaNascimento);
        
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                if (distanciaRestanteRua > 0) {
                    if (estadoAtual != "PARADO") {
                        distanciaRestanteRua -= 10;
                        distanciaParaSemaforo = distanciaRestanteRua - localSemaforo;
                        if (distanciaRestanteRua <= 0) {
                            System.out.println("FINALIZANDO CARRO " + placa);
                            finalizarCarro();
                        }
                    }

                    System.out.println("Carro " + placa + " se movendo na rua " + ruaNascimento + ". Distancia total da rua: " + distanciaRestanteRua);
                    System.out.println("Distancia para o semaforo: " + distanciaParaSemaforo);
                    if (distanciaRestanteRua == localSemaforo) {
                        AID semaforo = new AID("semaforo_" + ruaNascimento, AID.ISLOCALNAME);
                        ACLMessage pedidoStatus = new ACLMessage(ACLMessage.REQUEST);
                        pedidoStatus.addReceiver(semaforo);
                        pedidoStatus.setContent("STATUS");
                        send(pedidoStatus);
                    }
                }
            }
        });

        // Comportamento assíncrono para processar respostas dos semáforos
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage resposta = receive();
                if (resposta != null) {
                    String estadoSemaforo = resposta.getContent();
                    if (estadoSemaforo.equals("VERDE")) {
                        System.out.println("Carro " + placa + " esta atravessando na rua " + ruaNascimento + " com sinal " + estadoSemaforo);
                        estadoAtual = "ATRAVESSANDO";
                    } else if (estadoSemaforo.equals("VERMELHO")) {
                        if (furaSinal) {
                            System.out.println("Carro " + placa + " esta furando o sinal vermelho na rua " + ruaNascimento);

                            // Enviar a placa para o RadarAgent
                            AID radar = new AID("radar", AID.ISLOCALNAME);
                            ACLMessage msgRadar = new ACLMessage(ACLMessage.INFORM);
                            msgRadar.addReceiver(radar);
                            msgRadar.setContent(placa);
                            send(msgRadar);

                            estadoAtual = "ATRAVESSANDO";
                        } else {
                            System.out.println("Carro " + placa + " parou no semaforo " + ruaNascimento + " com sinal " + estadoSemaforo);
                            estadoAtual = "PARADO";
                        }
                    }
                } else {
                    block();
                }
            }
        });

    }

    private void geraAtributosCarro() {
        this.placa = geraPlaca();
        this.furaSinal = defineSeFuraSinal();
        this.ruaNascimento = defineRua();
        //this.direcao = initialRoad;  // Define a direção inicial do carro
    }

    private String geraPlaca() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder plate = new StringBuilder();

        // Gerar 3 letras aleatórias
        for (int i = 0; i < 3; i++) {
            plate.append(letters.charAt(random.nextInt(letters.length())));
        }

        // Gerar 4 números aleatórios
        for (int i = 0; i < 4; i++) {
            plate.append(random.nextInt(10));
        }

        return plate.toString();
    }

    private boolean defineSeFuraSinal() {
        Random random = new Random();
        return random.nextInt(100) < 30; // 30% de chance de ser infrator
    }

    private String defineRua() {
        String[] roads = {"N", "S", "E", "W"};
        Random random = new Random();
        return roads[random.nextInt(roads.length)];
    }
    
    private void finalizarCarro() {
        ContainerController container = getContainerController();
        // Imprimir mensagem de finalização e deletar o agente
        System.out.println("Carro " + placa + " chegou ao destino e sera removido.");
        this.criarCarro(container);
        doDelete();
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
}
