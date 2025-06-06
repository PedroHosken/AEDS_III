package TP02.alimento.src;

public class BoyerMoore {
    private static final int NO_OF_CHARS = 256; // Tamanho do alfabeto (ASCII estendido)

    /**
     * Pré-processa o padrão para criar a tabela de "caracteres ruins".
     * Esta tabela ajuda a determinar o maior salto possível em caso de um caractere não correspondente.
     * @param str O padrão.
     * @param size O tamanho do padrão.
     * @param badchar A tabela a ser preenchida (array de inteiros).
     */
    private static void badCharHeuristic(char[] str, int size, int badchar[]) {
        for (int i = 0; i < NO_OF_CHARS; i++) {
            badchar[i] = -1;
        }
        for (int i = 0; i < size; i++) {
            badchar[(int) str[i]] = i;
        }
    }

    /**
     * Busca a primeira ocorrência de um padrão em um texto usando o algoritmo Boyer-Moore.
     * @param texto O texto onde a busca será realizada.
     * @param padrao O padrão a ser encontrado.
     * @return O índice inicial da primeira ocorrência do padrão no texto, ou -1 se não for encontrado.
     */
    public static int search(String texto, String padrao) {
        char[] txt = texto.toCharArray();
        char[] pat = padrao.toCharArray();
        int m = pat.length;
        int n = txt.length;

        if (m == 0) return 0;
        if (n == 0) return -1;

        int[] badchar = new int[NO_OF_CHARS];
        badCharHeuristic(pat, m, badchar);

        int s = 0; // s é o deslocamento do padrão em relação ao texto
        while (s <= (n - m)) {
            int j = m - 1;

            // Continua diminuindo o índice j do padrão enquanto os caracteres do padrão e do texto correspondem
            while (j >= 0 && pat[j] == txt[s + j]) {
                j--;
            }

            // Se o padrão foi encontrado
            if (j < 0) {
                return s; // Retorna a posição do início da correspondência
            } else {
                // Desloca o padrão para que o "caractere ruim" no texto se alinhe com sua última ocorrência no padrão
                int badCharShift = j - badchar[txt[s + j]];
                s += Math.max(1, badCharShift);
            }
        }
        return -1; // Padrão não encontrado
    }
}