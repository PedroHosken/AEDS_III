package EXS.TP01.alimento;

/**
 * Método Main - Teste de Classe Meal
 * Banco de dados: daily_food_nutrition_dataset - KAGGLE
 * Autores:
 *    Gabriel Tamietti Mauro
 *    Pedro Hosken Fernandes Guimarães - 816561
 */

// ---- Bibliotecas ---- //
import java.io.RandomAccessFile;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main2 {
    public static void main(String[] args) throws ParseException {
        // Formatação da Data: definindo a data desejada (27/02/2025)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dataDesejada = sdf.parse("27/02/2025");

        // Instanciando a Meal com valores exemplares
        Meal r1 = new Meal(dataDesejada,
                1, "Bolo de Chocolate", "Sobremesa", 250, 4.5, 35.0,
                10.0, 2.0, 20.0, 300, 15, "Lanche", 100);

        // Declaração Externa
        byte[] ba;
        RandomAccessFile arq;

        // --- Gravando os dados de r1 em um arquivo binário --- //
        try {
            arq = new RandomAccessFile("Meal_ds.db", "rw"); // leitura e escrita
            ba = r1.toByteArray();
            arq.writeInt(ba.length); // comprimento total do registro
            arq.write(ba); // escrever o vetor de bytes

            // Declaração Externa
            int tam;
            Meal r_temp = new Meal(); // inicializa temporário
            // --- Lendo os dados do arquivo binário e reconstruindo um objeto Meal --- //
            arq.seek(0); // volta para ínicio do arquivo
            tam = arq.readInt(); // passa tamanho do registro
            byte[] bb = new byte[tam]; // vetor de bytes com o tamanho do registro
            arq.read(bb); // leitura do vetor de bytes
            r_temp.fromByteArray(bb); // passa para objeto
            arq.close(); // fechar arquivo

            System.out.println("Dados lidos do arquivo:");
            System.out.println(r_temp);
        } catch (Exception e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }
}

/**
 * TESTES
 * 1- Remover o DOS
 * DataOutputStream dos = new DataOutputStream(arq);
 * 
 * // Gravando os atributos de Meal em uma ordem definida
 * // Para a data, grava o timestamp (milissegundos desde 1 de janeiro de 1970)
 * dos.writeLong(r1.data.getTime());
 * 
 * dos.writeInt(r1.usuario);
 * dos.writeUTF(r1.alimento);
 * dos.writeUTF(r1.categoria);
 * dos.writeInt(r1.caloria);
 * dos.writeDouble(r1.proteina);
 * dos.writeDouble(r1.carboidrato);
 * dos.writeDouble(r1.gordura);
 * dos.writeDouble(r1.fibra);
 * dos.writeDouble(r1.acucar);
 * dos.writeInt(r1.sodio);
 * dos.writeInt(r1.colesterol);
 * dos.writeUTF(r1.tipo);
 * dos.writeInt(r1.liquido);
 * 
 * dos.close();
 * 
 * 
 * 2- remoção do DIS
 * DataInputStream dis = new DataInputStream(arq2);
 * 
 * Meal r_temp = new Meal();
 * // Reconstruindo a data a partir do long lido
 * r_temp.data = new Date(dis.readLong());
 * r_temp.usuario = dis.readInt();
 * r_temp.alimento = dis.readUTF();
 * r_temp.categoria = dis.readUTF();
 * r_temp.caloria = dis.readInt();
 * r_temp.proteina = dis.readDouble();
 * r_temp.carboidrato = dis.readDouble();
 * r_temp.gordura = dis.readDouble();
 * r_temp.fibra = dis.readDouble();
 * r_temp.acucar = dis.readDouble();
 * r_temp.sodio = dis.readInt();
 * r_temp.colesterol = dis.readInt();
 * r_temp.tipo = dis.readUTF();
 * r_temp.liquido = dis.readInt();
 * 
 * dis.close();
 */
