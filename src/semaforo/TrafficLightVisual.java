package semaforo;

import java.awt.Color;

public class TrafficLightVisual {
    private String position;
    private String state;
    private Color color;

    public TrafficLightVisual(String position) {
        this.position = position;
        this.state = "vermelho";
        this.color = Color.RED;
    }

    public String getPosition() {
        return position;
    }

    public String getState() {
        return state;
    }

    public Color getColor() {
        return color;
    }

    public void setState(String state) {
        this.state = state;
        switch (state) {
            case "verde":
                this.color = Color.GREEN;
                break;
            case "amarelo":
                this.color = Color.YELLOW;
                break;
            case "vermelho":
                this.color = Color.RED;
                break;
        }
    }
}
