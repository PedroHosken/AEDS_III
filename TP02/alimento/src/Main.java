package TP02.alimento.src;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    private static final String CSV_FILE = "TP02/alimento/daily_food_nutrition_dataset.csv";
    private static final String B_BIN_FILE = "meals.bin";
    private static final Scanner scanner = new Scanner(System.in);
    private static final ArvoreB arvoreB = new ArvoreB(2);
    private static final String HASH_BIN_FILE = "meals_hash.bin";
    private static final Diretorio diretorio = new Diretorio(2, 4);
    private static final ListaInvertida listaAlimento = new ListaInvertida();
    private static final Map<Integer, Meal> registros = new HashMap<>();

    public static void main(String[] args) throws IOException {

        int escolhaIndice;

        do {
            System.out.println("\n--- Escolha o Tipo de Índice ---");
            System.out.println("1. Árvore B");
            System.out.println("2. Hash");
            System.out.println("3. Lista Invertida");
            System.out.println("4. Casamento de Padrões"); 
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            escolhaIndice = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (escolhaIndice) {
                case 1:
                    menuArvoreB(scanner);
                    break;
                case 2:
                    menuHash(scanner);
                    break;
                case 3:
                    menuListaInvertida(scanner);
                    break;
                case 4: 
                    menuCasamentoPadroes(scanner);
                    break;
                case 0:
                    System.out.println("Encerrando o programa...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (escolhaIndice != 0);

        scanner.close();
    }
    
    // ============================= MENU DO CASAMENTO DE PADRÕES ===============================//
    private static void menuCasamentoPadroes(Scanner scanner) {
        int opcao;
        do {
            System.out.println("\n--- Menu Casamento de Padrões ---");
            System.out.println("Qual algoritmo deseja usar para buscar um padrão no campo 'alimento'?");
            System.out.println("1. KMP (Knuth-Morris-Pratt)");
            System.out.println("2. Boyer-Moore");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (opcao) {
                case 1:
                    buscarPadrao("KMP");
                    break;
                case 2:
                    buscarPadrao("BoyerMoore");
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    // ============================= BUSCA DE PADRÃO ===============================//
    private static void buscarPadrao(String algoritmo) {
        System.out.print("Digite o padrão a ser buscado no campo 'alimento': ");
        String padrao = scanner.nextLine().toLowerCase(); // Busca case-insensitive

        if (padrao.isEmpty()) {
            System.out.println("O padrão não pode ser vazio.");
            return;
        }

        int matchesCount = 0;
        System.out.println("\nBuscando por '" + padrao + "' usando " + algoritmo + "...");

        try (RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "r")) {
            if (raf.length() == 0) {
                System.out.println("O arquivo " + B_BIN_FILE + " está vazio ou não existe. Carregue os dados primeiro (Menu Árvore B -> Opção 1).");
                return;
            }

            raf.seek(4); // Pula o cabeçalho do arquivo

            while (raf.getFilePointer() < raf.length()) {
                long pos = raf.getFilePointer();
                byte lapide = raf.readByte();
                int tamanho = raf.readInt();
                byte[] data = new byte[tamanho];
                raf.readFully(data);

                if (lapide == 1) { // Processa apenas registros ativos
                    Meal meal = new Meal();
                    meal.fromByteArray(data);
                    String textoAlimento = meal.alimento.toLowerCase(); // Busca case-insensitive

                    int foundIndex = -1;
                    if (algoritmo.equals("KMP")) {
                        foundIndex = KMP.search(textoAlimento, padrao);
                    } else if (algoritmo.equals("BoyerMoore")) {
                        foundIndex = BoyerMoore.search(textoAlimento, padrao);
                    }

                    if (foundIndex != -1) {
                        matchesCount++;
                        System.out.println("\n--- Padrão Encontrado (Registro ID: " + meal.getUserId() + " na posição " + pos + ") ---");
                        System.out.println(meal);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo binário: " + e.getMessage());
            System.out.println("Certifique-se de que o arquivo '" + B_BIN_FILE + "' foi criado corretamente (Menu Árvore B -> Opção 1).");
        }

        if (matchesCount > 0) {
            System.out.println("\nBusca concluída. Total de registros encontrados: " + matchesCount);
        } else {
            System.out.println("\nNenhum registro contendo o padrão '" + padrao + "' foi encontrado.");
        }
    }

    // ============================= MENU DA ARVORE B ===============================//

    private static void menuArvoreB(Scanner scanner) {
        int opcao;
        do {
            System.out.println("\n--- Menu Árvore B ---");
            System.out.println("1. Carregar CSV para Arquivo Binário");
            System.out.println("2. Ler Registro");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("5. Ordenação Externa");
            System.out.println("0. Voltar");
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
                    System.out.println("Digite o número de arquivos que você deseja usar: ");
                    int numCaminhos = scanner.nextInt();
                    System.out.println("Digite o número de registros que você deseja usar: ");
                    int maxRegistros = scanner.nextInt();
                    ordenacaoExterna_ArvoreB(numCaminhos, maxRegistros);
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        } while (opcao != 0);
    }

    // ============================= MENU DO HASH ===============================//


    private static void menuHash(Scanner scanner) throws IOException{
        int opcao;
        do {
            System.out.println("\n--- Menu Hash ---");
            System.out.println("1. Carregar CSV para Hash");
            System.out.println("2. Ler Registro");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
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
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }


    // ============================= MENU DA LISTA INVERTIDA ===============================//

    private static void menuListaInvertida(Scanner scanner) throws IOException{
        int opcao;
        do {
            System.out.println("\n--- Menu Lista Invertida (Alimento) ---");
            System.out.println("1. Carregar CSV para Lista Invertida");
            System.out.println("2. Buscar por Termo");
            System.out.println("3. Atualizar Registro");
            System.out.println("4. Deletar Registro");
            System.out.println("5. Buscar por Múltiplos Termos");
            System.out.println("0. Voltar");
            System.out.print("Escolha: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir quebra de linha

            switch (opcao) {
                case 1:
                    carregarCSV_ListaInvertida();
                    break;
                case 2:
                    buscarTermo_ListaInvertida();
                    break;
                case 3:
                    atualizarRegistro_ListaInvertida();
                    break;
                case 4:
                    deletarRegistro_ListaInvertida();
                    break;
                case 5:
                    buscarMultiplosTermos_ListaInvertida();
                    break;
                case 0:
                    System.out.println("Voltando ao menu principal...");
                    break;
                default:
                    System.out.println("Opção inválida.");
                    break;
            }
        } while (opcao != 0);
    }


    // =============================Carregar arquivo binário (Opção 1)===============================//

    private static void carregarCSV_ArvoreB() {
        try {
            Files.deleteIfExists(Paths.get(B_BIN_FILE)); // Deleta o arquivo binário, se existir
    
            try (BufferedReader br = Files.newBufferedReader(Paths.get(CSV_FILE)); 
                 RandomAccessFile raf = new RandomAccessFile(B_BIN_FILE, "rw")) {
    
                String linha;
                int id = 1;
                raf.writeInt(0);  // Inicializa o cabeçalho do arquivo binário
    
                br.readLine(); // Ignora o cabeçalho do CSV
    
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
    
                        byte[] data = meal.toByteArray(); // Converte o Meal para bytes
                        long posicao = raf.length(); // Posição atual no arquivo binário
    
                        raf.seek(posicao); // Muda para a posição correta no arquivo
                        raf.writeByte(1); // Lápide
                        raf.writeInt(data.length); // Tamanho dos dados
                        raf.write(data); // Escreve os dados no arquivo binário
    
                        raf.seek(0); // Retorna para o início do arquivo
                        raf.writeInt(id); // Escreve o ID
    
                        id++;
    
                        arvoreB.inserir(meal.getUserId(), posicao); // Insere na Árvore B
    
                    } catch (Exception e) {
                        System.out.println("Erro ao processar linha: " + linha + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao manipular arquivos: " + e.getMessage());
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
    private static void lerRegistro_ArvoreB() {
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
    private static void atualizarRegistro_ArvoreB() {
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
    
        } catch (IOException e) {
            System.out.println("Erro de I/O ao atualizar: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro ao atualizar o registro: " + e.getMessage());
        }
    }
    
    

    // =============================Deletar Registro (Opção 4)===============================//
    private static void deletarRegistro_ArvoreB() {
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
        } catch (Exception e) {
            System.out.println("Erro ao deletar o registro: " + e.getMessage());
        }
    }
    
// =======================realizar ordenação externa (Opção 5)=================================//
    private static void ordenacaoExterna_ArvoreB(int numCaminhos, int maxRegistros) {
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

            // Se houver registros restantes no buffer após a última leitura
            if (!buffer.isEmpty()) {
                arquivosTemporarios.add(escreverArquivoTemporario(buffer));
            }
        } catch (IOException e) {
            System.out.println("Erro ao processar o arquivo: " + e.getMessage());
            return; // Retorna para evitar o lançamento da exceção
        }

        // Intercalação dos arquivos ordenados
        try {
            intercalarArquivos(arquivosTemporarios, numCaminhos);
        } catch (IOException e) {
            System.out.println("Erro ao intercalar arquivos: " + e.getMessage());
            return; // Retorna para evitar o lançamento da exceção
        }

        // Reconstrução da árvore B após a ordenação
        try {
            reconstruirArvoreB();
        } catch (IOException e) {
            System.out.println("Erro ao reconstruir a árvore B: " + e.getMessage());
            return; // Retorna para evitar o lançamento da exceção
        }

        // Limpeza dos arquivos temporários
        for (File tempFile : arquivosTemporarios) {
            if (!tempFile.delete()) {
                System.out.println("Erro ao excluir arquivo temporário: " + tempFile.getAbsolutePath());
            }
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

                } catch (ParseException e) {
                    System.out.println("Erro ao processar a linha (data inválida): " + linha);
                } catch (NumberFormatException e) {
                    System.out.println("Erro ao processar a linha (número inválido): " + linha);
                } catch (Exception e) {
                    System.out.println("Erro inesperado na linha: " + linha + " - " + e.getMessage());
                }
            }

            System.out.println("CSV carregado no hash com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao carregar CSV: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erro inesperado ao carregar CSV: " + e.getMessage());
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


    private static void carregarCSV_ListaInvertida() throws IOException {
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

    private static void buscarTermo_ListaInvertida() {
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

    private static void atualizarRegistro_ListaInvertida() {
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

    private static void deletarRegistro_ListaInvertida() {
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

    private static void buscarMultiplosTermos_ListaInvertida() {
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
}