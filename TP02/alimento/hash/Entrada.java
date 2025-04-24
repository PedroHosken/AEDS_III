package TP02.alimento.hash;

public class Entrada {
    private int chave;
    private long posicaoArquivo;

    public Entrada(int chave, long posicaoArquivo) {
        this.chave = chave;
        this.posicaoArquivo = posicaoArquivo;
    }

    public int getChave() {
        return chave;
    }

    public void setChave(int chave) {
        this.chave = chave;
    }

    public long getPosicaoArquivo() {
        return posicaoArquivo;
    }

    public void setPosicaoArquivo(long posicaoArquivo) {
        this.posicaoArquivo = posicaoArquivo;
    }
}
