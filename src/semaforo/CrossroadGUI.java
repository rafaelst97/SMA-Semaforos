
package semaforo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CrossroadGUI extends JFrame {
    private List<CarVisual> cars;
    private HashMap<String, CarVisual> carMap;
    private HashMap<String, TrafficLightVisual> trafficLights;
    private static final int ROAD_WIDTH = 100;
    private static final int WINDOW_SIZE = 600;
    private static final int CAR_SIZE = 20;
    private Agent coordinator;
    private Image bufferImage;
    private Graphics bufferGraphics;

    public CrossroadGUI(Agent coordinator) {
        this.coordinator = coordinator;
        cars = new ArrayList<>();
        carMap = new HashMap<>();
        trafficLights = new HashMap<>();

        setTitle("Simulação de Cruzamento");
        setSize(WINDOW_SIZE, WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializa os semáforos
        trafficLights.put("N", new TrafficLightVisual("N"));
        trafficLights.put("S", new TrafficLightVisual("S"));
        trafficLights.put("E", new TrafficLightVisual("E"));
        trafficLights.put("W", new TrafficLightVisual("W"));

        Timer timer = new Timer(100, e -> moveCars());
        timer.start();
        addBehaviourToUpdateTrafficLights();
        repaint();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferGraphics = bufferImage.getGraphics();
        }
        bufferGraphics.clearRect(0, 0, getWidth(), getHeight());
        bufferGraphics.setColor(Color.GRAY);
        bufferGraphics.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, 0, ROAD_WIDTH, WINDOW_SIZE);
        bufferGraphics.fillRect(0, (WINDOW_SIZE - ROAD_WIDTH) / 2, WINDOW_SIZE, ROAD_WIDTH);
        drawTrafficLights(bufferGraphics);
        for (CarVisual car : cars) {
            car.draw(bufferGraphics);
        }
        g.drawImage(bufferImage, 0, 0, this);
    }

    private void drawTrafficLights(Graphics g) {
        for (String position : trafficLights.keySet()) {
            TrafficLightVisual light = trafficLights.get(position);
            g.setColor(light.getColor());
            if (position.equals("N")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, (WINDOW_SIZE - ROAD_WIDTH) / 2 - 30, ROAD_WIDTH, 20);
            } else if (position.equals("S")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, (WINDOW_SIZE + ROAD_WIDTH) / 2 + 10, ROAD_WIDTH, 20);
            } else if (position.equals("E")) {
                g.fillRect((WINDOW_SIZE + ROAD_WIDTH) / 2 + 10, (WINDOW_SIZE - ROAD_WIDTH) / 2, 20, ROAD_WIDTH);
            } else if (position.equals("W")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2 - 30, (WINDOW_SIZE - ROAD_WIDTH) / 2, 20, ROAD_WIDTH);
            }
        }
    }

    public void updateTrafficLight(String position, String state) {
        System.out.println("ATUALIZANDO STATUS " + position + " - " + state);
        TrafficLightVisual light = trafficLights.get(position);
        if (light != null) {
            light.setState(state);
            repaint();
        }
    }

    public void updateCar(String content) {
        String[] data = content.split(";");
        String plate = data[0];
        String direction = data[1];

        if (!carMap.containsKey(plate)) {
            CarVisual car = new CarVisual(plate, direction, this);
            cars.add(car);
            carMap.put(plate, car);
            System.out.println("Carro adicionado à GUI: " + plate);
        }
    }

    private void moveCars() {
        for (int i = cars.size() - 1; i >= 0; i--) {
            CarVisual car = cars.get(i);
            car.move();
            if (car.isOutOfBounds()) {
                cars.remove(i);
                carMap.remove(car.getPlate());
                car.terminateAgent(); // Finaliza o agente do carro
            }
        }
        repaint(); // Atualiza a tela
    }

    private void addBehaviourToUpdateTrafficLights() {
        coordinator.addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = coordinator.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("CONTEUDO DA MENSAGEM: " + content);
                    String[] parts = content.split(";");
                    if (parts.length == 2 && isTrafficLightMessage(parts)) {
                        System.out.println("SEMÁFORO");
                        String position = parts[0];
                        String state = parts[1];
                        updateTrafficLight(position, state);
                    } else if (parts.length == 2) {
                        System.out.println("CARRO");
                        updateCar(content);
                    } else {
                        System.err.println("Mensagem desconhecida: " + content);
                    }
                } else {
                    block();
                }
            }

            private boolean isTrafficLightMessage(String[] parts) {
                return parts[0].matches("[NSEW]"); // Verifica se a posição é N, S, E ou W
            }
        });
    }
}
