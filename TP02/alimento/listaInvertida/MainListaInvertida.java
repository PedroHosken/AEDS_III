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
            System.out.println("5. Buscar por Múltiplos Termos");
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
        System.out.print("Informe o ID para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consumir quebra de linha

        Meal meal = registros.get(id);
        if (meal == null) {
            System.out.println("Registro não encontrado.");
            return;
        }

        String alimentoAntigo = meal.alimento; // Guardar o alimento antigo

        System.out.println("Digite os novos valores (pressione ENTER para manter o valor atual):");

        try {
            System.out.print("Nova Data (dd/MM/yyyy): ");
            String novaDataStr = scanner.nextLine();
            if (!novaDataStr.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                meal.data = sdf.parse(novaDataStr);
            }

            System.out.print("Novo alimento: ");
            String novoAlimento = scanner.nextLine();
            if (!novoAlimento.isEmpty()) {
                meal.alimento = novoAlimento;
            }

            System.out.print("Nova categoria: ");
            String novaCategoria = scanner.nextLine();
            if (!novaCategoria.isEmpty()) {
                meal.categoria = novaCategoria;
            }

            System.out.print("Nova caloria: ");
            String novaCaloria = scanner.nextLine();
            if (!novaCaloria.isEmpty()) {
                meal.caloria = Integer.parseInt(novaCaloria);
            }

            System.out.print("Nova proteína: ");
            String novaProteina = scanner.nextLine();
            if (!novaProteina.isEmpty()) {
                meal.proteina = Double.parseDouble(novaProteina);
            }

            System.out.print("Novo carboidrato: ");
            String novoCarbo = scanner.nextLine();
            if (!novoCarbo.isEmpty()) {
                meal.carboidrato = Double.parseDouble(novoCarbo);
            }

            System.out.print("Nova gordura: ");
            String novaGordura = scanner.nextLine();
            if (!novaGordura.isEmpty()) {
                meal.gordura = Double.parseDouble(novaGordura);
            }

            System.out.print("Nova fibra: ");
            String novaFibra = scanner.nextLine();
            if (!novaFibra.isEmpty()) {
                meal.fibra = Double.parseDouble(novaFibra);
            }

            System.out.print("Novo açúcar: ");
            String novoAcucar = scanner.nextLine();
            if (!novoAcucar.isEmpty()) {
                meal.acucar = Double.parseDouble(novoAcucar);
            }

            System.out.print("Novo sódio: ");
            String novoSodio = scanner.nextLine();
            if (!novoSodio.isEmpty()) {
                meal.sodio = Integer.parseInt(novoSodio);
            }

            System.out.print("Novo colesterol: ");
            String novoColesterol = scanner.nextLine();
            if (!novoColesterol.isEmpty()) {
                meal.colesterol = Integer.parseInt(novoColesterol);
            }

            System.out.print("Novo tipo: ");
            String novoTipo = scanner.nextLine();
            if (!novoTipo.isEmpty()) {
                meal.tipo = novoTipo;
            }

            System.out.print("Novo líquido: ");
            String novoLiquido = scanner.nextLine();
            if (!novoLiquido.isEmpty()) {
                meal.liquido = Integer.parseInt(novoLiquido);
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar dados: " + e.getMessage());
            return;
        }

        // Atualizar a lista invertida SE o alimento foi alterado
        if (!alimentoAntigo.equalsIgnoreCase(meal.alimento)) {
            listaAlimento.removerAlimento(alimentoAntigo, meal.getUserId());
            listaAlimento.adicionarAlimento(meal.alimento, meal.getUserId());
        }

        // Atualizar o HashMap de registros (já está atualizado pois mexemos direto no
        // objeto)

        System.out.println("Registro atualizado com sucesso!");
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

    private static void buscarMultiplosTermos() {
        System.out.print("Digite os termos separados por espaço: ");
        String linha = scanner.nextLine();
        String[] termos = linha.toLowerCase().split("\\s+");

        Map<String, List<Integer>> resultadosIndividuais = new HashMap<>();
        Set<Integer> uniao = new HashSet<>();
        List<Integer> intersecao = null;

        // Buscar IDs para cada termo individualmente
        for (String termo : termos) {
            List<Integer> ids = listaAlimento.buscar(termo);

            resultadosIndividuais.put(termo, ids);
            uniao.addAll(ids);

            if (intersecao == null) {
                intersecao = new ArrayList<>(ids);
            } else {
                intersecao.retainAll(ids); // interseção: só mantém quem aparece em todos
            }
        }

        // Mostrar resultados individuais
        System.out.println("\nResultados individuais por termo:");
        for (String termo : termos) {
            System.out.println("- Termo '" + termo + "' → IDs: " + resultadosIndividuais.get(termo));
        }

        // Mostrar total (união)
        System.out.println("\nTotal de IDs encontrados (união de todos os termos): " + uniao);

        // Mostrar interseção
        if (intersecao == null || intersecao.isEmpty()) {
            System.out.println("\nNenhum ID encontrado em comum (interseção vazia).");
        } else {
            System.out.println("\nIDs encontrados na interseção (presentes em todos os termos): " + intersecao);
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
