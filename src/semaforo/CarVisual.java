/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import java.awt.*;

public class CarVisual {

    private int x, y;
    private int speed = 5;
    private String plate;
    private String direction;
    private CrossroadGUI gui;
    private static final int CAR_SIZE = 20;

    public CarVisual(String plate, String direction, CrossroadGUI gui) {
        this.plate = plate;
        this.direction = direction;
        this.gui = gui;
        initializePosition();
    }

    private void initializePosition() {
        // Define a posição inicial do carro de acordo com a direção
        switch (direction) {
            case "N":
                x = (gui.getWidth() - CAR_SIZE) / 2;
                y = 0;
                break;
            case "S":
                x = (gui.getWidth() - CAR_SIZE) / 2;
                y = gui.getHeight() - CAR_SIZE;
                break;
            case "E":
                x = gui.getWidth() - CAR_SIZE;
                y = (gui.getHeight() - CAR_SIZE) / 2;
                break;
            case "W":
                x = 0;
                y = (gui.getHeight() - CAR_SIZE) / 2;
                break;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, CAR_SIZE, CAR_SIZE);
        g.setColor(Color.BLACK);
        g.drawString(plate, x, y - 5); // Placa acima do carro
    }

    public void move() {
        // Move o carro na direção definida
        switch (direction) {
            case "N":
                y += speed;
                break;
            case "S":
                y -= speed;
                break;
            case "E":
                x -= speed;
                break;
            case "W":
                x += speed;
                break;
        }
    }

    public boolean isOutOfBounds() {
        // Verifica se o carro saiu da tela
        return x < 0 || x > gui.getWidth() || y < 0 || y > gui.getHeight();
    }
    
    public String getPlate() {
        return plate;
    }

    public void terminateAgent() {
        System.out.println("CarAgent [" + plate + "]: terminou.");
    }
}
