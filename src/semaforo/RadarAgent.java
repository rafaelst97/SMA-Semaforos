package semaforo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.HashSet;
import java.util.Set;

public class RadarAgent extends Agent {
    
    private Set<String> placasInfratoras;

    @Override
    protected void setup() {
        placasInfratoras = new HashSet<>();

        System.out.println("Radar iniciado e aguardando infracoes...");

        // Comportamento para receber e processar mensagens dos carros infratores
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String placaCarro = msg.getContent();
                    if (placaCarro != null && !placaCarro.isEmpty()) {
                        // Armazena a placa do carro infrator
                        placasInfratoras.add(placaCarro);
                        System.out.println("Radar registrou carro infrator com placa: " + placaCarro);
                        System.out.println("Placas registradas ate agora: " + placasInfratoras);
                        
                        // Envia a placa ao CoordinatorAgent
                        ACLMessage msgPlaca = new ACLMessage(ACLMessage.INFORM);
                        msgPlaca.addReceiver(new AID("coordinator", AID.ISLOCALNAME));
                        msgPlaca.setContent("INFRACAO:" + placaCarro);
                        send(msgPlaca);
                        System.out.println("Mensagem de infração enviada: " + msgPlaca.getContent());
                    }
                } else {
                    block();
                }
            }
        });
    }
}
