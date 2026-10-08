package com.rotasegura.exception;

/**
 * =====================================================================
 * EXCECAO PERSONALIZADA: DataInvalidaException
 * =====================================================================
 * Representa erros relacionados a datas inconsistentes no sistema de
 * locacao (ex.: data de devolucao anterior a de inicio, data nula, etc.).
 *
 * Por que criar uma excecao propria?
 * - Deixa o codigo mais legivel (o nome ja diz o que deu errado).
 * - Permite capturar e tratar esse tipo de erro de forma especifica
 *   no menu principal, sem misturar com outros problemas.
 *
 * Herda de Exception (checked exception): o compilador obriga quem
 * chama os metodos a tratar ou declarar "throws".
 */
public class DataInvalidaException extends Exception {

    /**
     * Construtor que recebe apenas a mensagem de erro amigavel.
     * @param mensagem texto explicando o problema (exibido ao usuario)
     */
    public DataInvalidaException(String mensagem) {
        super(mensagem); // repassa a mensagem para a superclasse Exception
    }

    /**
     * Construtor que tambem guarda a causa original (util para debug).
     * @param mensagem texto amigavel
     * @param causa    excecao original que provocou este erro
     */
    public DataInvalidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
