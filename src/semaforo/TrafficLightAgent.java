
package semaforo;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class TrafficLightAgent extends Agent {

    private String posicao;
    private int tempoVerde;
    private int tempoVermelho;
    private String estadoAtual = "VERMELHO";
    
    @Override
    protected void setup() {
        
        //Leitura dos parametros passados para o agente
        Object[] parametros = getArguments();
        posicao = (String) parametros[0];
        tempoVerde = (int) parametros[1];
        tempoVermelho = (int) parametros[2];
        //Fim da leitura dos parametros
        
        System.out.println("SEMAFORO " + posicao + " INICIADO");
        
        // Comportamento para processar mensagens recebidas
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String conteudo = msg.getContent();
                    if (conteudo.equals("VERDE")) {
                        estadoAtual = conteudo;
                    } else if (conteudo.equals("VERMELHO")) {
                        estadoAtual = conteudo;
                    } else if (conteudo.equals("STATUS")){
                        ACLMessage resposta = msg.createReply();
                        resposta.setPerformative(ACLMessage.INFORM);
                        resposta.setContent(estadoAtual);
                        send(resposta);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
