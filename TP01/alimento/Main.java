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
            System.out.println("5. Ordenação Externa");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    carregarCSV();
                    break;
                case 2:
                    lerRegistro();
                    break;
                case 3:
                    atualizarRegistro();
                    break;
                case 4:
                    deletarRegistro();
                    break;
                case 5:
                    System.out.println("Digite o número de arquivos que voce deseja usar: ");
                    int numCaminhos = scanner.nextInt();

                    System.out.println("Digite o número de registros que voce deseja usar: ");
                    int maxRegistros = scanner.nextInt();

                    ordenacaoExterna(numCaminhos, maxRegistros);
                    break;

                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    // =============================Carregar CSV para conversão em arquivo binário (Opção 1)===============================//
    private static void carregarCSV() throws IOException {

        Files.deleteIfExists(Paths.get(BIN_FILE));

        try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE));
                RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "rw")) {

            String linha;

            int id = 1;
            raf.writeInt(0);

            br.readLine();

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

                    raf.seek(raf.length());
                    raf.writeByte(1); // Lápide (1 = ativo)
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

    // =============================Ler Registro (Opção 2)===============================//
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

    // =============================Atualizar Registro (Opção 3)===============================//
    private static void atualizarRegistro() throws IOException {
        System.out.print("Informe o ID para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try (RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "rw")) {
            raf.seek(4);

            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.read(data);

                if (lapide == 1) { // Apenas registros ativos
                    Meal meal = new Meal();
                    meal.fromByteArray(data);

                    if (meal.usuario == id) { // Atualiza todos com esse usuário
                        System.out.println(
                                "Digite os novos valores para a refeição (deixe em branco para manter o atual):");

                        System.out.println("Nova Data: ");
                        String novaDataStr = scanner.nextLine();
                        if (!novaDataStr.isEmpty()) {
                            SimpleDateFormat nova_data = new SimpleDateFormat("dd/MM/yyyy");
                            meal.data = nova_data.parse(novaDataStr);
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

                        // Gerar novo array de bytes
                        byte[] novoData = meal.toByteArray();

                        if (novoData.length <= data.length) {
                            // Atualizar no mesmo espaço se couber
                            raf.seek(pos + 5); // Volta para posição exata do dado (pula lápide e tamanho)
                            raf.write(novoData);
                        } else {
                            // Caso não caiba, marcar o atual como inativo e mover para o final
                            raf.seek(pos);
                            raf.writeByte(0); // Marca como inativo
                            raf.seek(raf.length()); // Ir para o final
                            raf.writeByte(1); // Nova lápide ativa
                            raf.writeInt(novoData.length);
                            raf.write(novoData);
                        }
                    }
                }
            }
            System.out.println("Registros atualizados com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao atualizar registros: " + e.getMessage());
        }
    }

    // =============================Deletar Registro (Opção 4)===============================//
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
// =======================realizar ordenação externa (Opção 5)=================================//
    private static void ordenacaoExterna(int numCaminhos, int maxRegistros) throws IOException {
        // Criação de lista de arquivos temporários para armazenar os blocos ordenados
        // o maxRegistros serão os registros que serão passados para a memória primaria
        // a cada ordenação
        List<File> arquivosTemporarios = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(BIN_FILE, "r")) {
            raf.seek(4); // Pula o cabeçalho do arquivo

            List<Meal> buffer = new ArrayList<>(); // Buffer em memória primaria que vai armazenar temp os registros
                                                   // lidos
            while (raf.getFilePointer() < raf.length()) {
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.read(data);

                // teste para ver se o registro não conteve a lapide marcada para ser passado
                // para o novo arquivo
                if (lapide == 1) { // Ignora registros deletados
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    buffer.add(meal);
                }

                if (buffer.size() >= maxRegistros) {
                    arquivosTemporarios.add(escreverArquivoTemporario(buffer));
                    buffer.clear();
                }
            }

            if (!buffer.isEmpty()) {
                arquivosTemporarios.add(escreverArquivoTemporario(buffer));
            }
        }

        // Intercalação dos arquivos ordenados
        intercalarArquivos(arquivosTemporarios, numCaminhos);

        // Limpeza dos arquivos temporários
        for (File tempFile : arquivosTemporarios) {
            tempFile.delete();
        }
    }

    // Uso do merge Sort para melhorar a ordenação, pois o Merge Sort garante mais estabilidade
    private static void mergeSort(List<Meal> registros, int esquerda, int direita) {
        if (esquerda < direita) {
            int meio = (esquerda + direita) / 2;
    
            mergeSort(registros, esquerda, meio);
            mergeSort(registros, meio + 1, direita);
    
            merge(registros, esquerda, meio, direita);
        }
    }
    
    private static void merge(List<Meal> registros, int esquerda, int meio, int direita) {
        int n1 = meio - esquerda + 1;
        int n2 = direita - meio;
    
        List<Meal> esquerdaLista = new ArrayList<>(n1);
        List<Meal> direitaLista = new ArrayList<>(n2);
    
        for (int i = 0; i < n1; i++) {
            esquerdaLista.add(registros.get(esquerda + i));
        }
        for (int j = 0; j < n2; j++) {
            direitaLista.add(registros.get(meio + 1 + j));
        }
    
        int i = 0, j = 0, k = esquerda;
        while (i < n1 && j < n2) {
            if (esquerdaLista.get(i).usuario <= direitaLista.get(j).usuario) {
                registros.set(k, esquerdaLista.get(i));
                i++;
            } else {
                registros.set(k, direitaLista.get(j));
                j++;
            }
            k++;
        }
    
        while (i < n1) {
            registros.set(k, esquerdaLista.get(i));
            i++;
            k++;
        }
    
        while (j < n2) {
            registros.set(k, direitaLista.get(j));
            j++;
            k++;
        }
    }
    
    // Meotodo de Ordenação interna
    private static File escreverArquivoTemporario(List<Meal> registros) throws IOException {
        mergeSort(registros, 0, registros.size() - 1); 
    
        File tempFile = File.createTempFile("temp", ".bin");
    
        try (RandomAccessFile raf = new RandomAccessFile(tempFile, "rw")) {
            for (Meal meal : registros) {
                byte[] data = meal.toByteArray();
                raf.writeByte(1);  // Mantém ativo
                raf.writeInt(data.length);
                raf.write(data);
            }
        }
        return tempFile;
    }
    
    // funcao responsável pela ordenação dos diferentes arquivos criados em apenas um completamente ordenado
    private static void intercalarArquivos(List<File> arquivos, int numCaminhos) throws IOException {
        
        // Uso de fila de prioridade -- o menor elemento de acordo com o campo usuário vai sempre ficar no topo,
        // assim garantindo a ordenação
        PriorityQueue<DataPointer> fila = new PriorityQueue<>(Comparator.comparingInt(dp -> dp.meal.usuario));
        List<RandomAccessFile> readers = new ArrayList<>();

        try (RandomAccessFile rafFinal = new RandomAccessFile(BIN_FILE, "rw")) {
            rafFinal.setLength(0);
            rafFinal.writeInt(0);

            for (File arquivo : arquivos) {
                RandomAccessFile raf = new RandomAccessFile(arquivo, "r");
                readers.add(raf);
                carregarProximoRegistro(raf, fila);
            }

            while (!fila.isEmpty()) {
                DataPointer dp = fila.poll();
                byte[] data = dp.meal.toByteArray();

                rafFinal.writeByte(1);
                rafFinal.writeInt(data.length);
                rafFinal.write(data);

                if (dp.reader.getFilePointer() < dp.reader.length()) {
                    carregarProximoRegistro(dp.reader, fila);
                } else {
                    dp.reader.close();
                }
            }
        }

        for (RandomAccessFile raf : readers) {
            raf.close();
        }
    }

   
    private static void carregarProximoRegistro(RandomAccessFile raf, PriorityQueue<DataPointer> fila)
            throws IOException {
        // leitura da lapide
        byte lapide = raf.readByte(); 
        int tamanho = raf.readInt();
        byte[] data = new byte[tamanho];
        raf.read(data);

        // Se o registro estiver ativo, adiciona à fila de prioridade
        if (lapide == 1) {
            Meal meal = new Meal();
            // Converte os bytes em um objeto Meal
            meal.fromByteArray(data); 
            fila.add(new DataPointer(meal, raf));
        }
    }

    //   Classe auxiliar para armazenar um registro Meal e o arquivo correspondente de
    //   onde ele foi lido.
    
    private static class DataPointer {
        Meal meal;
        RandomAccessFile reader;

        DataPointer(Meal meal, RandomAccessFile reader) {
            this.meal = meal;
            this.reader = reader;
        }
    }

}