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

        buckets = new ArrayList<>(tamanho);
        for (int i = 0; i < tamanho; i++) {
            buckets.add(new Bucket(profundidadeInicial, capacidadePorBucket));
        }
    }

    // Função hash usando os p bits menos significativos
    private int hash(int chave) {
        return chave & ((1 << profundidadeGlobal) - 1);
    }

    // Busca o bucket com base na chave
    public Bucket buscarBucket(int chave) {
        int indice = hash(chave);
        return buckets.get(indice);
    }

    // Insere entrada e trata colisão via divisão de bucket
    public void inserir(int chave, long posicaoArquivo) {
        Entrada novaEntrada = new Entrada(chave, posicaoArquivo);
        int indice = hash(chave);
        Bucket bucket = buckets.get(indice);

        if (!bucket.estaCheio()) {
            bucket.inserir(novaEntrada);
        } else {
            dividirBucket(indice);
            inserir(chave, posicaoArquivo); // reinserção após divisão
        }
    }

    // Divide o bucket estourado e redistribui as entradas
    private void dividirBucket(int indice) {
        Bucket bucketAntigo = buckets.get(indice);
        int profundidadeLocalAntiga = bucketAntigo.getProfundidadeLocal();
        int novaProfundidade = profundidadeLocalAntiga + 1;

        // Se a profundidade local ultrapassar a global, duplica o diretório
        if (novaProfundidade > profundidadeGlobal) {
            duplicarDiretorio();
        }

        // Novo bucket com profundidade local aumentada
        Bucket novoBucket = new Bucket(novaProfundidade, capacidadePorBucket);
        bucketAntigo.setProfundidadeLocal(novaProfundidade);

        // Atualiza os ponteiros do diretório para bucket novo ou antigo
        int mask = (1 << novaProfundidade) - 1;
        for (int i = 0; i < buckets.size(); i++) {
            if ((i & ((1 << profundidadeLocalAntiga) - 1)) == (indice & ((1 << profundidadeLocalAntiga) - 1))) {
                if (((i >> profundidadeLocalAntiga) & 1) == 1) {
                    buckets.set(i, novoBucket);
                } else {
                    buckets.set(i, bucketAntigo);
                }
            }
        }

        // Reinsere os dados do bucket antigo (agora vazio)
        List<Entrada> entradasAntigas = new ArrayList<>(bucketAntigo.getRegistros());
        bucketAntigo.getRegistros().clear();

        for (Entrada entrada : entradasAntigas) {
            inserir(entrada.getChave(), entrada.getPosicaoArquivo());
        }
    }

    // Duplica o diretório (2^p → 2^(p+1))
    private void duplicarDiretorio() {
        int tamanhoAtual = buckets.size();
        for (int i = 0; i < tamanhoAtual; i++) {
            buckets.add(buckets.get(i)); // duplica os ponteiros (não cria buckets novos ainda!)
        }
        profundidadeGlobal++;
    }

    // Utilitário para debug
    public void imprimirEstado() {
        System.out.println("Profundidade Global: " + profundidadeGlobal);
        for (int i = 0; i < buckets.size(); i++) {
            Bucket b = buckets.get(i);
            System.out.println("[" + i + "] → Profundidade: " + b.getProfundidadeLocal()
                    + " | Registros: " + b.getRegistros().size());
        }
    }
}
