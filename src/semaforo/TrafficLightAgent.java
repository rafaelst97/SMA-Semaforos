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

public class TrafficLightAgent extends Agent {
    private boolean isGreen;

    @Override
    protected void setup() {
        isGreen = false;

        addBehaviour(new TickerBehaviour(this, 5000) { // Alterna a cada 5 segundos
            @Override
            protected void onTick() {
                isGreen = !isGreen;
                String status = isGreen ? "GREEN" : "RED";
                
                // Notifica os carros sobre o status do semáforo
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(status);
                msg.addReceiver(getAID("CarAgent"));
                send(msg);
                
                System.out.println(getLocalName() + " está " + status);
            }
        });
    }
}
