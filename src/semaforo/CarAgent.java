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
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class CarAgent extends Agent {
    private boolean willBreakRedLight;

    @Override
    protected void setup() {
        // Define se o carro irá furar o sinal vermelho
        willBreakRedLight = Math.random() > 0.8; // 20% de chance de furar o sinal
        
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    
                    if (content.equals("RED")) {
                        if (willBreakRedLight) {
                            System.out.println(getLocalName() + " furou o sinal vermelho!");
                            // Notifica o radar
                            ACLMessage radarMsg = new ACLMessage(ACLMessage.INFORM);
                            radarMsg.addReceiver(getAID("RadarAgent"));
                            radarMsg.setContent("FURTOU_SINAL");
                            send(radarMsg);
                        } else {
                            System.out.println(getLocalName() + " parou no sinal vermelho.");
                        }
                    } else if (content.equals("GREEN")) {
                        System.out.println(getLocalName() + " passou no sinal verde.");
                    }
                } else {
                    block();
                }
            }
        });
    }
}

