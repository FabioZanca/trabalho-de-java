package com.rotasegura.model;

import com.rotasegura.exception.VeiculoInvalidoException;
import java.io.Serializable;
import java.util.Objects;

/**
 * =====================================================================
 * CLASSE ABSTRATA: Veiculo
 * =====================================================================
 * Classe-base de toda a hierarquia de veiculos da frota.
 *
 * =====================================================================
 * HERANCA (conceito obrigatorio do TP)
 * =====================================================================
 * Heranca permite criar uma classe-base com o que e COMUM e classes
 * filhas (subclasses) que REUTILIZAM esse codigo e acrescentam ou
 * especializam o comportamento.
 *
 * Hierarquia deste projeto:
 *
 *                    Veiculo  (abstract)
 *                   /    |    \
 *              Popular  Sedan  SUV
 *
 * O que as subclasses HERDAM automaticamente:
 *   - atributos: placa, marca, modelo, ano, status, quilometragem
 *   - metodos concretos: getters, setters, isDisponivel(),
 *     calcularValorLocacao(), equals(), hashCode(), toString()
 *
 * O que as subclasses SAO OBRIGADAS a implementar (metodos abstract):
 *   - calcularDiaria()
 *   - calcularSeguro()
 *   - calcularManutencao()
 *   - getCategoria()
 *
 * Por que a classe e ABSTRACT?
 *   - Nao faz sentido criar um "Veiculo generico" sem categoria.
 *   - abstract impede: new Veiculo(...)  (erro de compilacao)
 *   - So e permitido: new Popular(...), new Sedan(...), new SUV(...)
 *
 * Construtor PROTECTED:
 *   - protected permite que as subclasses chamem super(...)
 *   - mas impede que classes de outros pacotes instanciem Veiculo
 *     diretamente (reforca o uso da hierarquia).
 *
 * =====================================================================
 * ENCAPSULAMENTO (tambem aplicado aqui)
 * =====================================================================
 * Atributos tecnicos do veiculo (placa, ano, km, status) sao private.
 * Acesso somente via getters/setters com validacao (placa no formato
 * correto, ano no intervalo permitido, km nao negativa, etc.).
 *
 * =====================================================================
 * POLIMORFISMO (ligado a heranca)
 * =====================================================================
 * Metodos abstract sao resolvidos em tempo de EXECUCAO conforme o
 * tipo real do objeto (Popular, Sedan ou SUV), mesmo quando a
 * variavel de referencia e do tipo Veiculo.
 */
public abstract class Veiculo implements Serializable {

    private static final long serialVersionUID = 1L;

    // =================================================================
    // ATRIBUTOS PRIVADOS — ENCAPSULAMENTO dos dados tecnicos do veiculo
    // =================================================================
    // private: subclasses NAO acessam estes campos diretamente.
    // Elas usam getPlaca(), getAno(), etc. Isso evita que uma subclasse
    // altere placa/ano sem passar pela validacao dos setters.
    private String placa;
    private String marca;
    private String modelo;
    private int ano;
    private StatusVeiculo status;   // enum: DISPONIVEL, OCUPADO, MANUTENCAO
    private double quilometragem;

    /**
     * Construtor PROTECTED — detalhe importante de HERANCA.
     *
     * protected (e nao public): apenas subclasses deste pacote (ou
     * subclasses em geral) podem chama-lo via super(...).
     * Classes externas nao conseguem instanciar Veiculo.
     *
     * Fluxo tipico na subclasse:
     *   public Popular(...) throws VeiculoInvalidoException {
     *       super(placa, marca, modelo, ano, quilometragem); // chama este construtor
     *   }
     *
     * Ja aplica validacao em todos os campos e inicia status = DISPONIVEL.
     */
    protected Veiculo(String placa, String marca, String modelo, int ano, double quilometragem)
            throws VeiculoInvalidoException {
        setPlaca(placa);
        setMarca(marca);
        setModelo(modelo);
        setAno(ano);
        setQuilometragem(quilometragem);
        this.status = StatusVeiculo.DISPONIVEL; // todo veiculo novo nasce disponivel
    }

    // ---------- GETTERS ----------

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAno() { return ano; }
    public StatusVeiculo getStatus() { return status; }
    public double getQuilometragem() { return quilometragem; }

    /** Atalho: true se o veiculo pode ser alugado agora. */
    public boolean isDisponivel() {
        return status == StatusVeiculo.DISPONIVEL;
    }

    // ---------- SETTERS COM VALIDACAO ----------

    /**
     * Normaliza a placa (maiusculas, sem caracteres especiais) e valida tamanho.
     */
    public void setPlaca(String placa) throws VeiculoInvalidoException {
        if (placa == null || placa.trim().isEmpty()) {
            throw new VeiculoInvalidoException("Placa nao pode ser vazia.");
        }
        String placaLimpa = placa.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        if (placaLimpa.length() < 7 || placaLimpa.length() > 8) {
            throw new VeiculoInvalidoException("Placa deve ter 7 ou 8 caracteres alfanumericos.");
        }
        this.placa = placaLimpa;
    }

    public void setMarca(String marca) throws VeiculoInvalidoException {
        if (marca == null || marca.trim().isEmpty()) {
            throw new VeiculoInvalidoException("Marca nao pode ser vazia.");
        }
        this.marca = marca.trim();
    }

    public void setModelo(String modelo) throws VeiculoInvalidoException {
        if (modelo == null || modelo.trim().isEmpty()) {
            throw new VeiculoInvalidoException("Modelo nao pode ser vazio.");
        }
        this.modelo = modelo.trim();
    }

    /**
     * Ano deve estar entre 1990 e (ano atual + 1).
     */
    public void setAno(int ano) throws VeiculoInvalidoException {
        int anoAtual = java.time.Year.now().getValue();
        if (ano < 1990 || ano > anoAtual + 1) {
            throw new VeiculoInvalidoException(
                    "Ano do veiculo invalido (permitido: 1990 a " + (anoAtual + 1) + ").");
        }
        this.ano = ano;
    }

    /**
     * Altera o status operacional. Usado pelo servico ao abrir/fechar locacao
     * ou colocar em manutencao.
     */
    public void setStatus(StatusVeiculo status) {
        if (status == null) {
            throw new IllegalArgumentException("Status nao pode ser nulo.");
        }
        this.status = status;
    }

    public void setQuilometragem(double quilometragem) throws VeiculoInvalidoException {
        if (quilometragem < 0) {
            throw new VeiculoInvalidoException("Quilometragem nao pode ser negativa.");
        }
        this.quilometragem = quilometragem;
    }

    // =================================================================
    // METODOS ABSTRATOS — ponto central da HERANCA + POLIMORFISMO
    // =================================================================
    // abstract = NAO tem corpo aqui. Cada subclasse OBRIGATORIAMENTE
    // implementa com @Override. Se esquecer, o compilador gera erro.
    //
    // Isso forca o contrato: "todo Veiculo sabe calcular diaria, seguro
    // e manutencao", mas o VALOR depende da categoria (Popular/Sedan/SUV).

    /**
     * Calcula o valor da diaria conforme a CATEGORIA do veiculo.
     * HERANCA: declarado na base; implementado de forma diferente em
     * Popular, Sedan e SUV (valores e regras proprias de cada categoria).
     */
    public abstract double calcularDiaria();

    /**
     * Calcula o valor do seguro diario conforme a categoria.
     * HERANCA: mesma ideia — contrato na base, implementacao nas filhas.
     */
    public abstract double calcularSeguro();

    /**
     * Calcula o custo estimado de manutencao (taxa fixa + parcela por km).
     * HERANCA: cada categoria define sua propria formula.
     */
    public abstract double calcularManutencao();

    /**
     * Retorna o nome da categoria ("Popular", "Sedan" ou "SUV") para relatorios.
     * HERANCA: evita usar instanceof espalhado no codigo.
     */
    public abstract String getCategoria();

    // =================================================================
    // METODO CONCRETO HERDADO — usa os abstratos (polimorfismo em acao)
    // =================================================================
    // Este metodo e HERDADO pronto pelas subclasses. Ele chama
    // calcularDiaria() e calcularSeguro(), que serao resolvidos em
    // tempo de EXECUCAO conforme o tipo real do objeto.
    // Ex.: se this for um SUV, chama SUV.calcularDiaria(), nao o da base.

    /**
     * Calcula o valor total de uma locacao de N dias (diarias + seguro).
     * Demonstra HERANCA (metodo concreto na base) + POLIMORFISMO
     * (chamadas aos metodos abstract resolvidas na subclasse).
     */
    public double calcularValorLocacao(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Numero de dias deve ser positivo.");
        }
        return (calcularDiaria() + calcularSeguro()) * dias;
    }

    // ---------- equals / hashCode baseados na placa (identificador unico) ----------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return Objects.equals(placa, veiculo.placa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(placa);
    }

    /** Texto resumido usado nas listagens do menu. */
    @Override
    public String toString() {
        return String.format("[%s] %s %s %s (%d) - %.0f km - %s | Diaria: R$ %.2f",
                getCategoria(), marca, modelo, placa, ano, quilometragem,
                status.getDescricao(), calcularDiaria());
    }
}
