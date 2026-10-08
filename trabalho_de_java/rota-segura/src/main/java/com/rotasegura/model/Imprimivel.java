package com.rotasegura.model;

/**
 * =====================================================================
 * INTERFACE: Imprimivel
 * =====================================================================
 * Define um contrato padronizado para qualquer classe que precise
 * GERAR e IMPRIMIR documentos (contratos, comprovantes, relatorios).
 *
 * POR QUE USAR INTERFACE? (Polimorfismo)
 * ----------------------------------------
 * Diferentes classes podem implementar Imprimivel de formas distintas:
 *   - Contrato gera um comprovante de locacao/devolucao
 *   - No futuro, um RelatorioMensal poderia gerar outro formato
 *
 * O codigo cliente (Main, LocadoraService) chama apenas:
 *     imprimivel.gerarDocumento()  ou  imprimivel.imprimir()
 * sem precisar saber qual classe concreta esta por baixo.
 *
 * Isso atende o requisito: "Usar uma interface padronizada para gerar
 * e imprimir contratos e relatorios de fechamento."
 */
public interface Imprimivel {

    /**
     * Monta o conteudo textual completo do documento.
     * Cada implementacao decide o layout (tabelas, cabecalhos, totais...).
     *
     * @return String formatada pronta para exibicao no console
     *         ou gravacao em arquivo .txt
     */
    String gerarDocumento();

    /**
     * Metodo DEFAULT (Java 8+): ja tem implementacao na propria interface.
     * Classes que implementam Imprimivel NAO precisam sobrescrever este metodo,
     * a menos que queiram um comportamento diferente de impressao.
     *
     * Aqui simplesmente imprime no console o resultado de gerarDocumento().
     */
    default void imprimir() {
        System.out.println(gerarDocumento());
    }
}
