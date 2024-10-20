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

    private String direction;
    private boolean isGreen; // Estado atual do semáforo

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            direction = (String) args[0];
        }

        isGreen = false;
        System.out.println("TrafficLightAgent iniciado para a direção " + direction);

        // Comportamento de alternância do semáforo
        addBehaviour(new TickerBehaviour(this, 3000) {
            @Override
            protected void onTick() {
                // Alterna o estado do semáforo
                isGreen = !isGreen;
                String state = isGreen ? "VERDE" : "VERMELHO";
                System.out.println("Semáforo " + direction + " agora está " + state);

                // Notifica o coordenador sobre o estado do semáforo
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(getAID("Coordinator"));
                msg.setContent("SEMAFORO_" + direction + " " + state);
                send(msg);
            }
        });
    }
}
