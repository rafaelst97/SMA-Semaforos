/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javax.swing.*;

public class TrafficSimulationApp {

    private static final int NUM_CARS = 10; // Número inicial de carros

    public static void main(String[] args) {
        // Inicializa o JADE e a GUI
        startJade();
        startGUI();
    }

    private static void startJade() {
        // Configurações do contêiner principal do JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        ContainerController container = rt.createMainContainer(p);

        try {
            // Inicializa o agente coordenador
            AgentController coordinatorAgent = container.createNewAgent("CoordinatorAgent", "CoordinatorAgent", null);
            coordinatorAgent.start();

            // Inicializa o agente de semáforo
            AgentController trafficLightAgent = container.createNewAgent("TrafficLightAgent", "TrafficLightAgent", null);
            trafficLightAgent.start();

            // Inicializa o agente radar
            AgentController radarAgent = container.createNewAgent("RadarAgent", "RadarAgent", null);
            radarAgent.start();

            // Inicializa os agentes de carros
            for (int i = 0; i < NUM_CARS; i++) {
                String carName = "CarAgent_" + i;
                AgentController carAgent = container.createNewAgent(carName, "CarAgent", null);
                carAgent.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    private static void startGUI() {
        // Inicializa a interface gráfica Swing para o cruzamento
        SwingUtilities.invokeLater(() -> {
            TrafficIntersectionGUI gui = new TrafficIntersectionGUI();
            gui.setVisible(true);
        });
    }
}
