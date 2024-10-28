package semaforo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CrossroadGUI extends Application {

    private static CrossroadGUI instance; // Instância única
    private TextArea listaInfracoes;
    private Rectangle semaforoNorte, semaforoSul, semaforoLeste, semaforoOeste;

    @Override
    public void start(Stage primaryStage) {
        instance = this; // Define a instância única

        Pane pane = new Pane();

        int width = 800;
        int height = 600;

        // Estrada vertical
        Rectangle estradaVertical = new Rectangle(width / 2 - 50, 0, 100, height);
        estradaVertical.setFill(Color.GRAY);

        // Estrada horizontal
        Rectangle estradaHorizontal = new Rectangle(0, height / 2 - 50, width, 100);
        estradaHorizontal.setFill(Color.GRAY);
        
        // Adiciona os semáforos mais próximos do cruzamento
        semaforoNorte = criarSemaforo(width / 2 + 30, height / 2 - 80, Color.RED); // Canto superior direito da rua Norte, próximo ao cruzamento
        semaforoSul = criarSemaforo(width / 2 - 50, height / 2 + 60, Color.RED); // Canto inferior direito da rua Sul, próximo ao cruzamento
        semaforoLeste = criarSemaforo(width / 2 + 60, height / 2 + 30, Color.RED); // Canto direito da rua Leste, próximo ao cruzamento
        semaforoOeste = criarSemaforo(width / 2 - 80, height / 2 - 50, Color.RED); // Canto esquerdo da rua Oeste, próximo ao cruzamento

        // Área de texto para mostrar a lista de placas multadas
        listaInfracoes = new TextArea();
        listaInfracoes.setEditable(false);
        listaInfracoes.setPrefSize(250, 200);
        listaInfracoes.setLayoutX(10);
        listaInfracoes.setLayoutY(10);

        // Adiciona os elementos ao painel
        pane.getChildren().addAll(estradaVertical, estradaHorizontal, semaforoNorte, semaforoSul, semaforoLeste, semaforoOeste, listaInfracoes);

        // Configuração da cena
        Scene scene = new Scene(pane, width, height);
        primaryStage.setTitle("Visualização do Cruzamento");
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    // Método para criar um semáforo
    private Rectangle criarSemaforo(int x, int y, Color corInicial) {
        Rectangle semaforo = new Rectangle(20, 20);
        semaforo.setFill(corInicial);
        semaforo.setLayoutX(x);
        semaforo.setLayoutY(y);
        return semaforo;
    }

    // Método estático para obter a instância única
    public static CrossroadGUI getInstance() {
        return instance;
    }

    // Método para atualizar a cor de um semáforo
    public void atualizarSemaforo(String posicao, String estado) {
        Platform.runLater(() -> {
            Color cor = estado.equals("VERDE") ? Color.GREEN : Color.RED;
            switch (posicao) {
                case "N":
                    semaforoNorte.setFill(cor);
                    break;
                case "S":
                    semaforoSul.setFill(cor);
                    break;
                case "E":
                    semaforoLeste.setFill(cor);
                    break;
                case "W":
                    semaforoOeste.setFill(cor);
                    break;
            }
        });
    }

    public void adicionarInfracao(String placa) {
        if (listaInfracoes != null) {
            Platform.runLater(() -> listaInfracoes.appendText(placa + "\n"));
        }
    }

    public boolean estaInicializada() {
        return listaInfracoes != null;
    }
}
