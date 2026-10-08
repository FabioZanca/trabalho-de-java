package com.rotasegura.model;

import com.rotasegura.exception.VeiculoInvalidoException;

/**
 * =====================================================================
 * SUBCLASSE: SUV  (extends Veiculo)
 * =====================================================================
 * Especializacao de Veiculo para utilitarios esportivos
 * (ex.: Jeep Compass, Toyota SW4, Hyundai Creta).
 *
 * HERANCA: terceira especializacao obrigatoria do TP.
 * Segue o mesmo padrao (extends Veiculo + super + @Override),
 * com precos mais altos por causa do porte e valor de mercado.
 *
 * Valores de referencia:
 *   Diaria base ........ R$ 249,90
 *   Seguro diario ...... R$ 49,00
 *   Taxa de manutencao . R$ 75,00 + 0,02 por km
 */
public class SUV extends Veiculo {

    private static final long serialVersionUID = 1L;

    private static final double DIARIA_BASE = 249.90;
    private static final double SEGURO_DIARIO = 49.00;
    private static final double TAXA_MANUTENCAO = 75.00;

    public SUV(String placa, String marca, String modelo, int ano, double quilometragem)
            throws VeiculoInvalidoException {
        super(placa, marca, modelo, ano, quilometragem);
    }

    /**
     * Diaria de SUV:
     * - +15% para modelos com ate 2 anos
     * - -15% para modelos com 7 anos ou mais
     */
    @Override
    public double calcularDiaria() {
        int idade = java.time.Year.now().getValue() - getAno();
        if (idade <= 2) {
            return DIARIA_BASE * 1.15;
        } else if (idade >= 7) {
            return DIARIA_BASE * 0.85;
        }
        return DIARIA_BASE;
    }

    @Override
    public double calcularSeguro() {
        // Seguro mais alto devido ao valor elevado do bem
        return SEGURO_DIARIO;
    }

    @Override
    public double calcularManutencao() {
        return TAXA_MANUTENCAO + (getQuilometragem() * 0.02);
    }

    @Override
    public String getCategoria() {
        return "SUV";
    }
}
