package com.rotasegura.service;

import com.rotasegura.model.Cliente;
import com.rotasegura.model.Contrato;
import com.rotasegura.model.Veiculo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * =====================================================================
 * CLASSE: PersistenciaService
 * =====================================================================
 * Responsavel por GRAVAR e LER os dados do sistema em disco.
 *
 * REQUISITO ATENDIDO: "Persistir os dados em arquivos ... para preservar
 * o historico de locacoes."
 *
 * =====================================================================
 * GENERICS nesta classe
 * =====================================================================
 * Os metodos publicos usam colecoes tipadas:
 *   salvarClientes(List<Cliente> clientes)
 *   carregarClientes() -> List<Cliente>
 *   salvarVeiculos(List<Veiculo> veiculos)
 *   carregarVeiculos() -> List<Veiculo>
 *   salvarContratos(List<Contrato> contratos)
 *   carregarContratos() -> List<Contrato>
 *
 * Assim, LocadoraService e PersistenciaService "conversam" sempre com
 * tipos seguros. O cast (List<Cliente>) no carregar e necessario porque
 * ObjectInputStream devolve Object; o @SuppressWarnings("unchecked")
 * documenta que confiamos no conteudo do arquivo que nos mesmos gravamos.
 *
 * Estrategia: SERIALIZACAO BINARIA Java (ObjectOutputStream /
 * ObjectInputStream). Vantagens para um trabalho academico:
 *   - Nao exige bibliotecas externas (Gson, Jackson, etc.)
 *   - Preserva a hierarquia de objetos (Popular/Sedan/SUV continuam
 *     sendo do tipo correto ao serem relidos) — HERANCA + persistencia
 *   - Simples de implementar
 *
 * Arquivos gerados na pasta data/:
 *   clientes.dat   - lista de Cliente
 *   veiculos.dat   - lista de Veiculo (e subclasses)
 *   contratos.dat  - lista de Contrato
 *   comprovante_XXXXXXXX.txt - comprovantes exportados em texto legivel
 */
public class PersistenciaService {

    /** Caminho da pasta onde os arquivos serao gravados. */
    private final Path diretorioData;

    /**
     * @param caminhoDiretorio caminho relativo ou absoluto da pasta de dados
     *                         (ex.: "data"). A pasta e criada se nao existir.
     */
    public PersistenciaService(String caminhoDiretorio) {
        this.diretorioData = Paths.get(caminhoDiretorio);
        try {
            Files.createDirectories(diretorioData);
        } catch (IOException e) {
            System.err.println("Aviso: nao foi possivel criar diretorio de dados: " + e.getMessage());
        }
    }

    // ---------- CLIENTES (Generics: List<Cliente>) ----------

    /**
     * Grava a lista completa de clientes em clientes.dat.
     * GENERICS: parametro List<Cliente> — so aceita lista de Cliente.
     */
    public void salvarClientes(List<Cliente> clientes) throws IOException {
        salvarObjeto(clientes, "clientes.dat");
    }

    /**
     * Le a lista de clientes. Se o arquivo nao existir ou houver erro,
     * retorna uma lista vazia (sistema comeca "limpo").
     * GENERICS: retorno List<Cliente> tipado para o chamador.
     */
    @SuppressWarnings("unchecked")
    public List<Cliente> carregarClientes() {
        Object obj = carregarObjeto("clientes.dat");
        if (obj instanceof List) {
            return (List<Cliente>) obj;
        }
        return new ArrayList<>();
    }

    // ---------- VEICULOS (Generics: List<Veiculo> — inclui subclasses) ----------

    /**
     * GENERICS + HERANCA: List<Veiculo> armazena Popular, Sedan e SUV
     * porque todos estendem Veiculo. Na desserializacao, o tipo real
     * de cada objeto e preservado pela serializacao Java.
     */
    public void salvarVeiculos(List<Veiculo> veiculos) throws IOException {
        salvarObjeto(veiculos, "veiculos.dat");
    }

    @SuppressWarnings("unchecked")
    public List<Veiculo> carregarVeiculos() {
        Object obj = carregarObjeto("veiculos.dat");
        if (obj instanceof List) {
            return (List<Veiculo>) obj;
        }
        return new ArrayList<>();
    }

    // ---------- CONTRATOS ----------

    public void salvarContratos(List<Contrato> contratos) throws IOException {
        salvarObjeto(contratos, "contratos.dat");
    }

    @SuppressWarnings("unchecked")
    public List<Contrato> carregarContratos() {
        Object obj = carregarObjeto("contratos.dat");
        if (obj instanceof List) {
            return (List<Contrato>) obj;
        }
        return new ArrayList<>();
    }

    // ---------- METODOS AUXILIARES PRIVADOS ----------

    /**
     * Serializa qualquer objeto Java em um arquivo binario.
     * Usa BufferedOutputStream para melhor desempenho em disco.
     */
    private void salvarObjeto(Object objeto, String nomeArquivo) throws IOException {
        Path arquivo = diretorioData.resolve(nomeArquivo);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(arquivo)))) {
            oos.writeObject(objeto);
        }
    }

    /**
     * Desserializa um objeto a partir do arquivo.
     * Retorna null se o arquivo nao existir ou se ocorrer erro de leitura.
     */
    private Object carregarObjeto(String nomeArquivo) {
        Path arquivo = diretorioData.resolve(nomeArquivo);
        if (!Files.exists(arquivo)) {
            return null; // primeira execucao: ainda nao ha dados
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(arquivo)))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar " + nomeArquivo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Exporta um documento (contrato/comprovante) em arquivo de TEXTO puro,
     * legivel por qualquer editor. Nome tipico: comprovante_A1B2C3D4.txt
     */
    public void exportarDocumentoTexto(String conteudo, String nomeArquivo) throws IOException {
        Path arquivo = diretorioData.resolve(nomeArquivo);
        Files.writeString(arquivo, conteudo);
    }
}
