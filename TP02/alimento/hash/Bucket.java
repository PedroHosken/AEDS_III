package TP02.alimento.hash;

import java.util.ArrayList;
import java.util.List;

public class Bucket {
    private int profundidadeLocal;
    private int capacidadeMaxima;
    private List<Entrada> registros;

    public Bucket(int profundidadeLocal, int capacidadeMaxima) {
        this.profundidadeLocal = profundidadeLocal;
        this.capacidadeMaxima = capacidadeMaxima;
        this.registros = new ArrayList<>();
    }

    public boolean inserir(Entrada entrada) {
        if (!estaCheio()) {
            registros.add(entrada);
            return true;
        }
        return false;
    }

    public boolean estaCheio() {
        return registros.size() >= capacidadeMaxima;
    }

    public List<Entrada> getRegistros() {
        return registros;
    }

    public int getProfundidadeLocal() {
        return profundidadeLocal;
    }

    public void setProfundidadeLocal(int profundidadeLocal) {
        this.profundidadeLocal = profundidadeLocal;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(int capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }
}
