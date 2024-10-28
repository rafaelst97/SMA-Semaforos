package semaforo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class CrossroadGUI extends JFrame {

    public CrossroadGUI() {
        setTitle("Simulação de Cruzamento");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel cruzamentoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                desenharCruzamento(g);
            }

            private void desenharCruzamento(Graphics g) {
                // Desenha o cruzamento no centro do painel
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;

                // Desenha as ruas (linhas horizontais e verticais) centradas
                g.fillRect(centerX - 50, 0, 100, getHeight()); // Rua vertical centrada
                g.fillRect(0, centerY - 50, getWidth(), 100); // Rua horizontal centrada
            }
        };
        cruzamentoPanel.setPreferredSize(new Dimension(800, 800));
        cruzamentoPanel.setBackground(Color.WHITE);
        add(cruzamentoPanel, BorderLayout.CENTER);

        setVisible(true);
    }
}