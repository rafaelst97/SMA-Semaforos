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
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javax.swing.SwingUtilities;

public class CoordinatorAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("CoordinatorAgent iniciado.");

        // Inicializa a GUI na thread de eventos do Swing
        SwingUtilities.invokeLater(() -> {
            TrafficIntersectionGUI gui = new TrafficIntersectionGUI();
            gui.setVisible(true);
        });

        // Comportamento para criar outros agentes
        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                createAgents();
            }
        });

        // Comportamento para gerenciar mensagens recebidas
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("CoordinatorAgent recebeu: " + content);

                    // Verifica se a mensagem é sobre o semáforo
                    if (content.startsWith("SEMAFORO_")) {
                        String[] parts = content.split(" ");
                        String direction = parts[0].replace("SEMAFORO_", "");
                        String state = parts[1];
                        System.out.println("Semáforo " + direction + " agora está " + state);
                    }

                    // Verifica se a mensagem é sobre o movimento do carro
                    if (content.startsWith("VERIFICAR_SEMAFORO")) {
                        String[] parts = content.split(" ");
                        int lane = Integer.parseInt(parts[1]);

                        // Responde ao carro com o estado do semáforo (simulação simples)
                        ACLMessage response = msg.createReply();
                        response.setContent("VERDE"); // Apenas para testes, ajuste conforme necessário
                        send(response);
                        System.out.println("CoordinatorAgent respondeu com 'VERDE' para a via " + lane);
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Método para criar outros agentes
    private void createAgents() {
        ContainerController container = getContainerController();

        try {
            // Criar agentes de semáforo
            AgentController nTrafficLight = container.createNewAgent("N_TrafficLight", "semaforo.TrafficLightAgent", new Object[]{"N"});
            nTrafficLight.start();
            System.out.println("Agente N_TrafficLight criado.");

            AgentController sTrafficLight = container.createNewAgent("S_TrafficLight", "semaforo.TrafficLightAgent", new Object[]{"S"});
            sTrafficLight.start();
            System.out.println("Agente S_TrafficLight criado.");

            AgentController eTrafficLight = container.createNewAgent("E_TrafficLight", "semaforo.TrafficLightAgent", new Object[]{"E"});
            eTrafficLight.start();
            System.out.println("Agente E_TrafficLight criado.");

            AgentController wTrafficLight = container.createNewAgent("W_TrafficLight", "semaforo.TrafficLightAgent", new Object[]{"W"});
            wTrafficLight.start();
            System.out.println("Agente W_TrafficLight criado.");

            // Criar agente radar
            AgentController radarAgent = container.createNewAgent("Radar", "semaforo.RadarAgent", null);
            radarAgent.start();
            System.out.println("Agente Radar criado.");

            // Criar agentes de carro
            for (int i = 1; i <= 3; i++) {
                AgentController carAgent = container.createNewAgent("Car" + i, "semaforo.CarAgent", null);
                carAgent.start();
                System.out.println("Agente Car" + i + " criado.");
            }

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
