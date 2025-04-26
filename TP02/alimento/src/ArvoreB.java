package TP02.alimento.src;

import java.io.*;

public class ArvoreB {
    private NoB raiz;
    private int ordem;
    private final String ARQUIVO_INDICE = "arvoreB.bin";

    public ArvoreB(int ordem) {
        this.ordem = ordem;
        this.raiz = new NoB(ordem, true);
        carregar();
    }

    public void inserir(int chave, long posicao) {
        NoB r = raiz;

        if (r.getNumChaves() == 2 * ordem - 1) {
            NoB s = new NoB(ordem, false);
            s.setFilho(0, r);
            dividirFilho(s, 0, r);
            inserirNaoCheio(s, chave, posicao);
            raiz = s;
        } else {
            inserirNaoCheio(r, chave, posicao);
        }

        salvar();
    }

    private void inserirNaoCheio(NoB no, int chave, long posicao) {
        int i = no.getNumChaves() - 1;

        if (no.isFolha()) {
            while (i >= 0 && chave < no.getChaves()[i]) {
                no.setChave(i + 1, no.getChaves()[i]);
                no.setPosicao(i + 1, no.getPosicoes()[i]);
                i--;
            }
            no.setChave(i + 1, chave);
            no.setPosicao(i + 1, posicao);
            no.setNumChaves(no.getNumChaves() + 1);
        } else {
            while (i >= 0 && chave < no.getChaves()[i]) {
                i--;
            }
            i++;
            if (no.getFilhos()[i].getNumChaves() == 2 * ordem - 1) {
                dividirFilho(no, i, no.getFilhos()[i]);
                if (chave > no.getChaves()[i]) {
                    i++;
                }
            }
            inserirNaoCheio(no.getFilhos()[i], chave, posicao);
        }
    }

    private void dividirFilho(NoB pai, int i, NoB y) {
        NoB z = new NoB(ordem, y.isFolha());
        z.setNumChaves(ordem - 1);

        for (int j = 0; j < ordem - 1; j++) {
            z.setChave(j, y.getChaves()[j + ordem]);
            z.setPosicao(j, y.getPosicoes()[j + ordem]);
        }

        if (!y.isFolha()) {
            for (int j = 0; j < ordem; j++) {
                z.setFilho(j, y.getFilhos()[j + ordem]);
            }
        }

        y.setNumChaves(ordem - 1);

        for (int j = pai.getNumChaves(); j >= i + 1; j--) {
            pai.setFilho(j + 1, pai.getFilhos()[j]);
        }

        pai.setFilho(i + 1, z);

        for (int j = pai.getNumChaves() - 1; j >= i; j--) {
            pai.setChave(j + 1, pai.getChaves()[j]);
            pai.setPosicao(j + 1, pai.getPosicoes()[j]);
        }

        pai.setChave(i, y.getChaves()[ordem - 1]);
        pai.setPosicao(i, y.getPosicoes()[ordem - 1]);
        pai.setNumChaves(pai.getNumChaves() + 1);
    }

    public Long buscar(int chave) {
        return buscarInterno(raiz, chave);
    }

    private Long buscarInterno(NoB no, int chave) {
        int i = 0;
        while (i < no.getNumChaves() && chave > no.getChaves()[i]) {
            i++;
        }

        if (i < no.getNumChaves() && chave == no.getChaves()[i]) {
            return no.getPosicoes()[i];
        }

        if (no.isFolha()) {
            return null;
        }

        return buscarInterno(no.getFilhos()[i], chave);
    }

    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_INDICE))) {
            oos.writeObject(raiz);
        } catch (IOException e) {
            System.out.println("Erro ao salvar árvore B: " + e.getMessage());
        }
    }

    private void carregar() {
        File file = new File(ARQUIVO_INDICE);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            raiz = (NoB) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar árvore B: " + e.getMessage());
        }
    }

    public void remover(int chave) {
        removerInterno(raiz, chave);
    
        // Atualiza raiz se ela ficou vazia
        if (raiz.getNumChaves() == 0) {
            if (raiz.isFolha()) {
                raiz = new NoB(ordem, true); // Árvore vazia
            } else {
                raiz = raiz.getFilhos()[0]; // Nova raiz
            }
        }
    
        salvar();
    }
    
    private void removerInterno(NoB no, int chave) {
        int idx = encontrarIndice(no, chave);
    
        // Caso 1: chave está neste nó
        if (idx < no.getNumChaves() && no.getChaves()[idx] == chave) {
            if (no.isFolha()) {
                // Caso 1a: nó folha, remover direto
                for (int i = idx; i < no.getNumChaves() - 1; i++) {
                    no.setChave(i, no.getChaves()[i + 1]);
                    no.setPosicao(i, no.getPosicoes()[i + 1]);
                }
                no.setNumChaves(no.getNumChaves() - 1);
            } else {
                // Caso 1b: nó interno
                NoB filhoEsq = no.getFilhos()[idx];
                NoB filhoDir = no.getFilhos()[idx + 1];
    
                if (filhoEsq.getNumChaves() >= ordem) {
                    // Antecessor
                    int pred = getMax(filhoEsq);
                    long posPred = getMaxPosicao(filhoEsq);
                    removerInterno(filhoEsq, pred);
                    no.setChave(idx, pred);
                    no.setPosicao(idx, posPred);
                } else if (filhoDir.getNumChaves() >= ordem) {
                    // Sucessor
                    int succ = getMin(filhoDir);
                    long posSucc = getMinPosicao(filhoDir);
                    removerInterno(filhoDir, succ);
                    no.setChave(idx, succ);
                    no.setPosicao(idx, posSucc);
                } else {
                    // Fusão e remoção recursiva
                    fundir(no, idx);
                    removerInterno(filhoEsq, chave);
                }
            }
        } else {
            // Caso 2: chave está em um filho
            if (no.isFolha()) return; // Não encontrado
    
            boolean ultimoFilho = (idx == no.getNumChaves());
    
            NoB filho = no.getFilhos()[idx];
    
            if (filho.getNumChaves() < ordem) {
                preencher(no, idx);
            }
    
            if (ultimoFilho && idx > no.getNumChaves()) {
                removerInterno(no.getFilhos()[idx - 1], chave);
            } else {
                removerInterno(no.getFilhos()[idx], chave);
            }
        }
    }
    
    private int encontrarIndice(NoB no, int chave) {
        int idx = 0;
        while (idx < no.getNumChaves() && no.getChaves()[idx] < chave)
            idx++;
        return idx;
    }
    
    private void preencher(NoB pai, int idx) {
        if (idx > 0 && pai.getFilhos()[idx - 1].getNumChaves() >= ordem) {
            pegarDoAnterior(pai, idx);
        } else if (idx < pai.getNumChaves() && pai.getFilhos()[idx + 1].getNumChaves() >= ordem) {
            pegarDoProximo(pai, idx);
        } else {
            if (idx < pai.getNumChaves()) {
                fundir(pai, idx);
            } else {
                fundir(pai, idx - 1);
            }
        }
    }
    
    private void pegarDoAnterior(NoB pai, int idx) {
        NoB filho = pai.getFilhos()[idx];
        NoB irmao = pai.getFilhos()[idx - 1];
    
        for (int i = filho.getNumChaves() - 1; i >= 0; i--) {
            filho.setChave(i + 1, filho.getChaves()[i]);
            filho.setPosicao(i + 1, filho.getPosicoes()[i]);
        }
    
        if (!filho.isFolha()) {
            for (int i = filho.getNumChaves(); i >= 0; i--) {
                filho.setFilho(i + 1, filho.getFilhos()[i]);
            }
        }
    
        filho.setChave(0, pai.getChaves()[idx - 1]);
        filho.setPosicao(0, pai.getPosicoes()[idx - 1]);
    
        if (!filho.isFolha()) {
            filho.setFilho(0, irmao.getFilhos()[irmao.getNumChaves()]);
        }
    
        pai.setChave(idx - 1, irmao.getChaves()[irmao.getNumChaves() - 1]);
        pai.setPosicao(idx - 1, irmao.getPosicoes()[irmao.getNumChaves() - 1]);
    
        filho.setNumChaves(filho.getNumChaves() + 1);
        irmao.setNumChaves(irmao.getNumChaves() - 1);
    }
    
    private void pegarDoProximo(NoB pai, int idx) {
        NoB filho = pai.getFilhos()[idx];
        NoB irmao = pai.getFilhos()[idx + 1];
    
        filho.setChave(filho.getNumChaves(), pai.getChaves()[idx]);
        filho.setPosicao(filho.getNumChaves(), pai.getPosicoes()[idx]);
    
        if (!filho.isFolha()) {
            filho.setFilho(filho.getNumChaves() + 1, irmao.getFilhos()[0]);
        }
    
        pai.setChave(idx, irmao.getChaves()[0]);
        pai.setPosicao(idx, irmao.getPosicoes()[0]);
    
        for (int i = 1; i < irmao.getNumChaves(); i++) {
            irmao.setChave(i - 1, irmao.getChaves()[i]);
            irmao.setPosicao(i - 1, irmao.getPosicoes()[i]);
        }
    
        if (!irmao.isFolha()) {
            for (int i = 1; i <= irmao.getNumChaves(); i++) {
                irmao.setFilho(i - 1, irmao.getFilhos()[i]);
            }
        }
    
        filho.setNumChaves(filho.getNumChaves() + 1);
        irmao.setNumChaves(irmao.getNumChaves() - 1);
    }
    
    private void fundir(NoB pai, int idx) {
        NoB filho = pai.getFilhos()[idx];
        NoB irmao = pai.getFilhos()[idx + 1];
    
        filho.setChave(ordem - 1, pai.getChaves()[idx]);
        filho.setPosicao(ordem - 1, pai.getPosicoes()[idx]);
    
        for (int i = 0; i < irmao.getNumChaves(); i++) {
            filho.setChave(i + ordem, irmao.getChaves()[i]);
            filho.setPosicao(i + ordem, irmao.getPosicoes()[i]);
        }
    
        if (!filho.isFolha()) {
            for (int i = 0; i <= irmao.getNumChaves(); i++) {
                filho.setFilho(i + ordem, irmao.getFilhos()[i]);
            }
        }
    
        for (int i = idx + 1; i < pai.getNumChaves(); i++) {
            pai.setChave(i - 1, pai.getChaves()[i]);
            pai.setPosicao(i - 1, pai.getPosicoes()[i]);
            pai.setFilho(i, pai.getFilhos()[i + 1]);
        }
    
        filho.setNumChaves(filho.getNumChaves() + irmao.getNumChaves() + 1);
        pai.setNumChaves(pai.getNumChaves() - 1);
    }
    
    private int getMax(NoB no) {
        while (!no.isFolha()) {
            no = no.getFilhos()[no.getNumChaves()];
        }
        return no.getChaves()[no.getNumChaves() - 1];
    }
    
    private long getMaxPosicao(NoB no) {
        while (!no.isFolha()) {
            no = no.getFilhos()[no.getNumChaves()];
        }
        return no.getPosicoes()[no.getNumChaves() - 1];
    }
    
    private int getMin(NoB no) {
        while (!no.isFolha()) {
            no = no.getFilhos()[0];
        }
        return no.getChaves()[0];
    }
    
    private long getMinPosicao(NoB no) {
        while (!no.isFolha()) {
            no = no.getFilhos()[0];
        }
        return no.getPosicoes()[0];
    }
    

public void atualizar(int chaveAntiga, int novaChave, long novaPosicao) {
    remover(chaveAntiga);
    inserir(novaChave, novaPosicao);
}


public void limpar() {
    this.raiz = null;
}

}
