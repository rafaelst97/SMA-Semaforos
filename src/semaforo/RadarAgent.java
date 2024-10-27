/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.LinkedList;
import java.util.Queue;

public class RadarAgent extends Agent {
    private Queue<String> infractions = new LinkedList<>();
    private final int MAX_SIZE = 30;

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String plate = msg.getContent();
                    if (infractions.size() >= MAX_SIZE) {
                        infractions.poll(); // Remove o mais antigo
                    }
                    infractions.add(plate);
                    System.out.println("Infração registrada: " + plate);
                } else {
                    block();
                }
            }
        });
    }
}
