package TPS.TP01;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// ---- Classe Meal ---- //
public class Meal {

    // --- Atributos ---- //
    protected Date data;
    protected int usuario;
    protected String alimento;
    protected String categoria;
    protected int caloria;
    protected double proteina;
    protected double carboidrato;
    protected double gordura;
    protected double fibra;
    protected double acucar;
    protected int sodio;
    protected int colesterol;
    protected String tipo;
    protected int liquido;

    // ---- Construtor Padrão ---- //
    public Meal() {
        this.usuario = 0;
        this.alimento = "";
        this.categoria = "";
        this.caloria = 0;
        this.proteina = 0.0;
        this.carboidrato = 0.0;
        this.gordura = 0.0;
        this.fibra = 0.0;
        this.acucar = 0.0;
        this.sodio = 0;
        this.colesterol = 0;
        this.tipo = "";
        this.liquido = 0;
        this.data = null;

    }

    // ---- Construtor Passagem de Parametros ---- //
    public Meal(int usuario, String alimento, String categoria, int caloria, double proteina, double carboidrato,
            double gordura, double fibra, double acucar, int sodio, int colesterol, String tipo, int liquido,
            Date data) {
        this.usuario = usuario;
        this.alimento = alimento;
        this.categoria = categoria;
        this.caloria = caloria;
        this.proteina = proteina;
        this.carboidrato = carboidrato;
        this.gordura = gordura;
        this.fibra = fibra;
        this.acucar = acucar;
        this.sodio = sodio;
        this.colesterol = colesterol;
        this.tipo = tipo;
        this.liquido = liquido;
        this.data = data;

    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");// formata o valor dos pontos
        return "\nID:" + usuario +
                "\nAlimento:" + alimento +
                "\nCategoria:" + categoria +
                "\nCalorias:" + caloria +
                "\nProteinas:" + df.format(proteina) +
                "\nCarboidratos:" + df.format(carboidrato) +
                "\nGorduras:" + df.format(gordura) +
                "\nFibras:" + df.format(fibra) +
                "\nAcucares:" + df.format(acucar) +
                "\nSodio:" + sodio +
                "\nColesterol:" + colesterol +
                "\nRefeição:" + tipo +
                "\nLiquido:" + liquido +
                "\nData:" + new SimpleDateFormat("dd/MM/yyyy").format(data);
    }

}
