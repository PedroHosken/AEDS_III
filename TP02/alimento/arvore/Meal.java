package TP02.alimento.arvore;

/**
 * Classe Meal
 * Banco de dados: daily_food_nutrition_dataset - KAGLE
 * @author : Gabriel Tamietti Mauro
 * @author : Pedro Hosken Fernandes Guimarães - 816561
 */

// ---- Bibliotecas ---- //
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
        this.data = null;
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

    }

    // ---- Construtor Passagem de Parametros ---- //
    public Meal(Date data, int usuario, String alimento, String categoria, int caloria, double proteina,
            double carboidrato,
            double gordura, double fibra, double acucar, int sodio, int colesterol, String tipo, int liquido) {
        this.data = data;
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
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("#,##0.00");// formata o valor dos pontos
        return "\nData:" + new SimpleDateFormat("dd/MM/yyyy").format(data) +
                "\nID:" + usuario +
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
                "\nLiquido:" + liquido;
    }

    /**
     * Byte to bytearray
     * 
     * Metódo da Classe para escrever um objeto com seus atributos já em BYTE.
     * 
     */

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // conversão em byte
        DataOutputStream dos = new DataOutputStream(baos); // escrita dos dados

        // Escrever cada Atributo
        dos.writeLong(data.getTime()); // Grava a data como long (milissegundos)
        dos.writeInt(usuario);
        dos.writeUTF(alimento);
        dos.writeUTF(categoria);
        dos.writeInt(caloria);
        dos.writeDouble(proteina);
        dos.writeDouble(carboidrato);
        dos.writeDouble(gordura);
        dos.writeDouble(fibra);
        dos.writeDouble(acucar);
        dos.writeInt(sodio);
        dos.writeInt(colesterol);
        dos.writeUTF(tipo);
        dos.writeInt(liquido);

        // retornar vetor de bytes

        return baos.toByteArray();

    }

    /**
     * Metódo fromByteArray
     * Objetivo: ler de um arquivo de bytes, e formar o objeto a partir dele.
     * 
     */

    public void fromByteArray(byte[] ba) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(ba); // para ler do arquivo
        DataInputStream dis = new DataInputStream(bais); // para tratar os dados

        // ler cada atributo do arquivo
        long timestamp = dis.readLong(); // Lê o valor long correspondente à data e converte para Date
        data = new Date(timestamp);
        usuario = dis.readInt();
        alimento = dis.readUTF();
        categoria = dis.readUTF();
        caloria = dis.readInt();
        proteina = dis.readDouble();
        carboidrato = dis.readDouble();
        gordura = dis.readDouble();
        fibra = dis.readDouble();
        acucar = dis.readDouble();
        sodio = dis.readInt();
        colesterol = dis.readInt();
        tipo = dis.readUTF();
        liquido = dis.readInt();
    }
    public int getUserId() {
        return usuario;
    }
    

}

