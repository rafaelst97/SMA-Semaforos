
package semaforo;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoordinatorAgent extends Agent {
    
    //CONSTANTES DO SISTEMA
    public static final int TEMPO_VERMELHO = 5000;
    public static final int TEMPO_VERDE = 3000;
    public static final int MAX_CARROS = 5;
    
    // Lista dos nomes dos semáforos em sentido horário
    private String[] semaforos = {"semaforo_N", "semaforo_E", "semaforo_S", "semaforo_W"};
    private int semaforoAtual = 0;

    @Override
    protected void setup() {
        
        try {
            ContainerController container = getContainerController();
            
            //Criação de parâmetros para cada agente semaforo
            Object[] parametrosSemaforo_N = new Object[] { "N", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_E = new Object[] { "E", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_S = new Object[] { "S", TEMPO_VERDE, TEMPO_VERMELHO };
            Object[] parametrosSemaforo_W = new Object[] { "W", TEMPO_VERDE, TEMPO_VERMELHO };
            
            //Início do bloco da criacao dos semaforos
            AgentController semaforo_N = container.createNewAgent("semaforo_N","semaforo.TrafficLightAgent", parametrosSemaforo_N);
            AgentController semaforo_E = container.createNewAgent("semaforo_E","semaforo.TrafficLightAgent", parametrosSemaforo_E);
            AgentController semaforo_S = container.createNewAgent("semaforo_S","semaforo.TrafficLightAgent", parametrosSemaforo_S);
            AgentController semaforo_W = container.createNewAgent("semaforo_W","semaforo.TrafficLightAgent", parametrosSemaforo_W);
            
            semaforo_N.start();
            semaforo_E.start();
            semaforo_S.start();
            semaforo_W.start();
            //Fim da criacao dos semaforos

            // Adiciona o comportamento que controla os semáforos
            addBehaviour(new TickerBehaviour(this, TEMPO_VERDE + TEMPO_VERMELHO) {
                @Override
                protected void onTick() {
                    // Enviar mensagem de vermelho para todos os semáforos
                    for (String semaforo : semaforos) {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(new AID(semaforo, AID.ISLOCALNAME));
                        msg.setContent("VERMELHO");
                        send(msg);
                    }

                    // Enviar mensagem de verde para o semáforo atual
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID(semaforos[semaforoAtual], AID.ISLOCALNAME));
                    msg.setContent("VERDE");
                    send(msg);

                    // Atualizar para o próximo semáforo em sentido horário
                    semaforoAtual = (semaforoAtual + 1) % semaforos.length;
                    
                    // Solicitar o status de todos os semáforos
                    System.out.println("STATUS DE TODOS OS SEMAFOROS");
                    ACLMessage statusRequest = new ACLMessage(ACLMessage.REQUEST);
                    for (String semaforo : semaforos) {
                        statusRequest.addReceiver(new AID(semaforo, AID.ISLOCALNAME));
                    }
                    statusRequest.setContent("STATUS");
                    send(statusRequest);
                }
            });
            
            // Adiciona comportamento para receber o status dos semáforos
            addBehaviour(new CyclicBehaviour(this) {
                @Override
                public void action() {
                    ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                    if (msg != null) {
                        System.out.println("Status do semaforo " + msg.getSender().getLocalName() + ": " + msg.getContent());
                    } else {
                        block();
                    }
                }
            });
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
