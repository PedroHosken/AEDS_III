package TP03.compressao.lzw;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lzw {

    /**
     * Comprime um arquivo usando o algoritmo LZW.
     * 
     * @param arquivoEntrada O caminho do arquivo a ser comprimido.
     * @param arquivoSaida   O caminho do arquivo comprimido que será gerado.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    public static void compress(String arquivoEntrada, String arquivoSaida) throws IOException {

        int tamanhoDicionario = 256;
        Map<List<Byte>, Integer> dicionario = new HashMap<>();

        for (int i = 0; i < 256; i++) {
            List<Byte> sequencia = new ArrayList<>();
            sequencia.add((byte) i);
            dicionario.put(sequencia, i);
        }

        List<Byte> p = new ArrayList<>(); // Representa a sequência P atual

        try (InputStream is = new FileInputStream(arquivoEntrada);
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(arquivoSaida)))) {

            int byteLido;
            while ((byteLido = is.read()) != -1) {
                byte c = (byte) byteLido;

                List<Byte> pMaisC = new ArrayList<>(p);
                pMaisC.add(c);

                if (dicionario.containsKey(pMaisC)) {
                    p = pMaisC;
                } else {
                    dos.writeInt(dicionario.get(p));

                    // Adiciona a nova sequência (P+C) ao dicionário.
                    // Em implementações avançadas, o dicionário pode ser limitado ou reiniciado.
                    // Aqui, permitimos que ele cresça.
                    dicionario.put(pMaisC, tamanhoDicionario++);

                    p = new ArrayList<>();
                    p.add(c);
                }
            }

            // Escreve o código da última sequência P, se ela não for vazia
            if (!p.isEmpty()) {
                dos.writeInt(dicionario.get(p));
            }
        }
    }

    /**
     * Descomprime um arquivo usando o algoritmo LZW.
     * 
     * @param arquivoEntrada O caminho do arquivo comprimido.
     * @param arquivoSaida   O caminho onde o arquivo descomprimido será salvo.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    public static void decompress(String arquivoEntrada, String arquivoSaida) throws IOException {

        // 1. Inicialização do Dicionário
        int tamanhoDicionario = 256;
        Map<Integer, List<Byte>> dicionario = new HashMap<>();

        for (int i = 0; i < 256; i++) {
            List<Byte> sequencia = new ArrayList<>();
            sequencia.add((byte) i);
            dicionario.put(i, sequencia);
        }

        try (DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(arquivoEntrada)));
                OutputStream os = new FileOutputStream(arquivoSaida)) {

            // 2. Processamento do primeiro código
            int codigoAnterior = dis.readInt();
            List<Byte> p = new ArrayList<>(dicionario.get(codigoAnterior));
            for (byte b : p) {
                os.write(b);
            }

            // 3. Processamento dos códigos restantes
            while (true) { // O loop será interrompido pela EOFException
                int codigoAtual = dis.readInt();
                List<Byte> s;

                // 4. Verificação no Dicionário
                if (dicionario.containsKey(codigoAtual)) {
                    // Caso normal: o código já existe no dicionário
                    s = dicionario.get(codigoAtual);
                } else if (codigoAtual == tamanhoDicionario) {
                    // Caso especial (KwKwK): o código é o próximo a ser adicionado
                    s = new ArrayList<>(p);
                    s.add(p.get(0));
                } else {
                    throw new IOException("Erro de descompressão: código inválido " + codigoAtual +
                            " encontrado no arquivo. O arquivo pode estar corrompido.");
                }

                // Escreve a sequência decodificada na saída
                for (byte b : s) {
                    os.write(b);
                }

                // Adiciona a nova sequência ao dicionário: P + primeiro_byte_de_S
                List<Byte> novaSequencia = new ArrayList<>(p);
                novaSequencia.add(s.get(0));
                dicionario.put(tamanhoDicionario++, novaSequencia);

                // Atualiza a sequência anterior (P) para ser a sequência atual (S)
                p = s;
            }

        } catch (EOFException e) {
            // Fim do arquivo atingido. Isso é esperado e termina o loop.
        }
    }
}