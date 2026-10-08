package com.rotasegura.model;

/**
 * =====================================================================
 * ENUM: StatusVeiculo
 * =====================================================================
 * Representa os possiveis estados operacionais de um veiculo na frota.
 *
 * Enum e um tipo especial do Java que define um conjunto FIXO de constantes.
 * Vantagens:
 *   - Evita strings magicas ("disponivel", "ocupado"...) espalhadas no codigo
 *   - O compilador impede valores invalidos
 *   - Cada constante pode carregar dados extras (aqui: descricao legivel)
 *
 * Estados usados no sistema:
 *   DISPONIVEL  -> pode ser alugado
 *   OCUPADO     -> esta em uma locacao ativa
 *   MANUTENCAO  -> temporariamente fora de operacao
 */
public enum StatusVeiculo {

    /** Veiculo livre para nova locacao. */
    DISPONIVEL("Disponivel"),

    /** Veiculo vinculado a um contrato ativo. */
    OCUPADO("Ocupado"),

    /** Veiculo em manutencao preventiva ou corretiva. */
    MANUTENCAO("Em Manutencao");

    /** Texto amigavel exibido em telas e relatorios. */
    private final String descricao;

    /**
     * Construtor do enum (sempre private implicitamente).
     * @param descricao rotulo em portugues para o status
     */
    StatusVeiculo(String descricao) {
        this.descricao = descricao;
    }

    /** Retorna a descricao legivel do status. */
    public String getDescricao() {
        return descricao;
    }

    /**
     * toString() sobrescrito para que println(status) mostre a descricao
     * em vez do nome da constante (DISPONIVEL -> "Disponivel").
     */
    @Override
    public String toString() {
        return descricao;
    }
}
