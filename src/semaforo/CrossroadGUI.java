package semaforo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;

public class CrossroadGUI extends Application {

    private static CrossroadGUI instance; // Instância única
    private TextArea listaInfracoes;
    private Rectangle semaforoNorte, semaforoSul, semaforoLeste, semaforoOeste;
    private Pane pane; // Painel principal
    private Map<String, Rectangle> carros; // Mapa para os carros na GUI
    private Map<String, Text> textosPlacas; // Mapa para os textos das placas


    @Override
    public void start(Stage primaryStage) {
        instance = this; // Define a instância única

        pane = new Pane();
        carros = new HashMap<>(); // Inicializa o mapa dos carros
        textosPlacas = new HashMap<>(); // Inicializa o mapa dos textos das placas

        int width = 800;
        int height = 800;

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
    
    public void adicionarCarro(String placa, String direcao, boolean furaSinal) {
        Platform.runLater(() -> {
            Color corCarro = furaSinal ? Color.RED : Color.BLUE; // Vermelho para infratores, azul para outros
            Rectangle carro = new Rectangle(30, 15, corCarro); // Cria o carro
            Text textoPlaca = new Text(placa); // Texto com a placa
            textoPlaca.setFill(Color.WHITE);

            // Posiciona o carro na direção inicial
            switch (direcao) {
                case "N": // Rua Norte
                    carro.setLayoutX(390); // Centraliza na via vertical
                    carro.setLayoutY(0); // Começa no topo
                    textoPlaca.setLayoutX(390);
                    textoPlaca.setLayoutY(-10);
                    break;
                case "S": // Rua Sul
                    carro.setLayoutX(390); // Centraliza na via vertical
                    carro.setLayoutY(780); // Começa na parte inferior
                    textoPlaca.setLayoutX(390);
                    textoPlaca.setLayoutY(770);
                    break;
                case "E": // Rua Leste
                    carro.setLayoutX(780); // Começa no lado direito
                    carro.setLayoutY(390); // Centraliza na via horizontal
                    textoPlaca.setLayoutX(780);
                    textoPlaca.setLayoutY(380);
                    break;
                case "W": // Rua Oeste
                    carro.setLayoutX(0); // Começa no lado esquerdo
                    carro.setLayoutY(390); // Centraliza na via horizontal
                    textoPlaca.setLayoutX(0);
                    textoPlaca.setLayoutY(380);
                    break;
            }

            // Adiciona o carro e a placa ao painel
            pane.getChildren().addAll(carro, textoPlaca);
            carros.put(placa, carro);
            textosPlacas.put(placa, textoPlaca);
        });
    }
    
    // Método para mover um carro na GUI
    public void moverCarro(String placa, double deltaX, double deltaY) {
        Platform.runLater(() -> {
            Rectangle carro = carros.get(placa);
            Text textoPlaca = textosPlacas.get(placa);
            if (carro != null && textoPlaca != null) {
                // Move o carro
                carro.setLayoutX(carro.getLayoutX() + deltaX);
                carro.setLayoutY(carro.getLayoutY() + deltaY);

                // Move o texto da placa junto com o carro
                textoPlaca.setLayoutX(textoPlaca.getLayoutX() + deltaX);
                textoPlaca.setLayoutY(textoPlaca.getLayoutY() + deltaY);
            }
        });
    }
    
    // Método para remover um carro da GUI
    public void removerCarro(String placa) {
        Platform.runLater(() -> {
            Rectangle carro = carros.get(placa);
            Text textoPlaca = textosPlacas.get(placa);
            if (carro != null && textoPlaca != null) {
                pane.getChildren().removeAll(carro, textoPlaca); // Remove o carro e o texto
                carros.remove(placa);
                textosPlacas.remove(placa);
            }
        });
    }

    public boolean estaInicializada() {
        return listaInfracoes != null;
    }
}
