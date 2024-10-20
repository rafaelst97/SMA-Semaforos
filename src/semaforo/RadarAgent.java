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

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.HashMap;
import java.util.Map;

public class RadarAgent extends Agent {
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> carCounts = new HashMap<>();
    private static final int MONITOR_INTERVAL = 1000;

    @Override
    protected void setup() {
        System.out.println("RadarAgent " + getLocalName() + " iniciado.");
        carCounts.put("N", 0);
        carCounts.put("S", 0);
        carCounts.put("E", 0);
        carCounts.put("W", 0);

        addBehaviour(new TickerBehaviour(this, MONITOR_INTERVAL) {
            protected void onTick() {
                simulateCarCount();
                sendCarCountToCoordinator();
            }
        });

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(mt);
                if (msg != null) {
                    String content = msg.getContent();
                    if (content.startsWith("CLEAR_CARS")) {
                        String direction = content.split(":")[1];
                        carCounts.put(direction, 0); // Zerar o número de carros na direção especificada
                        System.out.println("RadarAgent limpou a contagem de carros na direção: " + direction);
                    }
                } else {
                    block();
                }
            }
        });

    }

    private void simulateCarCount() {
        for (String direction : carCounts.keySet()) {
            int currentCount = carCounts.get(direction);
            carCounts.put(direction, currentCount + (int) (Math.random() * 3));
        }
    }

    private void sendCarCountToCoordinator() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(getAID("CoordinatorAgent"));
        StringBuilder content = new StringBuilder("RADAR_UPDATE:");
        for (Map.Entry<String, Integer> entry : carCounts.entrySet()) {
            content.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }
        msg.setContent(content.toString());
        send(msg);
        System.out.println("RadarAgent enviou atualização para o coordenador: " + content);
    }
}
