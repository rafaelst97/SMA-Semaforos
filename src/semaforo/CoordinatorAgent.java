/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class CoordinatorAgent extends Agent {
    private static final long serialVersionUID = 1L;

    // Armazenar o número de carros em espera para cada semáforo
    private final Map<String, Integer> waitingCarsMap = new HashMap<>();
    // Fila de prioridade para gerenciar semáforos que precisam de abertura prioritária
    private final Queue<String> priorityQueue = new PriorityQueue<>();
    private String currentGreenLight = null; // Semáforo atualmente aberto

    @Override
    protected void setup() {
        System.out.println("CoordinatorAgent " + getLocalName() + " iniciado.");

        // Inicializar o número de carros para cada direção
        waitingCarsMap.put("N", 0);
        waitingCarsMap.put("S", 0);
        waitingCarsMap.put("E", 0);
        waitingCarsMap.put("W", 0);

        // Comportamento cíclico para receber mensagens dos semáforos e radares
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                // Template para mensagens de semáforo
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(mt);
                
                if (msg != null) {
                    // Processar a mensagem recebida
                    String content = msg.getContent();
                    String[] parts = content.split(":");
                    
                    if (parts[0].equals("TRAFFIC_LIGHT")) {
                        String direction = parts[1];
                        boolean isGreen = parts[2].equals("GREEN");
                        int carsWaiting = Integer.parseInt(parts[4]);
                        
                        // Atualizar o número de carros esperando
                        waitingCarsMap.put(direction, carsWaiting);

                        // Verificar se é necessária prioridade para este semáforo
                        if (carsWaiting >= 3 && !priorityQueue.contains(direction)) {
                            priorityQueue.add(direction);
                        }

                        // Tomar decisão sobre qual semáforo deve abrir
                        decideNextGreenLight();
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Método para decidir qual semáforo deve abrir
    private void decideNextGreenLight() {
        // Se há um semáforo em prioridade, abrir esse
        if (!priorityQueue.isEmpty()) {
            String nextGreen = priorityQueue.poll();
            if (!nextGreen.equals(currentGreenLight)) {
                sendGreenLightCommand(nextGreen);
            }
        } else {
            // Ciclo normal de alternância
            String[] directions = {"N", "E", "S", "W"};
            for (String direction : directions) {
                if (!direction.equals(currentGreenLight)) {
                    sendGreenLightCommand(direction);
                    break;
                }
            }
        }
    }

    // Enviar comando para abrir o semáforo
    private void sendGreenLightCommand(String direction) {
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(getAID(direction + "_Semaforo"));
        msg.setContent("OPEN");
        send(msg);

        // Notificar o radar para limpar a fila de carros
        ACLMessage clearMsg = new ACLMessage(ACLMessage.INFORM);
        clearMsg.addReceiver(getAID("Radar"));
        clearMsg.setContent("CLEAR_CARS:" + direction);
        send(clearMsg);

        // Atualizar o semáforo atualmente aberto
        currentGreenLight = direction;
    }
    
}

