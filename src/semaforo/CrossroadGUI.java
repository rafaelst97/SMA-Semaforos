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

        // Área de texto para mostrar a lista de placas multadas
        listaInfracoes = new TextArea();
        listaInfracoes.setEditable(false);
        listaInfracoes.setPrefSize(250, 200);
        listaInfracoes.setLayoutX(10);
        listaInfracoes.setLayoutY(10);

        // Adiciona os elementos ao painel
        pane.getChildren().addAll(estradaVertical, estradaHorizontal, listaInfracoes);

        // Configuração da cena
        Scene scene = new Scene(pane, width, height);
        primaryStage.setTitle("Visualização do Cruzamento");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método estático para obter a instância única
    public static CrossroadGUI getInstance() {
        return instance;
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
