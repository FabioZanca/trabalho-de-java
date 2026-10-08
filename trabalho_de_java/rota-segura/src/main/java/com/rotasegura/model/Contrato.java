package com.rotasegura.model;

import com.rotasegura.exception.DataInvalidaException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * =====================================================================
 * CLASSE: Contrato
 * =====================================================================
 * Representa um contrato de locacao (do momento da abertura ate o
 * fechamento/devolucao).
 *
 * CONCEITOS DEMONSTRADOS:
 * -----------------------
 * - POLIMORFISMO via interface Imprimivel: gerarDocumento() monta o
 *   comprovante completo; imprimir() exibe no console.
 * - ENCAPSULAMENTO: atributos private; valores so sao alterados pelos
 *   metodos de negocio (construtor e devolver()).
 * - Calculos dinamicos: usa os metodos polimorficos do Veiculo
 *   (calcularDiaria, calcularSeguro, calcularManutencao) para obter
 *   o valor correto conforme a categoria.
 *
 * Ciclo de vida:
 *   1) Construtor cria o contrato ATIVO e marca o veiculo como OCUPADO
 *      (isso e feito pelo LocadoraService).
 *   2) devolver() fecha o contrato, recalcula valores com a data real
 *      e libera o veiculo (status = DISPONIVEL).
 */
public class Contrato implements Imprimivel, Serializable {

    private static final long serialVersionUID = 1L;

    /** Formatador padrao de datas no padrao brasileiro. */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ---------- ATRIBUTOS ----------
    private final String id;                 // identificador unico (8 caracteres)
    private final Cliente cliente;           // quem alugou
    private final Veiculo veiculo;           // qual veiculo
    private final LocalDate dataInicio;      // inicio da locacao
    private LocalDate dataFimPrevista;       // previsao de devolucao
    private LocalDate dataDevolucao;         // null enquanto o contrato estiver ativo
    private double valorDiarias;
    private double valorSeguro;
    private double valorManutencao;
    private double valorTotal;
    private boolean ativo;                   // true = em andamento; false = fechado

    /**
     * Cria um novo contrato.
     * Valida as datas e calcula os valores PREVISTOS com base no periodo informado.
     *
     * @throws DataInvalidaException se as datas forem inconsistentes
     */
    public Contrato(Cliente cliente, Veiculo veiculo, LocalDate dataInicio, LocalDate dataFimPrevista)
            throws DataInvalidaException {
        validarDatas(dataInicio, dataFimPrevista);
        // Gera um ID curto e legivel a partir de um UUID
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.cliente = cliente;
        this.veiculo = veiculo;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.dataDevolucao = null;
        this.ativo = true;
        recalcularValoresPrevistos();
    }

    /**
     * Valida regras basicas de datas de locacao.
     * - Nenhuma data pode ser nula
     * - Fim deve ser ESTRITAMENTE posterior ao inicio
     * - Inicio nao pode ser no passado distante
     */
    private void validarDatas(LocalDate inicio, LocalDate fim) throws DataInvalidaException {
        if (inicio == null || fim == null) {
            throw new DataInvalidaException("Datas de inicio e fim nao podem ser nulas.");
        }
        if (fim.isBefore(inicio) || fim.isEqual(inicio)) {
            throw new DataInvalidaException(
                    "Data de devolucao prevista deve ser posterior a data de inicio.");
        }
        if (inicio.isBefore(LocalDate.now().minusDays(1))) {
            throw new DataInvalidaException("Data de inicio nao pode ser no passado distante.");
        }
    }

    /**
     * Calcula diarias + seguro + manutencao com base no periodo PREVISTO.
     * Chama os metodos polimorficos do veiculo (diaria/seguro mudam conforme categoria).
     */
    private void recalcularValoresPrevistos() {
        long dias = ChronoUnit.DAYS.between(dataInicio, dataFimPrevista);
        if (dias < 1) dias = 1;
        this.valorDiarias = veiculo.calcularDiaria() * dias;
        this.valorSeguro = veiculo.calcularSeguro() * dias;
        this.valorManutencao = veiculo.calcularManutencao(); // taxa unica por contrato
        this.valorTotal = valorDiarias + valorSeguro + valorManutencao;
    }

    /**
     * Realiza a DEVOLUCAO do veiculo e FECHA o contrato.
     * Recalcula os valores com a data EFETIVA de devolucao
     * (pode ser diferente da prevista — antecipacao ou atraso).
     *
     * @param dataDevolucaoEfetiva data real em que o cliente devolveu o carro
     * @throws DataInvalidaException se o contrato ja estiver fechado ou a data for invalida
     */
    public void devolver(LocalDate dataDevolucaoEfetiva) throws DataInvalidaException {
        if (!ativo) {
            throw new DataInvalidaException("Contrato ja esta fechado.");
        }
        if (dataDevolucaoEfetiva == null) {
            throw new DataInvalidaException("Data de devolucao nao pode ser nula.");
        }
        if (dataDevolucaoEfetiva.isBefore(dataInicio)) {
            throw new DataInvalidaException(
                    "Data de devolucao nao pode ser anterior a data de inicio da locacao.");
        }
        this.dataDevolucao = dataDevolucaoEfetiva;

        // Recalcula com o periodo real
        long dias = ChronoUnit.DAYS.between(dataInicio, dataDevolucaoEfetiva);
        if (dias < 1) dias = 1;

        this.valorDiarias = veiculo.calcularDiaria() * dias;
        this.valorSeguro = veiculo.calcularSeguro() * dias;
        this.valorManutencao = veiculo.calcularManutencao();
        this.valorTotal = valorDiarias + valorSeguro + valorManutencao;

        this.ativo = false;
        // Libera o veiculo para nova locacao
        this.veiculo.setStatus(StatusVeiculo.DISPONIVEL);
    }

    // ---------- GETTERS ----------

    public String getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Veiculo getVeiculo() { return veiculo; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public double getValorDiarias() { return valorDiarias; }
    public double getValorSeguro() { return valorSeguro; }
    public double getValorManutencao() { return valorManutencao; }
    public double getValorTotal() { return valorTotal; }
    public boolean isAtivo() { return ativo; }

    /**
     * Quantidade de dias efetivamente cobrados.
     * Se ainda ativo, usa a data prevista; se fechado, usa a data de devolucao.
     */
    public long getDiasLocados() {
        LocalDate fim = (dataDevolucao != null) ? dataDevolucao : dataFimPrevista;
        long dias = ChronoUnit.DAYS.between(dataInicio, fim);
        return dias < 1 ? 1 : dias;
    }

    // ---------- IMPLEMENTACAO DA INTERFACE Imprimivel (POLIMORFISMO) ----------

    /**
     * Gera o texto completo do contrato / comprovante de devolucao.
     * Layout em formato de "recibo" legivel no console e em arquivo .txt.
     */
    @Override
    public String gerarDocumento() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("           LOCADORA ROTA SEGURA - CONTRATO DE LOCACAO\n");
        sb.append("============================================================\n");
        sb.append(String.format("Contrato No: %s%n", id));
        sb.append(String.format("Status     : %s%n", ativo ? "ATIVO" : "FECHADO"));
        sb.append("------------------------------------------------------------\n");
        sb.append("CLIENTE\n");
        sb.append(String.format("  Nome : %s%n", cliente.getNome()));
        sb.append(String.format("  CPF  : %s%n", formatarCpf(cliente.getCpf())));
        sb.append(String.format("  Tel  : %s%n", cliente.getTelefone()));
        sb.append(String.format("  Email: %s%n", cliente.getEmail()));
        sb.append("------------------------------------------------------------\n");
        sb.append("VEICULO\n");
        sb.append(String.format("  Categoria: %s%n", veiculo.getCategoria()));
        sb.append(String.format("  Marca/Modelo: %s %s%n", veiculo.getMarca(), veiculo.getModelo()));
        sb.append(String.format("  Placa: %s | Ano: %d%n", veiculo.getPlaca(), veiculo.getAno()));
        sb.append(String.format("  Km atual: %.0f%n", veiculo.getQuilometragem()));
        sb.append("------------------------------------------------------------\n");
        sb.append("PERIODO\n");
        sb.append(String.format("  Inicio           : %s%n", dataInicio.format(FMT)));
        sb.append(String.format("  Fim previsto     : %s%n", dataFimPrevista.format(FMT)));
        if (dataDevolucao != null) {
            sb.append(String.format("  Devolucao efetiva: %s%n", dataDevolucao.format(FMT)));
        }
        sb.append(String.format("  Dias             : %d%n", getDiasLocados()));
        sb.append("------------------------------------------------------------\n");
        sb.append("VALORES\n");
        sb.append(String.format("  Diarias (%d x R$ %.2f): R$ %.2f%n",
                getDiasLocados(), veiculo.calcularDiaria(), valorDiarias));
        sb.append(String.format("  Seguro                    : R$ %.2f%n", valorSeguro));
        sb.append(String.format("  Taxa de manutencao        : R$ %.2f%n", valorManutencao));
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("  TOTAL A PAGAR            : R$ %.2f%n", valorTotal));
        sb.append("============================================================\n");
        if (!ativo) {
            sb.append("          *** COMPROVANTE DE DEVOLUCAO / FECHAMENTO ***\n");
            sb.append("============================================================\n");
        }
        return sb.toString();
    }

    /** Formata CPF no padrao 000.000.000-00 para exibicao. */
    private String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." +
               cpf.substring(6, 9) + "-" + cpf.substring(9);
    }

    /** Resumo de uma linha para listagens no menu. */
    @Override
    public String toString() {
        return String.format("Contrato %s | %s | %s %s | %s a %s | R$ %.2f | %s",
                id, cliente.getNome(), veiculo.getMarca(), veiculo.getModelo(),
                dataInicio.format(FMT),
                (dataDevolucao != null ? dataDevolucao : dataFimPrevista).format(FMT),
                valorTotal, ativo ? "ATIVO" : "FECHADO");
    }
}
