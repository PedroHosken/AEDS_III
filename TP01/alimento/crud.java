package EXS.TP01.alimento;

/**
 * Classe CRUD - tela de apoio
 * Banco de dados: daily_food_nutrition_dataset - KAGGLE
 * Autores:
 * Gabriel Tamietti Mauro
 * Pedro Hosken Fernandes Guimarães - 816561
 */

// ---- Bibliotecas ---- //
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;

public class crud {
    /**
     * Metódo Main
     * Objetivo: Tela inicial com opções do CRUD
     * Descrição:
     * 
     * CRUD Sequencial:
     * O sistema deverá oferecer uma tela inicial (com uso pelo terminal) com um
     * menu com as seguintes opções:
     * 
     * Realizar a carga da base de dados selecionada, através da importação de
     * arquivo CSV, de rota de API ou outro formato que julgar pertinente, para um
     * arquivo binário.
     * 
     * Ler um registro (id) -> esse método deve receber um id como parâmetro,
     * percorrer o arquivo binário e retornar os dados do id informado.
     * 
     * Atualizar um registro -> esse método deve receber novas informações sobre um
     * objeto e atualizar os valores dele no arquivo binário. Observe duas
     * possibilidades que podem acontecer:
     * O registro mantém seu tamanho - Nenhum problema aqui. Basta atualizar os
     * dados no próprio local.
     * O registro aumenta ou diminui de tamanho - O registro anterior deve ser
     * apagado (por meio da marcação lápide) e o novo registro deve ser escrito no
     * fim do arquivo.
     * 
     * Deletar um registro (id) -> esse método deve receber um id como parâmetro,
     * percorrer o arquivo binário e colocar uma marcação (lápide) no registro que
     * será considerado deletado.
     * 
     */
    public void main(String args[]) {
        // definir dados
        String path = "C:/User/User1/Document/GitHu/AEDS_II/EX/TP0/aliment/daily_food_nutrition_dataset.csv"; // Minha
                                                                                                              // máquina:
                                                                                                              // Pedro
        Scanner sc = new Scanner(System.in);
        int opcao = 0;
        // Tela Inicial - CRUD
        System.out.println("Bem vindo a nossa Tela Inicial, escolha o metodo desejado:");
        System.out.println("1 - Carga inicial da base para arquivo binario");
        System.out.println("2 - Buscar Refeicao por ID");
        System.out.println("3 - Fazer o update de uma Refeicao");
        System.out.println("4 - Deletar uma Refeicao");
        System.out.println("5 - Sair da Tela Inicial");

        opcao = sc.nextInt(); // leitura da opção

        // switch case
        switch (opcao) {
            // primeiro caso
            case 1:
                // segundo caso
            case 2:
                // terceiro caso
            case 3:
                // quarto caso
            case 4:
                // quinto caso
            case 5:
        }

    }

}
