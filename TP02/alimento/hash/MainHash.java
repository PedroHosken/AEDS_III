package TP02.alimento.hash;

//import TP02.alimento.hash.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainHash {

    private static final String CSV_FILE = "TP01/alimento/daily_food_nutrition_dataset.csv";
    private static final String HASH_BIN_FILE = "meals_hash.bin";
    private static final Scanner scanner = new Scanner(System.in);

    // Define profundidade inicial e capacidade por bucket
    private static final Diretorio diretorio = new Diretorio(2, 4);

    public static void main(String[] args) throws IOException {
        int opcao;

        do {
            System.out.println("\n--- Menu Hash Extensível ---");
            System.out.println("1. Carregar CSV no Hash");
            System.out.println("2. Ler Registro");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    carregarCSV_Hash();
                    break;
                case 2:
                    lerRegistro_hash();
                    break;
                case 3:
                    atualizarRegistro_hash();
                    break;
                case 4:
                    deletarRegistro_hash();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }

        } while (opcao != 0);
    }

    private static void carregarCSV_Hash() throws IOException {
        Files.deleteIfExists(Paths.get(HASH_BIN_FILE));

        try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE));
                RandomAccessFile raf = new RandomAccessFile(HASH_BIN_FILE, "rw")) {

            String linha;
            br.readLine(); // pula cabeçalho
            int id = 1;

            while ((linha = br.readLine()) != null) {
                String[] campos = linha.split(",");
                try {
                    Meal meal = new Meal(
                            new SimpleDateFormat("yyyy-MM-dd").parse(campos[0]),
                            parseIntOrDefault(campos[1]),
                            campos[2],
                            campos[3],
                            parseIntOrDefault(campos[4]),
                            parseDoubleOrDefault(campos[5]),
                            parseDoubleOrDefault(campos[6]),
                            parseDoubleOrDefault(campos[7]),
                            parseDoubleOrDefault(campos[8]),
                            parseDoubleOrDefault(campos[9]),
                            parseIntOrDefault(campos[10]),
                            parseIntOrDefault(campos[11]),
                            campos[12],
                            parseIntOrDefault(campos[13]));

                    byte[] data = meal.toByteArray();
                    long pos = raf.length();
                    raf.seek(pos);
                    raf.writeByte(1); // lápide
                    raf.writeInt(data.length);
                    raf.write(data);

                    diretorio.inserir(meal.getUserId(), pos);

                } catch (Exception e) {
                    System.out.println("Erro: " + linha);
                }
            }

            System.out.println("CSV carregado no hash com sucesso!");

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

    private static void lerRegistro_hash() throws IOException {
        System.out.print("Informe o ID do usuário: ");
        int id = scanner.nextInt();

        Bucket bucket = diretorio.buscarBucket(id);

        for (Entrada entrada : bucket.getRegistros()) {
            if (entrada.getChave() == id) {
                try (RandomAccessFile raf = new RandomAccessFile(HASH_BIN_FILE, "r")) {
                    raf.seek(entrada.getPosicaoArquivo());
                    byte lapide = raf.readByte();
                    int tamanho = raf.readInt();
                    byte[] data = new byte[tamanho];
                    raf.readFully(data);

                    if (lapide == 1) {
                        Meal meal = new Meal();
                        meal.fromByteArray(data);
                        System.out.println(meal);
                        return;
                    } else {
                        System.out.println("Registro marcado como removido.");
                        return;
                    }
                }
            }
        }

        System.out.println("Registro não encontrado.");
    }

    private static void deletarRegistro_hash() throws IOException {
        System.out.print("Informe o ID para deletar: ");
        int id = scanner.nextInt();

        Bucket bucket = diretorio.buscarBucket(id);
        Entrada entradaParaRemover = null;

        for (Entrada entrada : bucket.getRegistros()) {
            if (entrada.getChave() == id) {
                entradaParaRemover = entrada;
                break;
            }
        }

        if (entradaParaRemover == null) {
            System.out.println("Registro não encontrado no hash.");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(HASH_BIN_FILE, "rw")) {
            raf.seek(entradaParaRemover.getPosicaoArquivo());
            raf.writeByte(0); // marca como removido
        }

        bucket.getRegistros().remove(entradaParaRemover);
        System.out.println("Registro removido com sucesso.");
    }

    private static void atualizarRegistro_hash() throws IOException {
        System.out.print("Informe o ID para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Bucket bucket = diretorio.buscarBucket(id);
        Entrada entradaOriginal = null;

        for (Entrada e : bucket.getRegistros()) {
            if (e.getChave() == id) {
                entradaOriginal = e;
                break;
            }
        }

        if (entradaOriginal == null) {
            System.out.println("Registro não encontrado.");
            return;
        }

        try (RandomAccessFile raf = new RandomAccessFile(HASH_BIN_FILE, "rw")) {
            raf.seek(entradaOriginal.getPosicaoArquivo());
            byte lapide = raf.readByte();
            int tamanhoOriginal = raf.readInt();
            byte[] dadosAntigos = new byte[tamanhoOriginal];
            raf.readFully(dadosAntigos);

            if (lapide == 0) {
                System.out.println("Registro está removido.");
                return;
            }

            Meal meal = new Meal();
            meal.fromByteArray(dadosAntigos);

            System.out.println("Digite os novos valores (pressione ENTER para manter):");

            System.out.print("Nova Data (dd/MM/yyyy): ");
            String novaDataStr = scanner.nextLine();
            if (!novaDataStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                meal.data = sdf.parse(novaDataStr);
            }

            System.out.print("Novo alimento: ");
            String novoAlimento = scanner.nextLine();
            if (!novoAlimento.isEmpty())
                meal.alimento = novoAlimento;

            System.out.print("Nova categoria: ");
            String novaCategoria = scanner.nextLine();
            if (!novaCategoria.isEmpty())
                meal.categoria = novaCategoria;

            System.out.print("Nova caloria: ");
            String novaCaloria = scanner.nextLine();
            if (!novaCaloria.isEmpty())
                meal.caloria = Integer.parseInt(novaCaloria);

            System.out.print("Nova proteína: ");
            String novaProteina = scanner.nextLine();
            if (!novaProteina.isEmpty())
                meal.proteina = Double.parseDouble(novaProteina);

            System.out.print("Novo carboidrato: ");
            String novoCarbo = scanner.nextLine();
            if (!novoCarbo.isEmpty())
                meal.carboidrato = Double.parseDouble(novoCarbo);

            System.out.print("Nova gordura: ");
            String novaGordura = scanner.nextLine();
            if (!novaGordura.isEmpty())
                meal.gordura = Double.parseDouble(novaGordura);

            System.out.print("Nova fibra: ");
            String novaFibra = scanner.nextLine();
            if (!novaFibra.isEmpty())
                meal.fibra = Double.parseDouble(novaFibra);

            System.out.print("Novo açúcar: ");
            String novoAcucar = scanner.nextLine();
            if (!novoAcucar.isEmpty())
                meal.acucar = Double.parseDouble(novoAcucar);

            System.out.print("Novo sódio: ");
            String novoSodio = scanner.nextLine();
            if (!novoSodio.isEmpty())
                meal.sodio = Integer.parseInt(novoSodio);

            System.out.print("Novo colesterol: ");
            String novoColesterol = scanner.nextLine();
            if (!novoColesterol.isEmpty())
                meal.colesterol = Integer.parseInt(novoColesterol);

            System.out.print("Novo tipo: ");
            String novoTipo = scanner.nextLine();
            if (!novoTipo.isEmpty())
                meal.tipo = novoTipo;

            System.out.print("Novo líquido: ");
            String novoLiquido = scanner.nextLine();
            if (!novoLiquido.isEmpty())
                meal.liquido = Integer.parseInt(novoLiquido);

            byte[] dadosNovos = meal.toByteArray();

            if (dadosNovos.length <= dadosAntigos.length) {
                raf.seek(entradaOriginal.getPosicaoArquivo() + 5); // pula lápide e tamanho
                raf.write(dadosNovos);
            } else {
                // marca como removido
                raf.seek(entradaOriginal.getPosicaoArquivo());
                raf.writeByte(0);

                // grava novo no final do arquivo
                long novaPos = raf.length();
                raf.seek(novaPos);
                raf.writeByte(1);
                raf.writeInt(dadosNovos.length);
                raf.write(dadosNovos);

                // atualiza posição da entrada
                entradaOriginal.setPosicaoArquivo(novaPos);
            }

            System.out.println("Registro atualizado com sucesso!");

        } catch (Exception e) {
            System.out.println("Erro ao atualizar registro: " + e.getMessage());
        }
    }

}