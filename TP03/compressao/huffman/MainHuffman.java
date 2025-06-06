package TP03.compressao.huffman; // Ajuste o pacote conforme necessário

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MainHuffman {

    private static final String ARQUIVO_ORIGINAL_PADRAO = "meals.bin";
    private static final String PREFIXO_ARQUIVO_COMPRIMIDO = "meals_huffman_v";
    private static final String EXTENSAO_ARQUIVO_COMPRIMIDO = ".huff";
    private static final Scanner scanner = new Scanner(System.in);
    private static int proximaVersaoCompressao = 1;

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n--- Menu de Compressão Huffman ---");
            System.out.println("1. Comprimir Arquivo");
            System.out.println("2. Descomprimir Arquivo");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine(); // Consumir nova linha

            switch (opcao) {
                case 1:
                    comprimirArquivo();
                    break;
                case 2:
                    try {
                        descomprimirArquivo();
                    } catch (IOException | ClassNotFoundException e) {
                        System.err.println("Erro durante a descompressão: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
        scanner.close();
    }

    /**
     * Comprime um arquivo utilizando o algoritmo de Huffman.
     */
    private static void comprimirArquivo() {
        System.out
                .print("Digite o nome do arquivo original para comprimir (padrão: " + ARQUIVO_ORIGINAL_PADRAO + "): ");
        String nomeArquivoOriginal = scanner.nextLine();
        if (nomeArquivoOriginal.isEmpty()) {
            nomeArquivoOriginal = ARQUIVO_ORIGINAL_PADRAO;
        }

        File arquivoOriginal = new File(nomeArquivoOriginal);
        if (!arquivoOriginal.exists()) {
            System.out.println("Erro: Arquivo original '" + nomeArquivoOriginal + "' não encontrado.");
            return;
        }

        long tempoInicio = System.currentTimeMillis();

        try {
            // 1. Análise de Frequência
            Map<Byte, Integer> mapaFrequencias = calcularFrequencias(nomeArquivoOriginal);
            if (mapaFrequencias.isEmpty()) {
                System.out.println("Arquivo original vazio ou sem dados para compressão.");
                return;
            }

            // 2. Construção da Árvore de Huffman
            HuffmanTree arvoreHuffman = new HuffmanTree(mapaFrequencias);
            if (arvoreHuffman.raiz == null) {
                System.out.println("Falha ao construir a árvore de Huffman.");
                return;
            }

            // 3. Geração dos Códigos
            Map<Byte, String> mapaCodigos = arvoreHuffman.gerarMapaDeCodigos();

            // 4. Escrita do Arquivo Comprimido
            String nomeArquivoComprimido = PREFIXO_ARQUIVO_COMPRIMIDO + (proximaVersaoCompressao++)
                    + EXTENSAO_ARQUIVO_COMPRIMIDO;

            long tamanhoOriginalBytes = arquivoOriginal.length();
            long tamanhoComprimidoBytes = escreverArquivoComprimido(nomeArquivoOriginal, nomeArquivoComprimido,
                    mapaFrequencias, mapaCodigos);

            long tempoFim = System.currentTimeMillis();
            double tempoTotalSegundos = (tempoFim - tempoInicio) / 1000.0;

            // 5. Exibição de Resultados
            System.out.println("\n--- Resultados da Compressão ---");
            System.out.println("Arquivo Original: " + nomeArquivoOriginal + " (" + tamanhoOriginalBytes + " bytes)");
            System.out.println(
                    "Arquivo Comprimido: " + nomeArquivoComprimido + " (" + tamanhoComprimidoBytes + " bytes)");

            double taxaCompressao = 0;
            if (tamanhoOriginalBytes > 0) {
                taxaCompressao = 100.0 * (tamanhoOriginalBytes - tamanhoComprimidoBytes) / tamanhoOriginalBytes;
            }
            System.out.printf("Taxa de Compressão: %.2f%%\n", taxaCompressao);
            System.out.printf("Tempo de Compressão: %.3f segundos\n", tempoTotalSegundos);

        } catch (IOException e) {
            System.err.println("Erro durante a compressão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Calcula a frequência de cada byte em um arquivo.
     * 
     * @param nomeArquivo O nome do arquivo a ser lido.
     * @return Um mapa de Byte para Integer representando as frequências.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private static Map<Byte, Integer> calcularFrequencias(String nomeArquivo) throws IOException {
        Map<Byte, Integer> mapaFrequencias = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(nomeArquivo)) {
            int byteLido;
            while ((byteLido = fis.read()) != -1) {
                mapaFrequencias.put((byte) byteLido, mapaFrequencias.getOrDefault((byte) byteLido, 0) + 1);
            }
        }
        return mapaFrequencias;
    }

    /**
     * Escreve o arquivo comprimido, incluindo o cabeçalho com o mapa de frequências
     * e os dados codificados.
     * 
     * @param nomeArquivoOriginal   Nome do arquivo original.
     * @param nomeArquivoComprimido Nome do arquivo comprimido a ser gerado.
     * @param mapaFrequencias       Mapa de frequências para ser salvo no cabeçalho.
     * @param mapaCodigos           Mapa de códigos de Huffman para codificar os
     *                              dados.
     * @return O tamanho do arquivo comprimido em bytes.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private static long escreverArquivoComprimido(String nomeArquivoOriginal, String nomeArquivoComprimido,
            Map<Byte, Integer> mapaFrequencias, Map<Byte, String> mapaCodigos) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivoComprimido);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                FileInputStream fis = new FileInputStream(nomeArquivoOriginal)) {

            oos.writeObject(mapaFrequencias);

            long totalSimbolos = 0;
            for (int freq : mapaFrequencias.values()) {
                totalSimbolos += freq;
            }
            oos.writeLong(totalSimbolos);
            oos.flush(); // Garante que o cabeçalho seja escrito antes dos dados de bits

            byte bufferEscrita = 0;
            int contadorBits = 0;
            int byteLido;

            while ((byteLido = fis.read()) != -1) {
                String codigo = mapaCodigos.get((byte) byteLido);
                if (codigo == null) {
                    throw new IOException("Código Huffman não encontrado para o byte: " + byteLido +
                            ". Verifique a geração de códigos, especialmente para arquivos com um único tipo de byte.");
                }

                for (char bitChar : codigo.toCharArray()) {
                    bufferEscrita = (byte) (bufferEscrita << 1);
                    if (bitChar == '1') {
                        bufferEscrita = (byte) (bufferEscrita | 1);
                    }
                    contadorBits++;

                    if (contadorBits == 8) {
                        fos.write(bufferEscrita);
                        bufferEscrita = 0;
                        contadorBits = 0;
                    }
                }
            }

            if (contadorBits > 0) {
                bufferEscrita = (byte) (bufferEscrita << (8 - contadorBits));
                fos.write(bufferEscrita);
            }
        }
        return new File(nomeArquivoComprimido).length();
    }

    /**
     * Descomprime um arquivo previamente comprimido com Huffman.
     * 
     * @throws IOException            Se ocorrer um erro de I/O durante a leitura ou
     *                                escrita dos arquivos.
     * @throws ClassNotFoundException Se a classe do objeto serializado (mapa de
     *                                frequências) não for encontrada.
     */
    @SuppressWarnings("unchecked") // Para o cast do readObject()
    private static void descomprimirArquivo() throws IOException, ClassNotFoundException {
        System.out.print("Digite o nome do arquivo a ser descomprimido (ex: " + PREFIXO_ARQUIVO_COMPRIMIDO + "1"
                + EXTENSAO_ARQUIVO_COMPRIMIDO + "): ");
        String nomeArquivoComprimido = scanner.nextLine();

        File arquivoComprimido = new File(nomeArquivoComprimido);
        if (!arquivoComprimido.exists()) {
            System.out.println("Erro: Arquivo comprimido '" + nomeArquivoComprimido + "' não encontrado.");
            return;
        }

        System.out.print("Digite o nome para o arquivo descomprimido (padrão: " + ARQUIVO_ORIGINAL_PADRAO + "): ");
        String nomeArquivoDescomprimido = scanner.nextLine();
        if (nomeArquivoDescomprimido.isEmpty()) {
            nomeArquivoDescomprimido = ARQUIVO_ORIGINAL_PADRAO;
        }

        long tempoInicio = System.currentTimeMillis();
        System.out.println(
                "Iniciando descompressão de " + nomeArquivoComprimido + " para " + nomeArquivoDescomprimido + "...");

        try (FileInputStream fis = new FileInputStream(nomeArquivoComprimido);
                ObjectInputStream ois = new ObjectInputStream(fis);
                FileOutputStream fos = new FileOutputStream(nomeArquivoDescomprimido)) {

            Map<Byte, Integer> mapaFrequencias = (Map<Byte, Integer>) ois.readObject();
            long totalSimbolosOriginal = ois.readLong();

            if (mapaFrequencias.isEmpty() || totalSimbolosOriginal == 0) {
                System.out.println("Cabeçalho do arquivo comprimido inválido ou indica um arquivo original vazio.");
                // Cria um arquivo vazio se for o caso, pois o FileOutputStream já foi aberto.
                return;
            }

            HuffmanTree arvoreHuffman = new HuffmanTree(mapaFrequencias);
            if (arvoreHuffman.raiz == null) {
                System.out.println("Não foi possível reconstruir a árvore de Huffman a partir do cabeçalho.");
                return;
            }

            // Caso especial: se a árvore tem apenas um nó (raiz é uma folha),
            // significa que o arquivo original continha apenas um tipo de byte repetido.
            // Todos os bits decodificados (geralmente "0") levarão a este símbolo.
            boolean arvoreDeUmNoSo = arvoreHuffman.raiz.isFolha();

            HuffmanNode noAtual = arvoreHuffman.raiz;
            int byteLido;
            long simbolosDecodificados = 0;

            while (simbolosDecodificados < totalSimbolosOriginal && (byteLido = fis.read()) != -1) {
                for (int i = 7; i >= 0; i--) {
                    if (simbolosDecodificados == totalSimbolosOriginal) {
                        break;
                    }

                    int bit = (byteLido >> i) & 1;

                    if (arvoreDeUmNoSo) { // Se a árvore tem apenas um nó, qualquer bit decodifica para o símbolo da
                                          // raiz
                        noAtual = arvoreHuffman.raiz; // Garante que sempre aponta para a raiz/folha
                    } else if (bit == 0) {
                        noAtual = noAtual.esquerda;
                    } else {
                        noAtual = noAtual.direita;
                    }

                    if (noAtual == null) {
                        // Isso pode indicar um problema com a árvore ou o fluxo de bits se não for uma
                        // árvore de um nó só.
                        // Para árvores de um nó só, esta condição não deve ser atingida se noAtual é
                        // sempre resetado para raiz.
                        throw new IOException("Erro na decodificação: nó atual tornou-se nulo inesperadamente.");
                    }

                    if (noAtual.isFolha()) {
                        fos.write(noAtual.simbolo);
                        simbolosDecodificados++;
                        noAtual = arvoreHuffman.raiz;
                    }
                }
            }

            if (simbolosDecodificados != totalSimbolosOriginal) {
                System.err.println("Aviso: O número de símbolos decodificados (" + simbolosDecodificados +
                        ") não corresponde ao total esperado (" + totalSimbolosOriginal + ").");
            }

            long tempoFim = System.currentTimeMillis();
            double tempoTotalSegundos = (tempoFim - tempoInicio) / 1000.0;

            System.out.println("\n--- Resultados da Descompressão ---");
            System.out.println("Arquivo Comprimido: " + nomeArquivoComprimido);
            System.out.println("Arquivo Descomprimido: " + nomeArquivoDescomprimido + " ("
                    + new File(nomeArquivoDescomprimido).length() + " bytes)");
            System.out.printf("Tempo de Descompressão: %.3f segundos\n", tempoTotalSegundos);
            System.out.println("Descompressão concluída!");

        } // try-with-resources fecha fis, ois, fos automaticamente
    }
}