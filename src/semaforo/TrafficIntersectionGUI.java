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
import java.util.HashMap;
import java.util.Map;

public class TrafficIntersectionGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, JLabel> trafficLights;
    private final Map<String, JLabel> carCounters;

    public TrafficIntersectionGUI() {
        setTitle("Simulação de Cruzamento de Tráfego");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        trafficLights = new HashMap<>();
        carCounters = new HashMap<>();
        
        JPanel intersectionPanel = new JPanel();
        intersectionPanel.setLayout(new GridLayout(2, 2));

        // Criar as áreas de cada direção
        createTrafficArea(intersectionPanel, "N");
        createTrafficArea(intersectionPanel, "S");
        createTrafficArea(intersectionPanel, "E");
        createTrafficArea(intersectionPanel, "W");

        add(intersectionPanel, BorderLayout.CENTER);
    }

    private void createTrafficArea(JPanel panel, String direction) {
        JPanel areaPanel = new JPanel();
        areaPanel.setLayout(new BorderLayout());

        JLabel lightLabel = new JLabel("RED", SwingConstants.CENTER);
        lightLabel.setOpaque(true);
        lightLabel.setBackground(Color.RED);
        areaPanel.add(lightLabel, BorderLayout.NORTH);
        trafficLights.put(direction, lightLabel);
        
        JLabel counterLabel = new JLabel("Cars: 0", SwingConstants.CENTER);
        areaPanel.add(counterLabel, BorderLayout.SOUTH);
        carCounters.put(direction, counterLabel);

        panel.add(areaPanel);
    }

    // Método para atualizar o estado do semáforo
    public void updateTrafficLight(String direction, boolean isGreen) {
        JLabel lightLabel = trafficLights.get(direction);
        if (lightLabel != null) {
            lightLabel.setText(isGreen ? "GREEN" : "RED");
            lightLabel.setBackground(isGreen ? Color.GREEN : Color.RED);
        }
    }

    // Método para atualizar a contagem de carros
    public void updateCarCount(String direction, int count) {
        JLabel counterLabel = carCounters.get(direction);
        if (counterLabel != null) {
            counterLabel.setText("Cars: " + count);
        }
    }
}
