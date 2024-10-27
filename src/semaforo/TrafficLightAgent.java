
package semaforo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {
    private String position; // Posição do semáforo (N, S, E, W)
    private String state; // Estado do semáforo (verde, amarelo, vermelho)
    private final int greenDuration = 3000; // Duração do verde em milissegundos
    private final int yellowDuration = 1000; // Duração do amarelo
    private final int redDuration = 3000; // Duração do vermelho
    private AID coordinatorAID; // AID do CoordinatorAgent

    @Override
    protected void setup() {
        // Recebe a posição do semáforo na inicialização
        Object[] args = getArguments();
        position = (args != null && args.length > 0) ? (String) args[0] : "N"; // Padrão para Norte
        state = "vermelho"; // Inicialmente vermelho

        // Obtém o AID do coordenador (assumindo que é passado como argumento)
        if (args != null && args.length > 1) {
            coordinatorAID = (AID) args[1];
        }

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
                // Envia o estado atualizado do semáforo para o coordenador
                if (coordinatorAID != null) {
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.setContent(position + ";" + state);
                    msg.addReceiver(coordinatorAID);
                    send(msg);
                    System.out.println("Semaforo: " + position + " ficou " + state);
                } else {
                    System.err.println("AID do coordenador não definido para o agente de semáforo.");
                }
            }
        });
    }
}
