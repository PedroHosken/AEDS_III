package TP01.alimento;

/**
 * Método Main - Teste de Classe Meal
 * Banco de dados: daily_food_nutrition_dataset - KAGGLE
 * Autores:
 *    Gabriel Tamietti Mauro - 800829
 *    Pedro Hosken Fernandes Guimarães - 816561
 */

// ---- Bibliotecas ---- //
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    // Definição dos PATHS
    private static final String CSV_FILE = "TP01/alimento/daily_food_nutrition_dataset.csv";
    private static final String BIN_FILE = "meals.bin";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        int opcao;
        // Menu do CRUD
        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Carregar CSV para Arquivo Binário");
            System.out.println("2. Ler Registro");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1 : carregarCSV(); break;
                case 2 : lerRegistro(); break;
                case 3 : atualizarRegistro(); break;
                case 4 : deletarRegistro(); break;
                case 0 : System.out.println("Saindo..."); break;
                default : System.out.println("Opção inválida!"); break;
            }
        } while (opcao != 0);
    }

   
    //=============================Carregar CSV para conversão em arquivo binário (Opção 1)===============================//
   private static void carregarCSV() throws IOException {
    try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE));
         RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "rw")) {

        String linha;
        
        // Ler o cabeçalho do arquivo binário
        raf.seek(0);
        int ultimoId = raf.readInt();  // Obter o último ID utilizado
        int id = ultimoId + 1;         // O próximo ID será o último ID + 1

        br.readLine();  

        while ((linha = br.readLine()) != null) {
            String[] campos = linha.split(",");
            try {
                Meal meal = new Meal(
                    new SimpleDateFormat("yyyy-MM-dd").parse(campos[0]),
                    id,
                    campos[1],
                    campos[2],
                    parseIntOrDefault(campos[3]),
                    parseDoubleOrDefault(campos[4]),
                    parseDoubleOrDefault(campos[5]),
                    parseDoubleOrDefault(campos[6]),
                    parseDoubleOrDefault(campos[7]),
                    parseDoubleOrDefault(campos[8]),
                    parseIntOrDefault(campos[9]),
                    parseIntOrDefault(campos[10]),
                    campos[11],
                    parseIntOrDefault(campos[12])
                );

                byte[] data = meal.toByteArray();

                raf.seek(raf.length()); 
                raf.writeByte(1);        // Lápide (1 = ativo)
                raf.writeInt(data.length); 
                raf.write(data);         

                // Atualiza o último ID utilizado
                raf.seek(0);
                raf.writeInt(id);

                // Incrementa o ID para a próxima refeição
                id++;
            } catch (Exception e) {
                System.out.println("Erro ao processar linha: " + linha + " - " + e.getMessage());
            }
        }
        System.out.println("CSV carregado com sucesso!");
    } catch (Exception e) {
        System.out.println("Erro ao carregar CSV: " + e.getMessage());
    }
}



private static int parseIntOrDefault(String str) {
    try {
        return Integer.parseInt(str.trim());
    } catch (NumberFormatException e) {
        return 0;
    }
}

private static double parseDoubleOrDefault(String str) {
    try {
        return Double.parseDouble(str.trim());
    } catch (NumberFormatException e) {
        return 0.0;
    }
}


    
    //=============================Ler Registro (Opção 2)===============================//
    private static void lerRegistro() throws IOException {
        System.out.print("Informe o ID para ler: ");
        int id = scanner.nextInt();

        try (RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "r")) {
            raf.seek(4); 
            while (raf.getFilePointer() < raf.length()) {
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.read(data);

                if (lapide == 1) {
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    if (meal.getUserId() == id) {
                        System.out.println(meal);
                        return;
                    }
                }
            }
            System.out.println("Registro não encontrado.");
        }
    }

    //=============================Atualizar Registro (Opção 3)===============================//
    private static void atualizarRegistro() throws IOException {
        System.out.print("Informe o ID para atualizar: ");
        int id = scanner.nextInt();

        try (RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "rw")) {
            raf.seek(4); 
            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.read(data);

                if (lapide == 1) {
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    if (meal.usuario == id) {
                        System.out.print("Digite o novo tipo de refeição: ");
                        scanner.nextLine(); 
                        meal.tipo = scanner.nextLine();

                        byte[] novoData = meal.toByteArray();
                        if (novoData.length == data.length) {
                            raf.seek(pos + 5);
                            raf.write(novoData);
                        } else {
                            raf.seek(pos);
                            raf.writeByte(0);
                            raf.seek(raf.length());
                            raf.writeByte(1);
                            raf.writeInt(novoData.length);
                            raf.write(novoData);
                        }
                        System.out.println("Registro atualizado com sucesso!");
                        return;
                    }
                }
            }
            System.out.println("Registro não encontrado.");
        }
    }

    
    //=============================Deletar Registro (Opção 4)===============================//
    private static void deletarRegistro() throws IOException {
        System.out.print("Informe o ID para deletar: ");
        int id = scanner.nextInt();

        try (RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "rw")) {
            raf.seek(4);
            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.read(data);

                if (lapide == 1) {
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    if (meal.usuario == id) {
                        raf.seek(pos);
                        raf.writeByte(0);
                        System.out.println("Registro deletado com sucesso!");
                        return;
                    }
                }
            }
            System.out.println("Registro não encontrado.");
        }
    }
}