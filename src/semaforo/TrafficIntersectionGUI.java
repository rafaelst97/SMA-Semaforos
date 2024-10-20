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

public class TrafficIntersectionGUI extends JFrame {
    private JPanel intersectionPanel;
    private JLabel[] trafficLights; // Representa os semáforos
    private JTextArea infractorArea; // Área para mostrar infratores
    private ArrayList<String> infractors; // Lista de infratores

    public TrafficIntersectionGUI() {
        setTitle("Sistema de Cruzamento Inteligente");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        intersectionPanel = new JPanel();
        intersectionPanel.setLayout(new GridLayout(2, 2));
        trafficLights = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            trafficLights[i] = new JLabel("Semáforo " + (i + 1) + ": VERMELHO", SwingConstants.CENTER);
            trafficLights[i].setOpaque(true);
            trafficLights[i].setBackground(Color.RED);
            intersectionPanel.add(trafficLights[i]);
        }

        add(intersectionPanel, BorderLayout.CENTER);

        infractorArea = new JTextArea();
        infractorArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(infractorArea);
        scrollPane.setPreferredSize(new Dimension(400, 100));
        add(scrollPane, BorderLayout.SOUTH);

        infractors = new ArrayList<>();
    }

    public void updateTrafficLight(int index, boolean isGreen) {
        if (isGreen) {
            trafficLights[index].setText("Semáforo " + (index + 1) + ": VERDE");
            trafficLights[index].setBackground(Color.GREEN);
        } else {
            trafficLights[index].setText("Semáforo " + (index + 1) + ": VERMELHO");
            trafficLights[index].setBackground(Color.RED);
        }
    }

    public void addInfractor(String plate) {
        infractors.add(plate);
        infractorArea.append("Infrator: " + plate + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TrafficIntersectionGUI gui = new TrafficIntersectionGUI();
            gui.setVisible(true);
        });
    }
}
