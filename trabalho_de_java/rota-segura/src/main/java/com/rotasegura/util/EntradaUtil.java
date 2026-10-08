package com.rotasegura.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * =====================================================================
 * CLASSE UTILITARIA: EntradaUtil
 * =====================================================================
 * Centraliza a LEITURA SEGURA de dados digitados pelo usuario no console.
 *
 * Por que existe?
 * - Evita repetir o mesmo codigo de "ler e validar" em dezenas de pontos do Main.
 * - Garante que numeros e datas invalidas sejam rejeitados com mensagem clara
 *   e o usuario possa tentar de novo (loop interno), sem derrubar o programa.
 *
 * final class: nao deve ser herdada (utilitario puro).
 */
public final class EntradaUtil {

    /** Formato de data esperado do usuario: dia/mes/ano. */
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner;

    public EntradaUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Le uma linha de texto e remove espacos das extremidades.
     */
    public String lerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Le um inteiro. Se o usuario digitar algo que nao e numero,
     * exibe erro e pede novamente (nao lanca excecao para o Main).
     */
    public int lerInteiro(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.println("  [ERRO] Digite um numero inteiro valido.");
            }
        }
    }

    /**
     * Le um numero decimal. Aceita virgula ou ponto como separador.
     */
    public double lerDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linha = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linha);
            } catch (NumberFormatException e) {
                System.out.println("  [ERRO] Digite um numero valido (ex: 15000 ou 15000.5).");
            }
        }
    }

    /**
     * Le uma data no formato dd/MM/yyyy.
     * Continua pedindo ate receber uma data valida.
     */
    public LocalDate lerData(String prompt) {
        while (true) {
            System.out.print(prompt + " (dd/MM/yyyy): ");
            String linha = scanner.nextLine().trim();
            try {
                return LocalDate.parse(linha, FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  [ERRO] Data invalida. Use o formato dd/MM/yyyy (ex: 15/03/2026).");
            }
        }
    }

    /**
     * Pergunta de confirmacao Sim/Nao.
     * @return true se a resposta comecar com "S" (ignorando maiusculas)
     */
    public boolean confirmar(String prompt) {
        System.out.print(prompt + " (S/N): ");
        String resp = scanner.nextLine().trim().toUpperCase();
        return resp.startsWith("S");
    }
}
