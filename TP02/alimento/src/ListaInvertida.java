package TP02.alimento.src;

import java.util.*;

public class ListaInvertida {
    private Map<String, EntradaLista> dicionario;

    public ListaInvertida() {
        this.dicionario = new HashMap<>();
    }

    // Adicionar termos de um alimento
    public void adicionarAlimento(String alimento, int id) {
        if (alimento == null || alimento.isEmpty())
            return;

        String[] termos = alimento.toLowerCase().split("\\s+");

        for (String termo : termos) {
            if (termo.isEmpty())
                continue;

            EntradaLista entrada = dicionario.get(termo);
            if (entrada == null) {
                entrada = new EntradaLista(termo);
                dicionario.put(termo, entrada);
            }
            entrada.adicionarReferencia(id);
        }
    }

    // Remover termos de um alimento
    public void removerAlimento(String alimento, int id) {
        if (alimento == null || alimento.isEmpty())
            return;

        String[] termos = alimento.toLowerCase().split("\\s+");

        for (String termo : termos) {
            EntradaLista entrada = dicionario.get(termo);
            if (entrada != null) {
                entrada.removerReferencia(id);
                if (entrada.listaVazia()) {
                    dicionario.remove(termo);
                }
            }
        }
    }

    // Buscar um termo
    public List<Integer> buscar(String termo) {
        EntradaLista entrada = dicionario.get(termo.toLowerCase().trim());
        if (entrada != null) {
            return entrada.getReferencias();
        } else {
            return new ArrayList<>();
        }
    }

    // Buscar interseção de múltiplos termos
    public List<Integer> buscarIntersecao(String[] termos) {
        List<Integer> resultado = null;

        for (String termo : termos) {
            List<Integer> ids = buscar(termo);
            if (resultado == null) {
                resultado = new ArrayList<>(ids);
            } else {
                resultado.retainAll(ids);
            }
        }

        return resultado == null ? new ArrayList<>() : resultado;
    }

    // Impressão para debug
    public void imprimir() {
        for (EntradaLista entrada : dicionario.values()) {
            System.out.println("Termo: " + entrada.getTermo() + " → IDs: " + entrada.getReferencias());
        }
    }

    public Map<String, EntradaLista> getDicionario() {
        return dicionario;
    }

}
