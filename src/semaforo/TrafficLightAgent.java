/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.awt.Color;

public class TrafficLightAgent extends Agent {
    private String position; // Posição do semáforo (N, S, E, W)
    private String state; // Estado do semáforo (verde, amarelo, vermelho)
    private final int greenDuration = 3000; // Duração do verde em milissegundos
    private final int yellowDuration = 1000; // Duração do amarelo
    private final int redDuration = 3000; // Duração do vermelho

    @Override
    protected void setup() {
        // Recebe a posição do semáforo na inicialização
        Object[] args = getArguments();
        position = (args != null && args.length > 0) ? (String) args[0] : "N"; // Padrão para Norte
        state = "vermelho"; // Inicialmente vermelho

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                switch (state) {
                    case "verde":
                        state = "amarelo";
                        block(yellowDuration);
                        break;
                    case "amarelo":
                        state = "vermelho";
                        block(redDuration);
                        break;
                    case "vermelho":
                        state = "verde";
                        block(greenDuration);
                        break;
                }
                // Envia o estado atual para a GUI atualizar a barra
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(state);
                msg.addReceiver(new jade.core.AID("CrossroadGUI", AID.ISLOCALNAME));
                send(msg);
            }
        });
    }
}
