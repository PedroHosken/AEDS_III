package TP03.compressao.huffman; // Ou o seu pacote correspondente

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class HuffmanTree {

    public HuffmanNode raiz; // Raiz da árvore de Huffman

    // Construtor que constrói a árvore a partir de um mapa de frequências de bytes
    public HuffmanTree(Map<Byte, Integer> mapaFrequencias) {
        if (mapaFrequencias == null || mapaFrequencias.isEmpty()) {
            // Tratar caso de mapa vazio ou nulo, talvez lançar uma exceção
            // ou criar uma árvore vazia/nula.
            this.raiz = null;
            return;
        }

        // 1. Criar uma fila de prioridade para os nós folha
        PriorityQueue<HuffmanNode> filaPrioridade = new PriorityQueue<>();

        // 2. Para cada símbolo no mapa de frequências, criar um nó folha e adicionar à
        // fila
        for (Map.Entry<Byte, Integer> entrada : mapaFrequencias.entrySet()) {
            if (entrada.getValue() > 0) { // Apenas símbolos com frequência > 0
                filaPrioridade.add(new HuffmanNode(entrada.getKey(), entrada.getValue()));
            }
        }

        // Caso especial: se houver apenas um tipo de símbolo no arquivo
        if (filaPrioridade.size() == 1) {
            HuffmanNode unicoNo = filaPrioridade.poll();
            // Criar um nó pai artificial para ter um caminho (código '0' por exemplo)
            // ou definir uma convenção para este caso.
            // Uma abordagem comum é criar um nó pai com o únicoNo como filho esquerdo (ou
            // direito)
            // e o outro filho nulo ou um nó placeholder se necessário para a lógica de
            // código.
            // Para simplificar, podemos apenas ter o nó único como raiz,
            // e a lógica de geração de código precisará lidar com isso (ex: código "0").
            // No entanto, para formar uma árvore binária propriamente dita para o
            // algoritmo,
            // mesmo com um único símbolo, é comum criar um nó pai.
            // Vamos seguir a lógica de combinar até sobrar um. Se só tem um, ele é a raiz.
            // A geração de código para uma árvore com apenas um nó (raiz=folha) é um caso
            // especial.
            this.raiz = unicoNo; // Se só há um nó, ele é a raiz.
                                 // A geração de códigos tratará o código como "0" ou "1".
        } else {
            // 3. Construir a árvore combinando os nós
            while (filaPrioridade.size() > 1) {
                HuffmanNode filhoEsquerda = filaPrioridade.poll();
                HuffmanNode filhoDireita = filaPrioridade.poll();

                HuffmanNode pai = new HuffmanNode(filhoEsquerda, filhoDireita);
                filaPrioridade.add(pai);
            }
            // 4. O último nó na fila é a raiz da árvore de Huffman
            this.raiz = filaPrioridade.poll();
        }
    }

    // Método para gerar o mapa de códigos de Huffman (Byte -> String de bits)
    public Map<Byte, String> gerarMapaDeCodigos() {
        Map<Byte, String> mapaCodigos = new HashMap<>();
        if (this.raiz != null) {
            gerarCodigosRecursivo(this.raiz, "", mapaCodigos);
        }
        return mapaCodigos;
    }

    // Método auxiliar recursivo para gerar os códigos
    private void gerarCodigosRecursivo(HuffmanNode no, String codigoAtual, Map<Byte, String> mapaCodigos) {
        if (no == null) {
            return;
        }

        // Se é um nó folha, encontramos um símbolo e seu código
        if (no.isFolha()) {
            // Caso especial: árvore com apenas um nó (raiz é folha)
            if (codigoAtual.isEmpty() && no.simbolo != null) {
                // Se a árvore só tem um símbolo, por convenção, damos um código simples como
                // "0"
                mapaCodigos.put(no.simbolo, "0");
            } else if (no.simbolo != null) { // Checagem extra para garantir que é um símbolo válido
                mapaCodigos.put(no.simbolo, codigoAtual);
            }
            return;
        }

        // Se não é folha, continua para os filhos
        // Adiciona '0' para a esquerda e '1' para a direita (convenção comum)
        gerarCodigosRecursivo(no.esquerda, codigoAtual + "0", mapaCodigos);
        gerarCodigosRecursivo(no.direita, codigoAtual + "1", mapaCodigos);
    }

    // Outros métodos podem ser adicionados aqui, como:
    // - Um método para serializar a árvore (ou as frequências) para o cabeçalho do
    // arquivo.
    // - Um construtor estático para reconstruir a árvore a partir dos dados do
    // cabeçalho.
}