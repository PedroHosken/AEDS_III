package EXS.TP01.alimento;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Main {
    public static void main(String[] args) {

        Meal r1 = new Meal();
        Meal r2 = new Meal();
        Meal r3 = new Meal();

        // Gravando os dados dos Meales em um arquivo binário
        FileOutputStream arq; // arquivo de saída
        DataOutputStream dos; // fluxo de saída de dados

        // Lendo os dados dos Meales de um arquivo binário
        FileInputStream arq2; // arquivo de entrada
        DataInputStream dis; // fluxo de entrada de dados

        try {

            arq = new FileOutputStream("../dados/Meales_ds.db");
            dos = new DataOutputStream(arq); // conecta o fluxo de saída de dados ao arquivo

            dos.writeInt(j1.idMeal);
            dos.writeUTF(j1.nome);
            dos.writeFloat(j1.pontos);

            dos.writeInt(j2.idMeal);
            dos.writeUTF(j2.nome);
            dos.writeFloat(j2.pontos);

            dos.writeInt(j3.idMeal);
            dos.writeUTF(j3.nome);
            dos.writeFloat(j3.pontos);

            dos.close();
            arq.close();

            Meal j_temp = new Meal();

            arq2 = new FileInputStream("../dados/Meales_ds.db");
            dis = new DataInputStream(arq2); // conecta o fluxo de entrada de dados ao arquivo

            j_temp.idMeal = dis.readInt();
            j_temp.nome = dis.readUTF();
            j_temp.pontos = dis.readFloat();
            System.out.println(j_temp);

            j_temp.idMeal = dis.readInt();
            j_temp.nome = dis.readUTF();
            j_temp.pontos = dis.readFloat();
            System.out.println(j_temp);

            j_temp.idMeal = dis.readInt();
            j_temp.nome = dis.readUTF();
            j_temp.pontos = dis.readFloat();
            System.out.println(j_temp);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
