package TP03.compressao.huffman;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

    public HuffmanNode raiz;

    /**
     * Constrói a árvore de Huffman a partir de um mapa de frequências de bytes.
     * 
     * @param mapaFrequencias Mapa contendo cada byte e sua respectiva frequência.
     */
    public HuffmanTree(Map<Byte, Integer> mapaFrequencias) {
        if (mapaFrequencias == null || mapaFrequencias.isEmpty()) {
            this.raiz = null;
            return;
        }

        // 1. Cria uma fila de prioridade para armazenar os nós da árvore.
        PriorityQueue<HuffmanNode> filaPrioridade = new PriorityQueue<>();

        // 2. Cria um nó folha para cada símbolo com frequência > 0 e o adiciona à fila.
        for (Map.Entry<Byte, Integer> entrada : mapaFrequencias.entrySet()) {
            if (entrada.getValue() > 0) {
                filaPrioridade.add(new HuffmanNode(entrada.getKey(), entrada.getValue()));
            }
        }

        // 3. Constrói a árvore:
        // Enquanto houver mais de um nó na fila, combina os dois nós de menor
        // frequência
        // em um novo nó pai, que é reinserido na fila.
        // Trata o caso de arquivo com apenas um tipo de símbolo (filaPrioridade.size()
        // == 1).
        while (filaPrioridade.size() > 1) {
            HuffmanNode filhoEsquerda = filaPrioridade.poll();
            HuffmanNode filhoDireita = filaPrioridade.poll();

            HuffmanNode pai = new HuffmanNode(filhoEsquerda, filhoDireita);
            filaPrioridade.add(pai);
        }

        // 4. O nó restante na fila é a raiz da árvore de Huffman.
        // Se a fila estiver vazia (mapaFrequencias original vazio ou sem frequências >
        // 0), raiz será null.
        if (!filaPrioridade.isEmpty()) {
            this.raiz = filaPrioridade.poll();
        } else {
            this.raiz = null; // Garante que a raiz seja nula se não houver nós.
        }
    }

    /**
     * Gera o mapa de códigos de Huffman (Byte -> String de bits) para os símbolos
     * da árvore.
     * 
     * @return Um mapa onde a chave é o byte e o valor é seu código Huffman em
     *         formato de String.
     */
    public Map<Byte, String> gerarMapaDeCodigos() {
        Map<Byte, String> mapaCodigos = new HashMap<>();
        if (this.raiz != null) {
            gerarCodigosRecursivo(this.raiz, "", mapaCodigos);
        }
        return mapaCodigos;
    }

    /**
     * Método auxiliar recursivo para atravessar a árvore e gerar os códigos.
     * 
     * @param no          Nó atual na travessia.
     * @param codigoAtual Código binário acumulado até o nó atual.
     * @param mapaCodigos Mapa a ser preenchido com os códigos dos símbolos.
     */
    private void gerarCodigosRecursivo(HuffmanNode no, String codigoAtual, Map<Byte, String> mapaCodigos) {
        if (no == null) {
            return;
        }

        // Se é um nó folha, armazena o símbolo e seu código correspondente.
        if (no.isFolha()) {
            // Convenção para árvore com apenas um símbolo: código "0".
            // Se o nó raiz for uma folha, o codigoAtual estará vazio.
            if (codigoAtual.isEmpty() && no.simbolo != null) {
                mapaCodigos.put(no.simbolo, "0");
            } else if (no.simbolo != null) { // Para nós folha em árvores maiores.
                mapaCodigos.put(no.simbolo, codigoAtual);
            }
            return;
        }

        // Continua a travessia: '0' para esquerda, '1' para direita.
        gerarCodigosRecursivo(no.esquerda, codigoAtual + "0", mapaCodigos);
        gerarCodigosRecursivo(no.direita, codigoAtual + "1", mapaCodigos);
    }
}