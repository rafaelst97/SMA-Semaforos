package semaforo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class CrossroadGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();
        
        // Dimensões da janela
        int width = 800;
        int height = 600;

        // Estrada vertical
        Rectangle estradaVertical = new Rectangle(width / 2 - 50, 0, 100, height);
        estradaVertical.setFill(Color.GRAY);

        // Estrada horizontal
        Rectangle estradaHorizontal = new Rectangle(0, height / 2 - 50, width, 100);
        estradaHorizontal.setFill(Color.GRAY);

        // Adiciona as estradas ao painel
        pane.getChildren().addAll(estradaVertical, estradaHorizontal);

        // Configuração da cena
        Scene scene = new Scene(pane, width, height);
        primaryStage.setTitle("Visualização do Cruzamento");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Método principal para iniciar a aplicação JavaFX
    public static void main(String[] args) {
        launch(args);
    }
}
