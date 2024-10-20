/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrafficIntersectionGUI extends JFrame {

    private JLabel[] trafficLights; // Labels para os semáforos
    private JTextArea logArea; // Área de log para mostrar infrações e eventos
    private List<JLabel> cars; // Lista de carros na interface
    private Random random; // Gerador de números aleatórios para posições dos carros

    public TrafficIntersectionGUI() {
        setTitle("Simulação de Cruzamento");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null); // Layout nulo para controle manual

        random = new Random();
        cars = new ArrayList<>();

        // Painel central para representar o cruzamento
        JPanel intersectionPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawIntersection(g);
            }
        };
        intersectionPanel.setBounds(100, 100, 400, 400);
        add(intersectionPanel);

        // Inicializa os semáforos com posicionamento manual
        trafficLights = new JLabel[4];

        // Semáforo Norte
        trafficLights[0] = createTrafficLightLabel("Vermelho");
        trafficLights[0].setBounds(275, 50, 50, 20);
        add(trafficLights[0]);

        // Semáforo Sul
        trafficLights[1] = createTrafficLightLabel("Vermelho");
        trafficLights[1].setBounds(275, 500, 50, 20);
        add(trafficLights[1]);

        // Semáforo Leste
        trafficLights[2] = createTrafficLightLabel("Vermelho");
        trafficLights[2].setBounds(500, 275, 50, 20);
        add(trafficLights[2]);

        // Semáforo Oeste
        trafficLights[3] = createTrafficLightLabel("Vermelho");
        trafficLights[3].setBounds(50, 275, 50, 20);
        add(trafficLights[3]);

        // Painel inferior para o log de infrações
        logArea = new JTextArea();
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBounds(0, 550, 600, 50);
        add(logScrollPane);

        // Timer para criar carros automaticamente
        Timer carTimer = new Timer(2000, e -> addCar());
        carTimer.start();

        // Timer para mover os carros
        Timer moveTimer = new Timer(500, e -> moveCars());
        moveTimer.start();
    }

    // Método para criar o JLabel dos semáforos
    private JLabel createTrafficLightLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(Color.RED);
        label.setForeground(Color.WHITE);
        return label;
    }

    // Método para desenhar o cruzamento
    private void drawIntersection(Graphics g) {
        g.setColor(Color.GRAY);
        // Estradas verticais e horizontais formando um "+"
        g.fillRect(250, 0, 100, 600); // Estrada vertical
        g.fillRect(0, 250, 600, 100); // Estrada horizontal
    }

    // Atualiza o estado do semáforo
    public void updateTrafficLight(int lane, boolean isGreen) {
        if (lane >= 1 && lane <= 4) {
            trafficLights[lane - 1].setText(isGreen ? "Verde" : "Vermelho");
            trafficLights[lane - 1].setBackground(isGreen ? Color.GREEN : Color.RED);
        }
    }

    // Adiciona um log de infração à área de log
    public void addInfractionLog(String plate, int lane) {
        logArea.append("Infração: Carro " + plate + " na via " + lane + " - Sinal vermelho\n");
    }

    // Adiciona um carro ao cruzamento
    private void addCar() {
        JLabel car = new JLabel("Carro");
        car.setOpaque(true);
        car.setBackground(Color.BLUE);
        car.setForeground(Color.WHITE);
        car.setBounds(random.nextInt(500), random.nextInt(500), 50, 20);
        cars.add(car);
        add(car);
        repaint();
    }

    // Move os carros no cruzamento
    private void moveCars() {
        for (JLabel car : cars) {
            int x = car.getX();
            int y = car.getY();

            // Movimento simples: desce ou move para a direita
            if (y < 500) {
                car.setLocation(x, y + 5);
            } else if (x < 500) {
                car.setLocation(x + 5, y);
            }

            // Se o carro sair da tela, removê-lo
            if (x > 550 || y > 550) {
                remove(car);
            }
        }
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficIntersectionGUI gui = new TrafficIntersectionGUI();
            gui.setVisible(true);
        });
    }
}
