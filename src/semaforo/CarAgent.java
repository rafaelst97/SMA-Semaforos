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
    private String placa;
    private boolean furaSinal;
    private String ruaNascimento;
    private String direcao;

    @Override
    protected void setup() {
        
        this.geraAtributosCarro();
        System.out.println("Gerado carro com placa " + placa);
        System.out.println("Ele vai furar sinal? " + furaSinal);
        System.out.println("Rua que nasce " + ruaNascimento);
        //Parametros
        /*Object[] parametros = getArguments();
        
        placa = (String) parametros[0];
        furaSinal = (boolean) parametros[1];
        ruaNascimento = (String) parametros[2];
        direcao = (String) parametros[3];*/

    }

    private void geraAtributosCarro() {
        this.placa = geraPlaca();
        this.furaSinal = defineSeFuraSinal();
        this.ruaNascimento = defineRua();
        //this.direcao = initialRoad;  // Define a direção inicial do carro
    }

    private String geraPlaca() {
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

    private boolean defineSeFuraSinal() {
        Random random = new Random();
        return random.nextInt(100) < 30; // 30% de chance de ser infrator
    }

    private String defineRua() {
        String[] roads = {"N", "S", "E", "W"};
        Random random = new Random();
        return roads[random.nextInt(roads.length)];
    }
}
