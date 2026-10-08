package com.rotasegura.model;

import com.rotasegura.exception.VeiculoInvalidoException;

/**
 * =====================================================================
 * SUBCLASSE: Popular  (extends Veiculo)
 * =====================================================================
 * Especializacao de Veiculo para a categoria de carros populares
 * (ex.: Fiat Argo, VW Gol, Chevrolet Onix).
 *
 * =====================================================================
 * HERANCA na pratica
 * =====================================================================
 * A palavra-chave "extends Veiculo" significa:
 *
 *   1) Popular E UM Veiculo (relacao "e-um" / is-a).
 *      Pode ser guardado em variavel do tipo Veiculo:
 *          Veiculo v = new Popular(...);
 *
 *   2) HERDA automaticamente da classe-base:
 *      - atributos encapsulados (placa, marca, modelo, ano, status, km)
 *      - getters, setters, isDisponivel(), calcularValorLocacao(),
 *        equals(), hashCode(), toString()
 *
 *   3) OBRIGA-SE a implementar os metodos abstract de Veiculo:
 *      calcularDiaria(), calcularSeguro(), calcularManutencao(),
 *      getCategoria() — cada um com @Override.
 *
 *   4) O construtor chama super(...) para inicializar a parte
 *      "Veiculo" do objeto (placa, marca, etc.). Sem super(...),
 *      o compilador reclama.
 *
 * Valores de referencia desta categoria:
 *   Diaria base ........ R$ 89,90
 *   Seguro diario ...... R$ 15,00
 *   Taxa de manutencao . R$ 25,00 + 0,01 por km
 */
public class Popular extends Veiculo {

    private static final long serialVersionUID = 1L;

    // Constantes PROPRIAS desta subclasse (nao existem em Veiculo).
    // Cada categoria define sua propria tabela de precos.
    private static final double DIARIA_BASE = 89.90;
    private static final double SEGURO_DIARIO = 15.00;
    private static final double TAXA_MANUTENCAO = 25.00;

    /**
     * Construtor da subclasse.
     * HERANCA: super(...) chama o construtor protected de Veiculo,
     * que valida e grava placa/marca/modelo/ano/km e define status.
     * Popular nao precisa repetir essa logica — ela e herdada.
     */
    public Popular(String placa, String marca, String modelo, int ano, double quilometragem)
            throws VeiculoInvalidoException {
        super(placa, marca, modelo, ano, quilometragem);
    }

    /**
     * Diaria de Popular: valor base, com pequeno acrescimo se o veiculo
     * tiver ate 3 anos de idade (mais novo = um pouco mais caro).
     */
    @Override
    public double calcularDiaria() {
        int idade = java.time.Year.now().getValue() - getAno();
        if (idade <= 3) {
            return DIARIA_BASE * 1.05; // +5% para modelos recentes
        }
        return DIARIA_BASE;
    }

    /** Seguro diario fixo e acessivel para a categoria popular. */
    @Override
    public double calcularSeguro() {
        return SEGURO_DIARIO;
    }

    /**
     * Manutencao: taxa fixa por contrato + pequena parcela proporcional
     * a quilometragem atual do veiculo.
     */
    @Override
    public double calcularManutencao() {
        return TAXA_MANUTENCAO + (getQuilometragem() * 0.01);
    }

    @Override
    public String getCategoria() {
        return "Popular";
    }
}
