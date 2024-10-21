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

public class CrossroadGUI extends JFrame {
    private List<CarVisual> cars;  // Lista de carros visuais
    private HashMap<String, CarVisual> carMap; // Mapa para controle de carros por placa
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

        setTitle("Simulação de Cruzamento");
        setSize(WINDOW_SIZE, WINDOW_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Timer para mover os carros a cada 100 ms
        Timer timer = new Timer(100, e -> moveCars());
        timer.start();
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
