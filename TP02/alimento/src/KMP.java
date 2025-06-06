package TP02.alimento.src;

public class KMP {

    /**
     * Busca a primeira ocorrência de um padrão em um texto usando o algoritmo KMP.
     * @param texto O texto onde a busca será realizada.
     * @param padrao O padrão a ser encontrado.
     * @return O índice inicial da primeira ocorrência do padrão no texto, ou -1 se não for encontrado.
     */
    public static int search(String texto, String padrao) {
        int n = texto.length();
        int m = padrao.length();

        if (m == 0) return 0;
        if (n == 0) return -1;

        int[] lps = computeLPSArray(padrao);
        int i = 0; // índice para o texto
        int j = 0; // índice para o padrão

        while (i < n) {
            if (padrao.charAt(j) == texto.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                return i - j; // Padrão encontrado
            } else if (i < n && padrao.charAt(j) != texto.charAt(i)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return -1; // Padrão não encontrado
    }

    /**
     * Calcula o array de "Longest Proper Prefix which is also a Suffix" (LPS).
     * Este array é usado para pular caracteres de forma eficiente durante a busca.
     * @param padrao O padrão para o qual o array LPS será gerado.
     * @return O array LPS.
     */
    private static int[] computeLPSArray(String padrao) {
        int m = padrao.length();
        int[] lps = new int[m];
        int length = 0;
        int i = 1;
        lps[0] = 0;

        while (i < m) {
            if (padrao.charAt(i) == padrao.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = length;
                    i++;
                }
            }
        }
        return lps;
    }
}