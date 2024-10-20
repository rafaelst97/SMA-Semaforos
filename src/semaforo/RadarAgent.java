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
import java.util.ArrayList;

public class RadarAgent extends Agent {
    private ArrayList<String> infractors;

    @Override
    protected void setup() {
        infractors = new ArrayList<>();

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("FURTOU_SINAL")) {
                        String infractorPlate = msg.getSender().getLocalName();
                        infractors.add(infractorPlate);
                        System.out.println("Carro " + infractorPlate + " furou o sinal vermelho.");
                        updateUI();
                    }
                } else {
                    block();
                }
            }
        });
    }

    private void updateUI() {
        // Atualiza a interface gráfica em tempo real
        System.out.println("Infratores: " + infractors);
    }
}
