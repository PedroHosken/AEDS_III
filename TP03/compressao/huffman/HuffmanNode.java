package TP03.compressao.huffman; 

public class HuffmanNode implements Comparable<HuffmanNode> {

    public Byte simbolo;      // Byte (pode ser null para nós internos)
    public int frequencia;    // Frequência do símbolo ou soma das frequências dos filhos
    public HuffmanNode esquerda; // Filho esquerdo
    public HuffmanNode direita;  // Filho direito

    // Construtor para nós folha (que contêm um símbolo)
    public HuffmanNode(Byte simbolo, int frequencia) {
        this.simbolo = simbolo;
        this.frequencia = frequencia;
        this.esquerda = null;
        this.direita = null;
    }

    // Construtor para nós internos (que juntam dois nós)
    public HuffmanNode(HuffmanNode esquerda, HuffmanNode direita) {
        this.simbolo = null; // Nós internos não representam um símbolo específico
        this.esquerda = esquerda;
        this.direita = direita;
        // A frequência de um nó interno é a soma das frequências dos seus filhos
        this.frequencia = esquerda.frequencia + direita.frequencia;
    }

    // Método para verificar se o nó é uma folha
    public boolean isFolha() {
        return (this.esquerda == null) && (this.direita == null);
    }

    // Implementação do método compareTo para a PriorityQueue
    // Compara os nós com base em suas frequências
    @Override
    public int compareTo(HuffmanNode outroNo) {
        return this.frequencia - outroNo.frequencia; // Ordena do menor para o maior (Min-Heap)
    }

    // Opcional: um método toString para facilitar a depuração
    @Override
    public String toString() {
        String s = "Freq: " + frequencia;
        if (simbolo != null) {
            if (simbolo >= 32 && simbolo <= 126) { // Caractere imprimível
                 s += ", Char: " + (char)simbolo.byteValue();
            } else {
                 s += ", Byte: " + simbolo;
            }
        }
        return s;
    }
}