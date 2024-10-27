/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.util.Random;

public class CarAgent extends Agent {
    private String placa;
    private boolean furaSinal;
    private String ruaNascimento;
    private String direcao;

    @Override
    protected void setup() {
        
        this.geraAtributosCarro();
        System.out.println("Gerado carro com placa " + placa);
        System.out.println("Ele vai furar sinal? " + furaSinal);
        System.out.println("Rua que nasce " + ruaNascimento);
        
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                // Enviar mensagem para o semáforo da rua atual pedindo status
                AID semaforo = new AID("semaforo_" + ruaNascimento, AID.ISLOCALNAME);
                ACLMessage pedidoStatus = new ACLMessage(ACLMessage.REQUEST);
                pedidoStatus.addReceiver(semaforo);
                pedidoStatus.setContent("STATUS");
                send(pedidoStatus);

                // Esperar pela resposta do semáforo
                ACLMessage resposta = blockingReceive(500);
                if (resposta != null) {
                    String estadoSemaforo = resposta.getContent();
                    if (estadoSemaforo.equals("VERDE") || (estadoSemaforo.equals("VERMELHO") && furaSinal)) {
                        System.out.println("Carro " + placa + " esta avancando na rua " + ruaNascimento + " mesmo com sinal " + estadoSemaforo);
                        // Lógica para mover o carro para a próxima rua ou destino
                    } else if (estadoSemaforo.equals("VERMELHO")) {
                        System.out.println("Carro " + placa + " esta parado na rua " + ruaNascimento + " no sinal " + estadoSemaforo);
                    }
                } else {
                    System.out.println("Carro " + placa + " nao recebeu resposta do semaforo " + ruaNascimento);
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
}
