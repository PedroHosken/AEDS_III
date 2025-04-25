package TP02.alimento.listaInvertida;

import java.util.ArrayList;
import java.util.List;

public class EntradaLista {
    private String termo;
    private List<Integer> referencias;

    public EntradaLista(String termo) {
        this.termo = termo.toLowerCase().trim(); // normaliza
        this.referencias = new ArrayList<>();
    }

    public String getTermo() {
        return termo;
    }

    public List<Integer> getReferencias() {
        return referencias;
    }

    public void adicionarReferencia(int id) {
        if (!referencias.contains(id)) {
            referencias.add(id);
        }
    }

    public void removerReferencia(int id) {
        referencias.remove(Integer.valueOf(id));
    }

    public boolean listaVazia() {
        return referencias.isEmpty();
    }
}
