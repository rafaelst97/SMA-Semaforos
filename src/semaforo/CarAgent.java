/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import java.util.Random;

public class CarAgent extends Agent {
    private String licensePlate;
    private boolean isOffender;
    private String initialRoad;
    private String direction;
    private AID coordinatorAID; // AID do CoordinatorAgent

    @Override
    protected void setup() {
        // Recupera o AID do CoordinatorAgent passado como argumento
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            coordinatorAID = (AID) args[0];
        }

        // Inicialização do carro
        generateCarAttributes();
        
        // Exibir informações iniciais do agente carro
        System.out.println("CarAgent [" + licensePlate + "]: criado na via " + initialRoad + 
                           ". Infrator: " + (isOffender ? "Sim" : "Não"));
        
        // Comportamento de envio de mensagens para a GUI
        addBehaviour(new TickerBehaviour(this, 500) {
            @Override
            protected void onTick() {
                // Envia uma mensagem para o CoordinatorAgent com os dados do carro
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(coordinatorAID);
                msg.setContent(licensePlate + ";" + direction);
                send(msg);

                // Simulação de comportamento de infrator: ignora o semáforo
                if (isOffender && Math.random() < 0.1) { 
                    System.out.println("CarAgent [" + licensePlate + "]: furou o sinal vermelho!");
                }
            }
        });
    }

    private void generateCarAttributes() {
        this.licensePlate = generateLicensePlate();
        this.isOffender = generateOffenderStatus();
        this.initialRoad = generateInitialRoad();
        this.direction = initialRoad;  // Define a direção inicial do carro
    }

    private String generateLicensePlate() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder plate = new StringBuilder();

        // Gerar 3 letras aleatórias
        for (int i = 0; i < 3; i++) {
            plate.append(letters.charAt(random.nextInt(letters.length())));
        }

        // Gerar 4 números aleatórios
        for (int i = 0; i < 4; i++) {
            plate.append(random.nextInt(10));
        }

        return plate.toString();
    }

    private boolean generateOffenderStatus() {
        Random random = new Random();
        return random.nextInt(100) < 30; // 30% de chance de ser infrator
    }

    private String generateInitialRoad() {
        String[] roads = {"N", "S", "E", "W"};
        Random random = new Random();
        return roads[random.nextInt(roads.length)];
    }

    @Override
    protected void takeDown() {
        // Finalização do agente
        System.out.println("CarAgent [" + licensePlate + "]: terminando.");
        
        // Enviar mensagem para a GUI indicando a remoção do carro
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(coordinatorAID);
        msg.setContent(licensePlate + ";REMOVE");
        send(msg);
    }
}
