package TP02.alimento.src;

import java.io.Serializable;

public class NoB implements Serializable {
    private static final long serialVersionUID = 1L;

    int ordem;             // Ordem da árvore B (grau mínimo t)
    int numChaves;         // Número atual de chaves
    int[] chaves;          // Array de chaves (ID do registro)
    long[] posicoes;       // Posições no arquivo binário associadas às chaves
    NoB[] filhos;          // Ponteiros para os filhos
    boolean folha;         // Indica se o nó é uma folha

    public NoB(int ordem, boolean folha) {
        this.ordem = ordem;
        this.folha = folha;
        this.chaves = new int[2 * ordem - 1];
        this.posicoes = new long[2 * ordem - 1];
        this.filhos = new NoB[2 * ordem];
        this.numChaves = 0;
    }

    public boolean isFolha() {
        return folha;
    }

    public int getNumChaves() {
        return numChaves;
    }

    public int[] getChaves() {
        return chaves;
    }

    public long[] getPosicoes() {
        return posicoes;
    }

    public NoB[] getFilhos() {
        return filhos;
    }

    public void setFolha(boolean folha) {
        this.folha = folha;
    }

    public void setNumChaves(int numChaves) {
        this.numChaves = numChaves;
    }

    public void setChave(int i, int chave) {
        this.chaves[i] = chave;
    }

    public void setPosicao(int i, long posicao) {
        this.posicoes[i] = posicao;
    }

    public void setFilho(int i, NoB filho) {
        this.filhos[i] = filho;
    }
}
