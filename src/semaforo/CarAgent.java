/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class CarAgent extends Agent {

    private String plate; // Placa do carro
    private boolean isOffender; // Define se o carro é um infrator
    private int currentLane; // Via atual do carro
    private Random random;

    @Override
    protected void setup() {
        random = new Random();
        
        // Gera uma placa aleatória para o carro
        plate = generateRandomPlate();

        // Define aleatoriamente se o carro é um infrator
        isOffender = random.nextBoolean();

        // Define aleatoriamente a via em que o carro será gerado
        currentLane = random.nextInt(4) + 1;

        System.out.println("Carro criado: " + plate + " na via " + currentLane +
                (isOffender ? " (Infrator)" : " (Respeita Sinal)"));

        // Comportamento de movimento do carro
        addBehaviour(new CarMovementBehaviour(this, 2000)); // Verifica o movimento a cada 2 segundos
    }

    private String generateRandomPlate() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder plate = new StringBuilder();

        // Gera 3 letras aleatórias
        for (int i = 0; i < 3; i++) {
            plate.append(letters.charAt(random.nextInt(letters.length())));
        }

        // Gera 4 dígitos aleatórios
        plate.append("-");
        for (int i = 0; i < 4; i++) {
            plate.append(random.nextInt(10));
        }

        return plate.toString();
    }

    // Comportamento do movimento do carro
    private class CarMovementBehaviour extends TickerBehaviour {
        public CarMovementBehaviour(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            // Envia mensagem para o agente coordenador para verificar o semáforo da via atual
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(getAID("Coordinator"));
            msg.setContent("VERIFICAR_SEMAFORO " + currentLane);
            send(msg);

            // Recebe a resposta do semáforo
            ACLMessage response = receive();
            if (response != null) {
                String content = response.getContent();
                if (content.equals("VERDE") || (content.equals("VERMELHO") && isOffender)) {
                    // Se o semáforo estiver verde ou o carro for infrator, ele passa
                    System.out.println(plate + " está passando pela via " + currentLane);

                    // Notifica o radar se passar no sinal vermelho
                    if (content.equals("VERMELHO") && isOffender) {
                        ACLMessage radarMsg = new ACLMessage(ACLMessage.INFORM);
                        radarMsg.addReceiver(getAID("Radar"));
                        radarMsg.setContent("INFRAÇÃO " + plate + " via " + currentLane);
                        send(radarMsg);
                    }

                    // Move o carro para uma nova via aleatória após passar
                    currentLane = random.nextInt(4) + 1;
                } else {
                    // Se o semáforo estiver vermelho e o carro não for infrator, ele aguarda
                    System.out.println(plate + " aguardando na via " + currentLane);
                }
            }
        }
    }
}
