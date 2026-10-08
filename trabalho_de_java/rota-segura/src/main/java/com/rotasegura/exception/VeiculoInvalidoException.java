package com.rotasegura.exception;

/**
 * =====================================================================
 * EXCECAO PERSONALIZADA: VeiculoInvalidoException
 * =====================================================================
 * Lancada quando os dados tecnicos de um veiculo sao invalidos, por exemplo:
 *   - Placa vazia, muito curta ou com formato incorreto
 *   - Ano fora do intervalo permitido
 *   - Quilometragem negativa
 *   - Tentativa de cadastrar placa ja existente na frota
 *
 * Usada nos setters da classe abstrata Veiculo e no servico de cadastro.
 */
public class VeiculoInvalidoException extends Exception {

    /**
     * @param mensagem descricao do dado invalido
     */
    public VeiculoInvalidoException(String mensagem) {
        super(mensagem);
    }
}
