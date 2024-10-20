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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {
    private static final long serialVersionUID = 1L;

    // Estado do semáforo
    private boolean isGreen = false;
    private int waitingCars = 0; // Número de carros esperando no semáforo
    private String direction; // Direção do semáforo (N, S, E, W)

    // Referência à interface gráfica
    private TrafficIntersectionGUI gui;

    // Tempo de abertura e fechamento (em milissegundos)
    private static final int GREEN_DURATION = 3000;
    private static final int RED_DURATION = 3000;

    @Override
    protected void setup() {
        // Inicializar a direção do semáforo com base nos argumentos
        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            direction = (String) args[0];
            gui = (TrafficIntersectionGUI) args[1]; // Recebe a GUI como argumento
        } else {
            direction = "UNKNOWN";
        }

        // Adicionar comportamento cíclico para alternar o semáforo
        addBehaviour(new TickerBehaviour(this, GREEN_DURATION + RED_DURATION) {
            protected void onTick() {
                // Alternar o semáforo
                if (waitingCars >= 3) {
                    // Prioridade para este semáforo se houver 3 ou mais carros
                    setGreenLight(true);
                } else {
                    // Alternância normal do semáforo
                    setGreenLight(!isGreen);
                }
                
                // Enviar status do semáforo para o agente coordenador
                sendStatusToCoordinator();
            }
        });

        // Comportamento cíclico para receber mensagens de outros agentes
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    if (content.equals("OPEN")) {
                        setGreenLight(true);
                        waitingCars = 0; // Zerar a contagem de carros

                        // Enviar mensagem para o RadarAgent para limpar a fila
                        ACLMessage clearMsg = new ACLMessage(ACLMessage.INFORM);
                        clearMsg.addReceiver(getAID("Radar"));
                        clearMsg.setContent("CLEAR_CARS:" + direction);
                        send(clearMsg);
                    } else if (content.startsWith("CAR_WAITING")) {
                        waitingCars++;
                    }
                } else {
                    block();
                }
            }
        });

    }

    // Método para definir o estado do semáforo
    private void setGreenLight(boolean green) {
        isGreen = green;
        if (gui != null) {
            gui.updateTrafficLight(direction, isGreen);
            gui.updateCarCount(direction, waitingCars);
        }
        // Se o semáforo estiver verde, limpar a fila de carros
        if (isGreen) {
            waitingCars = 0;
        }
    }

    // Enviar status para o agente coordenador
    private void sendStatusToCoordinator() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(getAID("CoordinatorAgent"));
        msg.setContent("TRAFFIC_LIGHT:" + direction + ":" + (isGreen ? "GREEN" : "RED") + ":CARS:" + waitingCars);
        send(msg);
    }
}
