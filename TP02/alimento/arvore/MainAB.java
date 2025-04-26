package TP02.alimento.arvore;

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


public class MainAB {
    
    // Definição dos PATHS
    private static final String CSV_FILE = "TP01/alimento/daily_food_nutrition_dataset.csv";
    private static final String B_BIN_FILE = "meals.bin";
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArvoreB arvoreB = new ArvoreB(2);

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
                    carregarCSV_ArvoreB();
                    break;
                case 2:
                    lerRegistro_ArvoreB();
                    break;
                case 3:
                    atualizarRegistro_ArvoreB();
                    break;
                case 4:
                    deletarRegistro_ArvoreB();
                    break;
                case 5:
                    System.out.println("Digite o número de arquivos que voce deseja usar: ");
                    int numCaminhos = scanner.nextInt();

                    System.out.println("Digite o número de registros que voce deseja usar: ");
                    int maxRegistros = scanner.nextInt();

                    ordenacaoExterna_ArvoreB(numCaminhos, maxRegistros);
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
    private static void carregarCSV_ArvoreB() throws IOException {
        Files.deleteIfExists(Paths.get(B_BIN_FILE));
    
        try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE));
             RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "rw")) {
    
            String linha;
            int id = 1;
            raf.writeInt(0);
    
            br.readLine(); // Ignora cabeçalho
    
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
                    long posicao = raf.length();
    
                    raf.seek(posicao);
                    raf.writeByte(1); // Lápide
                    raf.writeInt(data.length);
                    raf.write(data);
    
                    raf.seek(0);
                    raf.writeInt(id);
                    id++;
    
                    arvoreB.inserir(meal.getUserId(), posicao);
    
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
    private static void lerRegistro_ArvoreB() throws IOException {
        System.out.print("Informe o ID para ler: ");
        int id = scanner.nextInt();
    
        Long pos = arvoreB.buscar(id);
    
        if (pos == null) {
            System.out.println("ID não encontrado na árvore B.");
            return;
        }
    
        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "r")) {
            raf.seek(pos);
            byte lapide = raf.readByte();
            int tamanho = raf.readInt();
            byte[] data = new byte[tamanho];
            raf.readFully(data);
    
            if (lapide == 1) {
                Meal meal = new Meal();
                meal.fromByteArray(data);
                System.out.println("==== Dados da refeição ====");
                System.out.println(meal);
            } else {
                System.out.println("Registro está marcado como removido.");
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o registro: " + e.getMessage());
        }
    }
    

    // =============================Atualizar Registro (Opção 3)===============================//
    private static void atualizarRegistro_ArvoreB() throws IOException {
        System.out.print("Informe o ID para atualizar: ");
        int id = scanner.nextInt();
        scanner.nextLine();
    
        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "rw")) {
            Long pos = arvoreB.buscar(id);
    
            if (pos == null) {
                System.out.println("Usuário não encontrado.");
                return;
            }
    
            raf.seek(pos);
            byte lapide = raf.readByte();
            int tamanho = raf.readInt();
            byte[] data = new byte[tamanho];
            raf.readFully(data);
    
            if (lapide == 0) {
                System.out.println("Este registro está inativo.");
                return;
            }
    
            Meal meal = new Meal();
            meal.fromByteArray(data);
    
            System.out.println("Digite os novos valores para a refeição (deixe em branco para manter o atual):");
    
            System.out.print("Nova Data: ");
            String novaDataStr = scanner.nextLine();
            if (!novaDataStr.isEmpty()) {
                SimpleDateFormat nova_data = new SimpleDateFormat("dd/MM/yyyy");
                meal.data = nova_data.parse(novaDataStr);
            }
    
            System.out.print("Novo alimento: ");
            String novoAlimento = scanner.nextLine();
            if (!novoAlimento.isEmpty()) meal.alimento = novoAlimento;
    
            System.out.print("Nova categoria: ");
            String novaCategoria = scanner.nextLine();
            if (!novaCategoria.isEmpty()) meal.categoria = novaCategoria;
    
            System.out.print("Nova caloria: ");
            String novaCaloria = scanner.nextLine();
            if (!novaCaloria.isEmpty()) meal.caloria = Integer.parseInt(novaCaloria);
    
            System.out.print("Nova proteína: ");
            String novaProteina = scanner.nextLine();
            if (!novaProteina.isEmpty()) meal.proteina = Double.parseDouble(novaProteina);
    
            System.out.print("Novo carboidrato: ");
            String novoCarbo = scanner.nextLine();
            if (!novoCarbo.isEmpty()) meal.carboidrato = Double.parseDouble(novoCarbo);
    
            System.out.print("Nova gordura: ");
            String novaGordura = scanner.nextLine();
            if (!novaGordura.isEmpty()) meal.gordura = Double.parseDouble(novaGordura);
    
            System.out.print("Nova fibra: ");
            String novaFibra = scanner.nextLine();
            if (!novaFibra.isEmpty()) meal.fibra = Double.parseDouble(novaFibra);
    
            System.out.print("Novo açúcar: ");
            String novoAcucar = scanner.nextLine();
            if (!novoAcucar.isEmpty()) meal.acucar = Double.parseDouble(novoAcucar);
    
            System.out.print("Novo sódio: ");
            String novoSodio = scanner.nextLine();
            if (!novoSodio.isEmpty()) meal.sodio = Integer.parseInt(novoSodio);
    
            System.out.print("Novo colesterol: ");
            String novoColesterol = scanner.nextLine();
            if (!novoColesterol.isEmpty()) meal.colesterol = Integer.parseInt(novoColesterol);
    
            System.out.print("Novo tipo: ");
            String novoTipo = scanner.nextLine();
            if (!novoTipo.isEmpty()) meal.tipo = novoTipo;
    
            System.out.print("Novo líquido: ");
            String novoLiquido = scanner.nextLine();
            if (!novoLiquido.isEmpty()) meal.liquido = Integer.parseInt(novoLiquido);
    
            byte[] novoData = meal.toByteArray();
    
            if (novoData.length <= data.length) {
                raf.seek(pos + 5);
                raf.write(novoData);
            } else {
                raf.seek(pos);
                raf.writeByte(0);

                long novaPosicao = raf.length();
                raf.seek(novaPosicao);

                raf.writeByte(1);
                raf.writeInt(novoData.length);
                raf.write(novoData);
                arvoreB.atualizar(id, meal.getUserId(), novaPosicao);
            }
    
            System.out.println("Registro atualizado com sucesso!");
    
        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }
    }
    

    // =============================Deletar Registro (Opção 4)===============================//
    private static void deletarRegistro_ArvoreB() throws IOException {
        System.out.print("Informe o ID para deletar: ");
        int id = scanner.nextInt();
    
        Long pos = arvoreB.buscar(id);
    
        if (pos == null) {
            System.out.println("ID não encontrado na árvore B.");
            return;
        }
    
        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "rw")) {
            raf.seek(pos);
            byte lapide = raf.readByte();
    
            if (lapide == 0) {
                System.out.println("O registro já está removido.");
                return;
            }
    
            raf.seek(pos);
            raf.writeByte(0);
            arvoreB.remover(id);
    
            System.out.println("Registro deletado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao acessar o arquivo: " + e.getMessage());
        }
    }
    
// =======================realizar ordenação externa (Opção 5)=================================//
    private static void ordenacaoExterna_ArvoreB(int numCaminhos, int maxRegistros) throws IOException {
        // Criação de lista de arquivos temporários para armazenar os blocos ordenados
        // o maxRegistros serão os registros que serão passados para a memória primaria
        // a cada ordenação
        List<File> arquivosTemporarios = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "r")) {
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

        reconstruirArvoreB();

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

        try (RandomAccessFile rafFinal = new RandomAccessFile(B_BIN_FILE, "rw")) {
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

    // Método usado na ordenação externa para refazer a arvoreB

    private static void reconstruirArvoreB() throws IOException {
        if (arvoreB == null) {
            ArvoreB arvoreB = new ArvoreB(2); 
        }
    
        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "r")) {
            raf.seek(4); 
    
            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.readFully(data);
    
                if (lapide == 1) {
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    arvoreB.inserir(meal.getUserId(), pos);
                }
            }
        }
    }
    
    

}