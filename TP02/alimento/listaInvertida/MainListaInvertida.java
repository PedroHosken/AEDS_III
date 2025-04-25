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
            System.out.println("5. Imprimir Lista Invertida");
            System.out.println("6. Salvar Lista Invertida");
            System.out.println("7. Carregar Lista Invertida");
            System.out.println("8. Buscar por Múltiplos Termos");

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
                case 5:
                    listaAlimento.imprimir();
                    break;
                case 6:
                    salvarLista();
                    break;
                case 7:
                    carregarLista();
                    break;
                case 8:
                    buscarMultiplosTermos();
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
            System.out.println("CSV carregado na lista invertida com sucesso!");
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
            for (int id : ids) {
                System.out.println(registros.get(id));
            }
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

    private static void salvarLista() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("listaInvertida.dat"))) {
            oos.writeObject(listaAlimento);
            System.out.println("Lista invertida salva com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar a lista invertida: " + e.getMessage());
        }
    }

    private static void carregarLista() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("listaInvertida.dat"))) {
            ListaInvertida carregada = (ListaInvertida) ois.readObject();
            listaAlimento.getDicionario().clear();
            listaAlimento.getDicionario().putAll(carregada.getDicionario());
            System.out.println("Lista invertida carregada com sucesso!");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar a lista invertida: " + e.getMessage());
        }
    }

    private static void buscarMultiplosTermos() {
        System.out.print("Digite os termos separados por espaço: ");
        String linha = scanner.nextLine();
        String[] termos = linha.toLowerCase().split("\\s+");

        List<Integer> ids = listaAlimento.buscarIntersecao(termos);

        if (ids.isEmpty()) {
            System.out.println("Nenhum registro encontrado que contenha todos os termos.");
        } else {
            System.out.println("IDs encontrados: " + ids);
            for (int id : ids) {
                System.out.println(registros.get(id));
            }
        }
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
