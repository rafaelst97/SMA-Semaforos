/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package semaforo;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CoordinatorAgent extends Agent {
    private List<AID> carAgents;  // Lista de agentes de carro
    private CrossroadGUI gui;     // Referência para a GUI
    private final int MAX_CARS = 20; // Limite de carros na tela

    @Override
    protected void setup() {
        System.out.println(getLocalName() + ": iniciado.");
        carAgents = new ArrayList<>();

        // Inicializa a GUI do cruzamento na Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            gui = new CrossroadGUI(this); // Passa o CoordinatorAgent para a GUI
            gui.setVisible(true);
        });

        // Comportamento para criar e gerenciar agentes de carro
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                // Verifica o número de agentes ativos e cria novos se necessário
                if (carAgents.size() < MAX_CARS) {
                    createCarAgent();
                }
            }
        });

        // Comportamento para receber mensagens dos agentes de carro
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    // Recebe a mensagem de um CarAgent e processa
                    String content = msg.getContent();
                    SwingUtilities.invokeLater(() -> gui.updateCar(content));
                } else {
                    block();
                }
            }
        });
    }

    private void createCarAgent() {
        try {
            // Cria um novo agente de carro
            String agentName = "CarAgent" + System.currentTimeMillis();
            Object[] args = { getAID() };  // Passa o AID do CoordinatorAgent para o CarAgent
            ContainerController container = getContainerController();
            AgentController carAgent = container.createNewAgent(agentName, "semaforo.CarAgent", args);
            carAgent.start();
            carAgents.add(new AID(agentName, AID.ISLOCALNAME));

            System.out.println(getLocalName() + ": criou " + agentName);
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        // Finaliza todos os agentes de carro ao encerrar o CoordinatorAgent
        for (AID carAgent : carAgents) {
            try {
                getContainerController().getAgent(carAgent.getLocalName()).kill();
                System.out.println(getLocalName() + ": finalizou " + carAgent.getLocalName());
            } catch (Exception e) {
                System.out.println(getLocalName() + ": erro ao finalizar " + carAgent.getLocalName());
                e.printStackTrace();
            }
        }
        carAgents.clear();
        if (gui != null) {
            SwingUtilities.invokeLater(() -> gui.dispose()); // Fecha a GUI ao encerrar o agente coordenador
        }
        System.out.println(getLocalName() + ": encerrando.");
    }
}
