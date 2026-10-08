package com.rotasegura.exception;

/**
 * =====================================================================
 * EXCECAO PERSONALIZADA: VeiculoIndisponivelException
 * =====================================================================
 * Lancada quando o sistema tenta realizar uma operacao invalida sobre
 * um veiculo, por exemplo:
 *   - Alugar um veiculo que ja esta OCUPADO
 *   - Alugar um veiculo em MANUTENCAO
 *   - Referenciar uma placa que nao existe na frota
 *
 * Demonstra o requisito de RESILIENCIA: o programa nao "quebra";
 * a excecao e capturada no Main e uma mensagem clara e exibida.
 */
public class VeiculoIndisponivelException extends Exception {

    /**
     * @param mensagem descricao do problema (ex.: "Veiculo ABC1D23 nao esta disponivel")
     */
    public VeiculoIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
