/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package semaforo;

/**
 *
 * @author Rafael
 */
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Random;

public class CarAgent extends Agent {
    private static final long serialVersionUID = 1L;
    
    // Direção atual do carro (N, S, E, W)
    private String direction;
    private boolean waiting = false; // Indica se o carro está aguardando no semáforo

    @Override
    protected void setup() {
        System.out.println("CarAgent " + getLocalName() + " iniciado.");

        // Escolher uma direção inicial aleatoriamente
        direction = getRandomDirection();

        // Comportamento cíclico para simular o movimento do carro
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                if (waiting) {
                    // Verificar se o semáforo está verde para a direção atual
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                    ACLMessage msg = receive(mt);

                    if (msg != null && msg.getContent().equals("GREEN:" + direction)) {
                        // Semáforo está verde, o carro pode atravessar
                        System.out.println(getLocalName() + " atravessou o cruzamento para " + direction);
                        waiting = false; // O carro não está mais esperando
                        
                        // Reiniciar o agente para uma nova direção
                        addBehaviour(new OneShotBehaviour() {
                            public void action() {
                                direction = getRandomDirection();
                                System.out.println(getLocalName() + " agora se movendo para " + direction);
                            }
                        });
                    } else {
                        block();
                    }
                } else {
                    // Enviar mensagem para o semáforo atual informando que está esperando
                    waiting = true;
                    sendWaitingMessage();
                }
            }
        });
    }

    // Método para enviar mensagem de espera para o semáforo
    private void sendWaitingMessage() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(getAID(direction + "TrafficLightAgent"));
        msg.setContent("CAR_WAITING");
        send(msg);
        System.out.println(getLocalName() + " esperando no semáforo " + direction);
    }

    // Método para escolher uma direção aleatória
    private String getRandomDirection() {
        String[] directions = {"N", "S", "E", "W"};
        Random rand = new Random();
        return directions[rand.nextInt(directions.length)];
    }
}

