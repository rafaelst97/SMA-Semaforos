package semaforo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashSet;
import java.util.Set;

public class RadarAgent extends Agent {
    
    private Set<String> placasInfratoras;
    private CrossroadGUI gui;

    @Override
    protected void setup() {
        placasInfratoras = new HashSet<>();
        gui = CrossroadGUI.getInstance();

        System.out.println("Radar iniciado e aguardando infracoes...");

        // Comportamento para receber e processar mensagens dos carros infratores
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Filtrar apenas mensagens do tipo INFORM
                MessageTemplate template = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                ACLMessage msg = receive(template);

                if (msg != null) {
                    String placaCarro = msg.getContent();
                    if (placaCarro != null && !placaCarro.isEmpty()) {
                        // Verifica se a placa já foi registrada para evitar duplicidade
                        if (!placasInfratoras.contains(placaCarro)) {
                            // Armazena a placa do carro infrator
                            placasInfratoras.add(placaCarro);
                            gui.adicionarInfracao(placaCarro);
                            System.out.println("Radar registrou carro infrator com placa: " + placaCarro);
                            System.out.println("Placas registradas ate agora: " + placasInfratoras);
                            
                            // Envia a placa ao CoordinatorAgent
                            ACLMessage msgPlaca = new ACLMessage(ACLMessage.INFORM);
                            msgPlaca.addReceiver(new AID("coordinator", AID.ISLOCALNAME));
                            msgPlaca.setContent("INFRACAO:" + placaCarro);
                            send(msgPlaca);
                            System.out.println("Mensagem de infracao enviada: " + msgPlaca.getContent());
                        } else {
                            System.out.println("Placa " + placaCarro + " já registrada. Ignorando duplicidade.");
                        }
                    }
                } else {
                    block(); // Bloqueia o comportamento até a chegada de novas mensagens
                }
            }
        });
    }
}
