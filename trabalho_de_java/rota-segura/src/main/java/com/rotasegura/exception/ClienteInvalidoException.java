package com.rotasegura.exception;

/**
 * =====================================================================
 * EXCECAO PERSONALIZADA: ClienteInvalidoException
 * =====================================================================
 * Lancada durante a validacao dos dados cadastrais do cliente, por exemplo:
 *   - CPF com quantidade incorreta de digitos
 *   - Nome vazio ou muito curto
 *   - Idade menor que 18 anos
 *   - E-mail sem o caractere '@'
 *   - Tentativa de cadastrar CPF ja existente
 *
 * Faz parte do ENCAPSULAMENTO: as regras de validacao ficam dentro
 * da classe Cliente (setters), e esta excecao comunica a falha.
 */
public class ClienteInvalidoException extends Exception {

    /**
     * @param mensagem texto explicando qual regra de validacao foi violada
     */
    public ClienteInvalidoException(String mensagem) {
        super(mensagem);
    }
}
