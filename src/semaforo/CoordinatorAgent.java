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
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class CoordinatorAgent extends Agent {
    private int carCounter = 0;
    private ContainerController container;

    @Override
    protected void setup() {
        container = getContainerController();
        addBehaviour(new CarGenerator(this, 3000)); // Gera carros a cada 3 segundos
        addBehaviour(new TrafficLightController(this, 5000)); // Alterna semáforos a cada 5 segundos
    }

    private class CarGenerator extends TickerBehaviour {
        public CarGenerator(Agent a, long interval) {
            super(a, interval);
        }

        @Override
        protected void onTick() {
            try {
                carCounter++;
                AgentController car = container.createNewAgent("Car" + carCounter, "CarAgent", null);
                car.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class TrafficLightController extends TickerBehaviour {
        private int currentGreenLight = 0;

        public TrafficLightController(Agent a, long interval) {
            super(a, interval);
        }

        @Override
        protected void onTick() {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            for (int i = 0; i < 4; i++) {
                msg.addReceiver(getAID("TrafficLight" + i));
                if (i == currentGreenLight) {
                    msg.setContent("GREEN");
                } else {
                    msg.setContent("RED");
                }
                send(msg);
            }

            // Atualiza o semáforo na interface gráfica
            ((TrafficIntersectionGUI) getArguments()[0]).updateTrafficLight(currentGreenLight, true);
            currentGreenLight = (currentGreenLight + 1) % 4;
        }
    }
}
