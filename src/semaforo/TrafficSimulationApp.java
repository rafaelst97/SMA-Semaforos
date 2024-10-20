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
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class TrafficSimulationApp {
    public static void main(String[] args) {
        // Inicializar a interface gráfica
        TrafficIntersectionGUI gui = new TrafficIntersectionGUI();
        gui.setVisible(true);  // Certifique-se de que a GUI esteja visível

        // Inicializar o ambiente JADE
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        AgentContainer mainContainer = rt.createMainContainer(p);

        try {
            // Iniciar o agente coordenador
            AgentController coordinator = mainContainer.createNewAgent(
                    "Semaforo", 
                    "semaforo.CoordinatorAgent", 
                    null
            );
            coordinator.start();

            // Iniciar os agentes de semáforo para cada direção (N, S, E, W)
            String[] directions = {"N", "S", "E", "W"};
            for (String direction : directions) {
                Object[] trafficLightArgs = new Object[]{direction, gui};
                AgentController trafficLight = mainContainer.createNewAgent(
                        direction + "_Semaforo", 
                        "semaforo.TrafficLightAgent", 
                        trafficLightArgs
                );
                trafficLight.start();
            }

            // Iniciar o agente de radar
            AgentController radar = mainContainer.createNewAgent(
                    "Radar", 
                    "semaforo.RadarAgent", 
                    null
            );
            radar.start();

            // Iniciar alguns agentes de carro (opcional, para simulação)
            for (int i = 1; i <= 3; i++) {
                AgentController car = mainContainer.createNewAgent(
                        "Carro" + i, 
                        "semaforo.CarAgent", 
                        null
                );
                car.start();
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
