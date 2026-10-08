package com.rotasegura.model;

import com.rotasegura.exception.VeiculoInvalidoException;

/**
 * =====================================================================
 * SUBCLASSE: Sedan  (extends Veiculo)
 * =====================================================================
 * Especializacao de Veiculo para sedans intermediarios
 * (ex.: Toyota Corolla, Honda Civic, VW Jetta).
 *
 * HERANCA: mesma ideia de Popular — herda atributos e metodos de
 * Veiculo e implementa os metodos abstract com precos intermediarios.
 * A estrutura (extends + super + @Override) e identica; muda apenas
 * a tabela de valores, refletindo conforto e valor de mercado maiores.
 *
 * Valores de referencia:
 *   Diaria base ........ R$ 149,90
 *   Seguro diario ...... R$ 28,00
 *   Taxa de manutencao . R$ 45,00 + 0,015 por km
 */
public class Sedan extends Veiculo {

    private static final long serialVersionUID = 1L;

    private static final double DIARIA_BASE = 149.90;
    private static final double SEGURO_DIARIO = 28.00;
    private static final double TAXA_MANUTENCAO = 45.00;

    public Sedan(String placa, String marca, String modelo, int ano, double quilometragem)
            throws VeiculoInvalidoException {
        super(placa, marca, modelo, ano, quilometragem);
    }

    /**
     * Diaria de Sedan:
     * - +10% se o veiculo tiver ate 2 anos
     * - -10% se tiver 8 anos ou mais (desconto por depreciação)
     * - valor base nos demais casos
     */
    @Override
    public double calcularDiaria() {
        int idade = java.time.Year.now().getValue() - getAno();
        if (idade <= 2) {
            return DIARIA_BASE * 1.10;
        } else if (idade >= 8) {
            return DIARIA_BASE * 0.90;
        }
        return DIARIA_BASE;
    }

    @Override
    public double calcularSeguro() {
        return SEGURO_DIARIO;
    }

    @Override
    public double calcularManutencao() {
        return TAXA_MANUTENCAO + (getQuilometragem() * 0.015);
    }

    @Override
    public String getCategoria() {
        return "Sedan";
    }
}
