package TP03.compressao.lzw;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainLzw {

    private static final String ARQUIVO_ORIGINAL_PADRAO = "meals.bin";
    private static final String PREFIXO_ARQUIVO_COMPRIMIDO = "meals_lzw_v";
    private static final String EXTENSAO_ARQUIVO_COMPRIMIDO = ".lzw";
    private static final Scanner scanner = new Scanner(System.in);
    private static int proximaVersaoCompressao = 1;

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("\n--- Menu de Compressão LZW ---");
            System.out.println("1. Comprimir Arquivo com LZW");
            System.out.println("2. Descomprimir Arquivo com LZW");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            // Tratamento de entrada para garantir que seja um inteiro
            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();
            } else {
                System.out.println("Opção inválida. Por favor, insira um número.");
                scanner.next(); // Limpa a entrada inválida
                opcao = -1; // Define uma opção inválida para continuar o loop
            }
            scanner.nextLine(); // Consome a nova linha restante

            switch (opcao) {
                case 1:
                    comprimirArquivo();
                    break;
                case 2:
                    descomprimirArquivo();
                    break;
                case 0:
                    System.out.println("Saindo do programa...");
                    break;
                default:
                    if (opcao != -1) {
                        System.out.println("Opção inválida. Tente novamente.");
                    }
                    break;
            }
        } while (opcao != 0);
        scanner.close();
    }

    /**
     * Orquestra a compressão de um arquivo usando LZW.
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

        String nomeArquivoComprimido = PREFIXO_ARQUIVO_COMPRIMIDO + (proximaVersaoCompressao++)
                + EXTENSAO_ARQUIVO_COMPRIMIDO;
        System.out.println("Comprimindo para o arquivo: " + nomeArquivoComprimido);

        long tempoInicio = System.currentTimeMillis();

        try {
            // Chama o método de compressão LZW
            Lzw.compress(nomeArquivoOriginal, nomeArquivoComprimido);

            long tempoFim = System.currentTimeMillis();
            double tempoTotalSegundos = (tempoFim - tempoInicio) / 1000.0;

            // Exibe os resultados
            long tamanhoOriginalBytes = arquivoOriginal.length();
            long tamanhoComprimidoBytes = new File(nomeArquivoComprimido).length();

            System.out.println("\n--- Resultados da Compressão LZW ---");
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
            System.err.println("Erro durante a compressão com LZW: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Orquestra a descompressão de um arquivo LZW.
     */
    private static void descomprimirArquivo() {
        System.out.print("Digite o nome do arquivo a ser descomprimido (ex: " + PREFIXO_ARQUIVO_COMPRIMIDO + "1"
                + EXTENSAO_ARQUIVO_COMPRIMIDO + "): ");
        String nomeArquivoComprimido = scanner.nextLine();

        File arquivoComprimido = new File(nomeArquivoComprimido);
        if (!arquivoComprimido.exists()) {
            System.out.println("Erro: Arquivo comprimido '" + nomeArquivoComprimido + "' não encontrado.");
            return;
        }

        // Define o nome padrão para o arquivo de saída
        String nomeArquivoDescomprimido = "descomprimido_"
                + arquivoComprimido.getName().replace(EXTENSAO_ARQUIVO_COMPRIMIDO, ".bin");
        System.out.println("O arquivo será salvo como: " + nomeArquivoDescomprimido);

        long tempoInicio = System.currentTimeMillis();

        try {
            // Chama o método de descompressão LZW
            Lzw.decompress(nomeArquivoComprimido, nomeArquivoDescomprimido);

            long tempoFim = System.currentTimeMillis();
            double tempoTotalSegundos = (tempoFim - tempoInicio) / 1000.0;

            System.out.println("\n--- Resultados da Descompressão LZW ---");
            System.out.println("Arquivo Comprimido: " + nomeArquivoComprimido);
            System.out.println("Arquivo Descomprimido: " + nomeArquivoDescomprimido + " ("
                    + new File(nomeArquivoDescomprimido).length() + " bytes)");
            System.out.printf("Tempo de Descompressão: %.3f segundos\n", tempoTotalSegundos);

        } catch (IOException e) {
            System.err.println("Erro durante a descompressão com LZW: " + e.getMessage());
            e.printStackTrace();
        }
    }
}