/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CrossroadGUI extends JFrame {
    private List<CarVisual> cars;  // Lista de carros visuais
    private HashMap<String, CarVisual> carMap; // Mapa para controle de carros por placa
    private HashMap<String, Color> trafficLights; // Mapeamento de semáforos por posição
    private static final int ROAD_WIDTH = 100;
    private static final int WINDOW_SIZE = 600;
    private static final int CAR_SIZE = 20;
    private Agent coordinator; // Referência para o agente coordenador
    private Image bufferImage; // Imagem de buffer
    private Graphics bufferGraphics; // Gráficos de buffer

    public CrossroadGUI(Agent coordinator) {
        this.coordinator = coordinator;
        cars = new ArrayList<>();
        carMap = new HashMap<>();
        trafficLights = new HashMap<>();

        setTitle("Simulação de Cruzamento");
        setSize(WINDOW_SIZE, WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Inicializa os semáforos com estado vermelho
        trafficLights.put("N", Color.RED);
        trafficLights.put("S", Color.RED);
        trafficLights.put("E", Color.RED);
        trafficLights.put("W", Color.RED);

        // Timer para mover os carros a cada 100 ms
        Timer timer = new Timer(100, e -> moveCars());
        timer.start();

        // Comportamento para atualizar semáforos baseado em mensagens de agentes
        addBehaviourToUpdateTrafficLights();
    }

    @Override
    public void update(Graphics g) {
        paint(g);
    }

    @Override
    public void paint(Graphics g) {
        // Cria o buffer para evitar a cintilação
        if (bufferImage == null) {
            bufferImage = createImage(getWidth(), getHeight());
            bufferGraphics = bufferImage.getGraphics();
        }

        // Limpa o buffer
        bufferGraphics.clearRect(0, 0, getWidth(), getHeight());

        // Desenhar as estradas
        bufferGraphics.setColor(Color.GRAY);
        bufferGraphics.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, 0, ROAD_WIDTH, WINDOW_SIZE); // Vertical
        bufferGraphics.fillRect(0, (WINDOW_SIZE - ROAD_WIDTH) / 2, WINDOW_SIZE, ROAD_WIDTH); // Horizontal

        // Desenhar as barras de semáforos próximas ao cruzamento
        drawTrafficLights(bufferGraphics);

        // Desenhar os carros
        for (CarVisual car : cars) {
            car.draw(bufferGraphics);
        }

        // Renderiza o buffer na tela
        g.drawImage(bufferImage, 0, 0, this);
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

    private void drawTrafficLights(Graphics g) {
        // Desenha a barra de semáforo para cada posição próxima ao cruzamento
        for (String position : trafficLights.keySet()) {
            g.setColor(trafficLights.get(position));
            if (position.equals("N")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, (WINDOW_SIZE - ROAD_WIDTH) / 2 - 30, ROAD_WIDTH, 20); // Barra no norte
            } else if (position.equals("S")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2, (WINDOW_SIZE + ROAD_WIDTH) / 2 + 10, ROAD_WIDTH, 20); // Barra no sul
            } else if (position.equals("E")) {
                g.fillRect((WINDOW_SIZE + ROAD_WIDTH) / 2 + 10, (WINDOW_SIZE - ROAD_WIDTH) / 2, 20, ROAD_WIDTH); // Barra no leste
            } else if (position.equals("W")) {
                g.fillRect((WINDOW_SIZE - ROAD_WIDTH) / 2 - 30, (WINDOW_SIZE - ROAD_WIDTH) / 2, 20, ROAD_WIDTH); // Barra no oeste
            }
        }
    }

    public void updateCar(String content) {
        // Conteúdo esperado: "placa;direção"
        String[] data = content.split(";");
        String plate = data[0];
        String direction = data[1];

        // Verifica se o carro já está na interface
        if (!carMap.containsKey(plate)) {
            CarVisual car = new CarVisual(plate, direction, this);
            cars.add(car);
            carMap.put(plate, car);
            System.out.println("Carro adicionado à GUI: " + plate);
        }
    }

    public void addCar(CarVisual car) {
        cars.add(car);
        carMap.put(car.getPlate(), car);
    }

    // Método para atualizar o estado do semáforo
    public void updateTrafficLight(String position, String state) {
        switch (state) {
            case "verde":
                trafficLights.put(position, Color.GREEN);
                break;
            case "amarelo":
                trafficLights.put(position, Color.YELLOW);
                break;
            case "vermelho":
                trafficLights.put(position, Color.RED);
                break;
        }
        repaint(); // Atualiza a tela
    }

    // Comportamento para atualizar os semáforos baseado nas mensagens recebidas dos agentes de semáforo
    private void addBehaviourToUpdateTrafficLights() {
        Thread t = new Thread(() -> {
            while (true) {
                ACLMessage msg = coordinator.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                if (msg != null) {
                    String[] content = msg.getContent().split(";");
                    String position = content[0];
                    String state = content[1];
                    updateTrafficLight(position, state);
                }
            }
        });
        t.start();
    }

    public static void main(String[] args) {
        // Criação de um agente coordenador de teste (exemplo, precisa de execução JADE)
        Agent coordinator = new Agent();
        CrossroadGUI gui = new CrossroadGUI(coordinator);
        gui.setVisible(true);

        // Exemplo de teste de atualização de carros na GUI
        gui.updateCar("CAR1234;N");
        gui.updateCar("CAR5678;S");
    }
}
