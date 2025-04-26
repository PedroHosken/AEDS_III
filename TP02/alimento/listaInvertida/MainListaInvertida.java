package TP02.alimento.listaInvertida;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainListaInvertida {

    private static final String CSV_FILE = "TP01/alimento/daily_food_nutrition_dataset.csv";
    private static final Scanner scanner = new Scanner(System.in);

    private static final ListaInvertida listaAlimento = new ListaInvertida();
    private static final Map<Integer, Meal> registros = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int opcao;

        do {
            System.out.println("\n--- Menu Lista Invertida (Alimento) ---");
            System.out.println("1. Carregar CSV para Lista Invertida");
            System.out.println("2. Buscar por Termo");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // consumir quebra de linha

            switch (opcao) {
                case 1:
                    carregarCSV();
                    break;
                case 2:
                    buscarTermo();
                    break;
                case 3:
                    atualizarRegistro();
                    break;
                case 4:
                    deletarRegistro();
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

    private static void carregarCSV() throws IOException {
        Files.deleteIfExists(Paths.get("dicionarioInvertido.bin"));
        Files.deleteIfExists(Paths.get("listasInvertidas.bin"));

        try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE))) {
            String linha;
            br.readLine(); // pula cabeçalho

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

                    listaAlimento.adicionarAlimento(meal.alimento, meal.getUserId());
                    registros.put(meal.getUserId(), meal);

                } catch (Exception e) {
                    System.out.println("Erro ao processar linha: " + linha);
                }
            }

            // Agora, imediatamente salva em arquivos binários
            salvarListaInvertidaBinario();
            System.out.println("CSV carregado e lista invertida salva em binário com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao carregar CSV: " + e.getMessage());
        }
    }

    private static void buscarTermo() {
        System.out.print("Digite o termo a buscar: ");
        String termo = scanner.nextLine();

        List<Integer> ids = listaAlimento.buscar(termo);
        if (ids.isEmpty()) {
            System.out.println("Nenhum registro encontrado para o termo.");
        } else {
            System.out.println("IDs encontrados: " + ids);
            /*
             * for (int id : ids) {
             * System.out.println(registros.get(id));
             * }
             */
        }
    }

    private static void atualizarRegistro() {
        System.out.print("Informe o ID para atualizar o alimento: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Meal meal = registros.get(id);
        if (meal == null) {
            System.out.println("Registro não encontrado.");
            return;
        }

        listaAlimento.removerAlimento(meal.alimento, meal.getUserId());

        System.out.print("Novo alimento: ");
        String novoAlimento = scanner.nextLine();
        if (!novoAlimento.isEmpty()) {
            meal.alimento = novoAlimento;
        }

        listaAlimento.adicionarAlimento(meal.alimento, meal.getUserId());

        System.out.println("Registro atualizado na lista invertida.");
    }

    private static void deletarRegistro() {
        System.out.print("Informe o ID para deletar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Meal meal = registros.get(id);
        if (meal == null) {
            System.out.println("Registro não encontrado.");
            return;
        }

        listaAlimento.removerAlimento(meal.alimento, meal.getUserId());
        registros.remove(id);

        System.out.println("Registro removido da lista invertida.");
    }

    // Salvar a lista invertida em dois arquivos binários
    private static void salvarListaInvertidaBinario() {
        try (DataOutputStream dicionarioOut = new DataOutputStream(new FileOutputStream("dicionarioInvertido.bin"));
                DataOutputStream listasOut = new DataOutputStream(new FileOutputStream("listasInvertidas.bin"))) {

            for (Map.Entry<String, EntradaLista> entry : listaAlimento.getDicionario().entrySet()) {
                String termo = entry.getKey();
                List<Integer> referencias = entry.getValue().getReferencias();

                byte[] termoBytes = termo.getBytes("UTF-8");
                dicionarioOut.writeInt(termoBytes.length);
                dicionarioOut.write(termoBytes);

                long posicaoLista = listasOut.size();
                dicionarioOut.writeLong(posicaoLista);

                listasOut.writeInt(referencias.size());
                for (int id : referencias) {
                    listasOut.writeInt(id);
                }
            }
            System.out.println("Lista invertida salva em binário com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar lista invertida binária: " + e.getMessage());
        }
    }

    // Carregar a lista invertida a partir de dois arquivos binários
    private static void carregarListaInvertidaBinario() {
        try (DataInputStream dicionarioIn = new DataInputStream(new FileInputStream("dicionarioInvertido.dat"));
                RandomAccessFile listasIn = new RandomAccessFile("listasInvertidas.da", "r")) {

            listaAlimento.getDicionario().clear();

            while (dicionarioIn.available() > 0) {
                int tamanhoTermo = dicionarioIn.readInt();
                byte[] termoBytes = new byte[tamanhoTermo];
                dicionarioIn.readFully(termoBytes);
                String termo = new String(termoBytes, "UTF-8");

                long posicaoLista = dicionarioIn.readLong();

                listasIn.seek(posicaoLista);
                int quantidade = listasIn.readInt();

                EntradaLista entrada = new EntradaLista(termo);
                for (int i = 0; i < quantidade; i++) {
                    int id = listasIn.readInt();
                    entrada.adicionarReferencia(id);
                }

                listaAlimento.getDicionario().put(termo, entrada);
            }

            System.out.println("Lista invertida carregada do binário com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar lista invertida binária: " + e.getMessage());
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
}
