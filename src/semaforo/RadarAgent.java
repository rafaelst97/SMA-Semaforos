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
import jade.lang.acl.ACLMessage;
import java.util.HashSet;
import java.util.Set;

public class RadarAgent extends Agent {

    // Conjunto para armazenar as placas dos carros infratores
    private Set<String> violators;

    @Override
    protected void setup() {
        violators = new HashSet<>();
        System.out.println("RadarAgent iniciado.");

        // Adiciona comportamento cíclico para monitorar infrações de trânsito
        addBehaviour(new MonitorInfractionsBehaviour());
    }

    // Comportamento para monitorar infrações
    private class MonitorInfractionsBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            // Recebe mensagens de carros
            ACLMessage msg = receive();
            if (msg != null) {
                String content = msg.getContent();
                
                // Verifica se a mensagem indica uma infração
                if (content.startsWith("INFRAÇÃO")) {
                    // Extrai a placa do carro infrator
                    String plate = content.split(" ")[1];
                    int lane = Integer.parseInt(content.split(" ")[3]);

                    // Adiciona a placa à lista de infratores
                    if (violators.add(plate)) {
                        System.out.println("Radar detectou uma infração: Carro " + plate + 
                                           " passou no sinal vermelho na via " + lane);

                        // Exibe as infrações na interface gráfica (simulado aqui com print)
                        displayViolation(plate, lane);
                    }
                }
            } else {
                // Se não houver mensagens, bloqueia até a próxima mensagem
                block();
            }
        }

        // Método para exibir a infração na interface gráfica
        private void displayViolation(String plate, int lane) {
            // Aqui você pode integrar com a interface gráfica para exibir a infração na área específica
            System.out.println("Exibindo infração: " + plate + " na via " + lane);
        }
    }
}
