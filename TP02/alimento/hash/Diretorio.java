package TP02.alimento.hash;

import java.util.ArrayList;
import java.util.List;

public class Diretorio {

    private int profundidadeGlobal;
    private List<Bucket> buckets;
    private final int capacidadePorBucket;

    public Diretorio(int profundidadeInicial, int capacidadePorBucket) {
        this.profundidadeGlobal = profundidadeInicial;
        this.capacidadePorBucket = capacidadePorBucket;
        int tamanho = (int) Math.pow(2, profundidadeGlobal);

        buckets = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            buckets.add(new Bucket(profundidadeInicial, capacidadePorBucket));
        }
    }

    private int hash(int chave) {
        int mascara = (1 << profundidadeGlobal) - 1;
        return chave & mascara; // pega os p bits menos significativos
    }

    public void inserir(int chave, long posicaoArquivo) {
        Entrada novaEntrada = new Entrada(chave, posicaoArquivo);
        int indice = hash(chave);
        Bucket bucket = buckets.get(indice);

        if (!bucket.estaCheio()) {
            bucket.inserir(novaEntrada);
        } else {
            dividirBucket(indice);
            inserir(chave, posicaoArquivo); // tenta novamente
        }
    }

    private void dividirBucket(int indice) {
        Bucket bucketAntigo = buckets.get(indice);
        int novaProfundidade = bucketAntigo.getProfundidadeLocal() + 1;

        if (novaProfundidade > profundidadeGlobal) {
            duplicarDiretorio();
        }

        Bucket novoBucket = new Bucket(novaProfundidade, capacidadePorBucket);
        bucketAntigo.setProfundidadeLocal(novaProfundidade);

        List<Entrada> entradasAntigas = new ArrayList<>(bucketAntigo.getRegistros());
        bucketAntigo.getRegistros().clear();

        int mask = (1 << novaProfundidade) - 1;
        for (int i = 0; i < buckets.size(); i++) {
            if ((i & mask) == (indice & mask)) {
                if (((i >> (novaProfundidade - 1)) & 1) == 1) {
                    buckets.set(i, novoBucket);
                } else {
                    buckets.set(i, bucketAntigo);
                }
            }
        }

        for (Entrada entrada : entradasAntigas) {
            inserir(entrada.getChave(), entrada.getPosicaoArquivo());
        }
    }

    private void duplicarDiretorio() {
        List<Bucket> novaTabela = new ArrayList<>();
        for (Bucket b : buckets) {
            novaTabela.add(b);
            novaTabela.add(b);
        }
        buckets = novaTabela;
        profundidadeGlobal++;
    }

    public void imprimirEstado() {
        System.out.println("Profundidade Global: " + profundidadeGlobal);
        for (int i = 0; i < buckets.size(); i++) {
            Bucket b = buckets.get(i);
            System.out.println("[" + i + "] → Profundidade: " + b.getProfundidadeLocal() + " | Registros: " + b.getRegistros().size());
        }
    }
}
