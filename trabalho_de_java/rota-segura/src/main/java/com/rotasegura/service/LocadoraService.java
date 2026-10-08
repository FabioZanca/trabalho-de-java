package com.rotasegura.service;

import com.rotasegura.exception.*;
import com.rotasegura.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * =====================================================================
 * CLASSE: LocadoraService
 * =====================================================================
 * Nucleo de REGRAS DE NEGOCIO da locadora.
 *
 * =====================================================================
 * GENERICS / TIPOS GENERICOS (conceito obrigatorio do TP)
 * =====================================================================
 * Generics permitem parametrizar tipos em classes e interfaces.
 * Em vez de usar List "crua" (que aceita qualquer Object), usamos:
 *
 *     List<Cliente>   — so aceita objetos Cliente
 *     List<Veiculo>   — so aceita objetos Veiculo (e subclasses!)
 *     List<Contrato>  — so aceita objetos Contrato
 *
 * Vantagens:
 *   1) SEGURANCA EM TEMPO DE COMPILACAO
 *      clientes.add(new Popular(...));  // ERRO de compilacao!
 *      O compilador impede misturar tipos e evita ClassCastException
 *      em tempo de execucao.
 *
 *   2) CODIGO MAIS CLARO
 *      Quem le List<Cliente> ja sabe que cada elemento e um Cliente.
 *      Nao precisa de casts manuais: Cliente c = clientes.get(0);
 *
 *   3) FUNCIONA COM HERANCA
 *      List<Veiculo> pode guardar Popular, Sedan e SUV porque
 *      todos SAO Veiculo (polimorfismo + generics juntos).
 *
 *   4) METODOS QUE DEVOLVEM COLECOES TIPADAS
 *      listarClientes() retorna List<Cliente>
 *      listarVeiculosDisponiveis() retorna List<Veiculo>
 *      O chamador sabe exatamente o que vai receber.
 *
 * Onde mais aparece Generics neste projeto:
 *   - PersistenciaService.salvarClientes(List<Cliente>)
 *   - PersistenciaService.carregarClientes() -> List<Cliente>
 *   - Optional<Cliente>, Optional<Veiculo>, Optional<Contrato>
 *   - Stream API: veiculos.stream().filter(...).collect(...)
 *
 * Outros conceitos nesta classe:
 *   - RESILIENCIA: valida veiculo ocupado, datas, cliente inexistente
 *   - POLIMORFISMO: chama metodos de Veiculo sem saber a subclasse
 *
 * Esta classe NAO conhece o console (Scanner). Ela apenas processa
 * dados e devolve resultados. A interacao com o usuario fica no Main.
 */
public class LocadoraService {

    // =================================================================
    // COLECOES GENERICAS — requisito de Tipos Genericos do TP
    // =================================================================
    // List<T> e uma interface generica do Java (java.util.List).
    // O parametro <T> fixa o tipo dos elementos:
    //
    //   List<Cliente>  -> T = Cliente
    //   List<Veiculo>  -> T = Veiculo  (aceita Popular, Sedan, SUV)
    //   List<Contrato> -> T = Contrato
    //
    // private final: encapsulamento — so esta classe manipula as listas;
    // metodos publicos devolvem copias (new ArrayList<>(...)).
    private final List<Cliente> clientes;
    private final List<Veiculo> veiculos;
    private final List<Contrato> contratos;

    /** Servico de persistencia injetado no construtor. */
    private final PersistenciaService persistencia;

    /**
     * Ao criar o servico, carrega automaticamente o historico gravado em disco.
     * Se for a primeira execucao, as listas ficam vazias.
     *
     * GENERICS na inicializacao:
     *   new ArrayList<>(outraLista) cria um ArrayList tipado com os
     *   mesmos elementos. O diamante <> (Java 7+) infere o tipo a partir
     *   da declaracao da variavel (List<Cliente>, List<Veiculo>, etc.).
     */
    public LocadoraService(PersistenciaService persistencia) {
        this.persistencia = persistencia;
        // ArrayList<> e a implementacao concreta; o tipo da variavel e a interface List<T>
        this.clientes = new ArrayList<>(persistencia.carregarClientes());
        this.veiculos = new ArrayList<>(persistencia.carregarVeiculos());
        this.contratos = new ArrayList<>(persistencia.carregarContratos());
    }

    // =================================================================
    // CLIENTES
    // =================================================================

    /**
     * Cadastra um novo cliente, impedindo CPF duplicado.
     * @throws ClienteInvalidoException se o CPF ja existir
     */
    public void cadastrarCliente(Cliente cliente) throws ClienteInvalidoException {
        if (buscarClientePorCpf(cliente.getCpf()).isPresent()) {
            throw new ClienteInvalidoException(
                    "Ja existe cliente cadastrado com o CPF " + cliente.getCpf());
        }
        clientes.add(cliente);
        salvarTudo(); // persiste imediatamente apos cada operacao critica
    }

    /**
     * Busca cliente pelo CPF (aceita formatado ou so digitos).
     * @return Optional - presente se encontrou, vazio caso contrario
     */
    public Optional<Cliente> buscarClientePorCpf(String cpf) {
        String cpfLimpo = cpf == null ? "" : cpf.replaceAll("[^0-9]", "");
        return clientes.stream()
                .filter(c -> c.getCpf().equals(cpfLimpo))
                .findFirst();
    }

    /**
     * Retorna uma COPIA da lista (copia defensiva).
     * Evita que codigo externo altere a colecao interna sem passar pelo servico.
     *
     * GENERICS: o tipo de retorno List<Cliente> garante que o chamador
     * recebe apenas Clientes — sem necessidade de cast.
     */
    public List<Cliente> listarClientes() {
        return new ArrayList<>(clientes);
    }

    // =================================================================
    // VEICULOS
    // =================================================================

    /**
     * Cadastra veiculo na frota, impedindo placa duplicada.
     */
    public void cadastrarVeiculo(Veiculo veiculo) throws VeiculoInvalidoException {
        if (buscarVeiculoPorPlaca(veiculo.getPlaca()).isPresent()) {
            throw new VeiculoInvalidoException(
                    "Ja existe veiculo com a placa " + veiculo.getPlaca());
        }
        veiculos.add(veiculo);
        salvarTudo();
    }

    public Optional<Veiculo> buscarVeiculoPorPlaca(String placa) {
        String placaLimpa = placa == null ? "" :
                placa.trim().toUpperCase().replaceAll("[^A-Z0-9]", "");
        return veiculos.stream()
                .filter(v -> v.getPlaca().equals(placaLimpa))
                .findFirst();
    }

    public List<Veiculo> listarVeiculos() {
        return new ArrayList<>(veiculos);
    }

    /**
     * Filtra apenas veiculos com status DISPONIVEL.
     * GENERICS + Stream: a lista de origem e List<Veiculo>; o resultado
     * do collect tambem e List<Veiculo>. O compilador garante o tipo
     * em toda a cadeia (filter, collect).
     */
    public List<Veiculo> listarVeiculosDisponiveis() {
        return veiculos.stream()
                .filter(Veiculo::isDisponivel) // method reference
                .collect(Collectors.toList());
    }

    public List<Veiculo> listarVeiculosPorCategoria(String categoria) {
        return veiculos.stream()
                .filter(v -> v.getCategoria().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }

    /**
     * Coloca veiculo em manutencao (so se nao estiver ocupado).
     */
    public void colocarEmManutencao(String placa) throws VeiculoIndisponivelException {
        Veiculo v = buscarVeiculoPorPlaca(placa)
                .orElseThrow(() -> new VeiculoIndisponivelException(
                        "Veiculo nao encontrado: " + placa));
        if (v.getStatus() == StatusVeiculo.OCUPADO) {
            throw new VeiculoIndisponivelException(
                    "Nao e possivel colocar em manutencao um veiculo ocupado.");
        }
        v.setStatus(StatusVeiculo.MANUTENCAO);
        salvarTudo();
    }

    public void liberarDeManutencao(String placa) throws VeiculoIndisponivelException {
        Veiculo v = buscarVeiculoPorPlaca(placa)
                .orElseThrow(() -> new VeiculoIndisponivelException(
                        "Veiculo nao encontrado: " + placa));
        if (v.getStatus() != StatusVeiculo.MANUTENCAO) {
            throw new VeiculoIndisponivelException("Veiculo nao esta em manutencao.");
        }
        v.setStatus(StatusVeiculo.DISPONIVEL);
        salvarTudo();
    }

    // =================================================================
    // LOCACOES (abrir / devolver)
    // =================================================================

    /**
     * Abre uma nova locacao.
     * Passos de validacao (RESILIENCIA):
     *   1. Cliente deve existir
     *   2. Veiculo deve existir
     *   3. Veiculo deve estar DISPONIVEL
     *   4. Nao pode haver outro contrato ATIVO para a mesma placa
     *   5. Datas devem ser consistentes (validado dentro de Contrato)
     *
     * Se tudo ok: cria o Contrato, marca veiculo como OCUPADO e persiste.
     */
    public Contrato abrirLocacao(String cpfCliente, String placaVeiculo,
                                 LocalDate dataInicio, LocalDate dataFimPrevista)
            throws ClienteInvalidoException, VeiculoIndisponivelException, DataInvalidaException {

        Cliente cliente = buscarClientePorCpf(cpfCliente)
                .orElseThrow(() -> new ClienteInvalidoException(
                        "Cliente nao encontrado com CPF: " + cpfCliente));

        Veiculo veiculo = buscarVeiculoPorPlaca(placaVeiculo)
                .orElseThrow(() -> new VeiculoIndisponivelException(
                        "Veiculo nao encontrado: " + placaVeiculo));

        if (!veiculo.isDisponivel()) {
            throw new VeiculoIndisponivelException(
                    "Veiculo " + placaVeiculo + " nao esta disponivel. Status atual: "
                            + veiculo.getStatus());
        }

        // Dupla verificacao: status + existencia de contrato ativo
        boolean jaAlugado = contratos.stream()
                .anyMatch(c -> c.isAtivo() && c.getVeiculo().getPlaca().equals(veiculo.getPlaca()));
        if (jaAlugado) {
            throw new VeiculoIndisponivelException("Veiculo ja possui locacao ativa.");
        }

        Contrato contrato = new Contrato(cliente, veiculo, dataInicio, dataFimPrevista);
        veiculo.setStatus(StatusVeiculo.OCUPADO);
        contratos.add(contrato);
        salvarTudo();
        return contrato;
    }

    /**
     * Devolve o veiculo e fecha o contrato.
     * O metodo devolver() do Contrato recalcula os valores finais
     * e libera o veiculo (status = DISPONIVEL).
     */
    public Contrato devolverVeiculo(String idContrato, LocalDate dataDevolucao)
            throws DataInvalidaException, VeiculoIndisponivelException {

        Contrato contrato = buscarContratoPorId(idContrato)
                .orElseThrow(() -> new VeiculoIndisponivelException(
                        "Contrato nao encontrado: " + idContrato));

        if (!contrato.isAtivo()) {
            throw new DataInvalidaException("Este contrato ja foi fechado.");
        }

        contrato.devolver(dataDevolucao);
        salvarTudo();
        return contrato;
    }

    public Optional<Contrato> buscarContratoPorId(String id) {
        return contratos.stream()
                .filter(c -> c.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public List<Contrato> listarContratos() {
        return new ArrayList<>(contratos);
    }

    public List<Contrato> listarContratosAtivos() {
        return contratos.stream()
                .filter(Contrato::isAtivo)
                .collect(Collectors.toList());
    }

    public List<Contrato> listarContratosFechados() {
        return contratos.stream()
                .filter(c -> !c.isAtivo())
                .collect(Collectors.toList());
    }

    // =================================================================
    // RELATORIOS
    // =================================================================

    /**
     * Monta um relatorio de fechamento consolidado (frota, contratos, faturamento).
     * O texto segue o mesmo estilo dos documentos Imprimivel.
     */
    public String gerarRelatorioFechamento() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================================\n");
        sb.append("     RELATORIO DE FECHAMENTO - LOCADORA ROTA SEGURA\n");
        sb.append("============================================================\n");
        sb.append(String.format("Total de clientes cadastrados : %d%n", clientes.size()));
        sb.append(String.format("Total de veiculos na frota    : %d%n", veiculos.size()));
        sb.append(String.format("  - Disponiveis               : %d%n",
                veiculos.stream().filter(Veiculo::isDisponivel).count()));
        sb.append(String.format("  - Ocupados                  : %d%n",
                veiculos.stream().filter(v -> v.getStatus() == StatusVeiculo.OCUPADO).count()));
        sb.append(String.format("  - Em manutencao             : %d%n",
                veiculos.stream().filter(v -> v.getStatus() == StatusVeiculo.MANUTENCAO).count()));
        sb.append(String.format("Contratos ativos              : %d%n", listarContratosAtivos().size()));
        sb.append(String.format("Contratos fechados            : %d%n", listarContratosFechados().size()));

        double faturamento = listarContratosFechados().stream()
                .mapToDouble(Contrato::getValorTotal)
                .sum();
        sb.append(String.format("Faturamento (contratos fechados): R$ %.2f%n", faturamento));
        sb.append("============================================================\n");

        sb.append("\n--- Detalhamento por categoria ---\n");
        for (String cat : List.of("Popular", "Sedan", "SUV")) {
            long qtd = veiculos.stream().filter(v -> v.getCategoria().equals(cat)).count();
            sb.append(String.format("  %s: %d veiculo(s)%n", cat, qtd));
        }
        sb.append("============================================================\n");
        return sb.toString();
    }

    // =================================================================
    // PERSISTENCIA
    // =================================================================

    /**
     * Grava as tres colecoes em disco.
     * Chamado apos cada operacao que altera o estado do sistema.
     * Erros de I/O sao apenas logados (nao interrompem o fluxo).
     */
    public void salvarTudo() {
        try {
            persistencia.salvarClientes(clientes);
            persistencia.salvarVeiculos(veiculos);
            persistencia.salvarContratos(contratos);
        } catch (IOException e) {
            System.err.println("Erro ao persistir dados: " + e.getMessage());
        }
    }

    /**
     * Exporta o comprovante de um contrato para arquivo .txt na pasta data/.
     */
    public void exportarComprovante(Contrato contrato) {
        try {
            String nome = "comprovante_" + contrato.getId() + ".txt";
            persistencia.exportarDocumentoTexto(contrato.gerarDocumento(), nome);
            System.out.println("Comprovante exportado para: data/" + nome);
        } catch (IOException e) {
            System.err.println("Erro ao exportar comprovante: " + e.getMessage());
        }
    }
}
