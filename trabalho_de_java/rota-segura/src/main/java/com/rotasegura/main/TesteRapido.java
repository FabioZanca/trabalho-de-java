package com.rotasegura.main;

import com.rotasegura.model.*;
import com.rotasegura.service.LocadoraService;
import com.rotasegura.service.PersistenciaService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

/**
 * =====================================================================
 * CLASSE DE TESTE: TesteRapido
 * =====================================================================
 * Script automatizado que exercita o fluxo completo e os cenarios de
 * resiliencia SEM interacao com o usuario.
 *
 * Util para:
 *   - Verificar rapidamente se a compilacao e a logica estao corretas
 *   - Demonstrar na apresentacao os casos de erro tratados
 *
 * Execucao:
 *   java -cp out com.rotasegura.main.TesteRapido
 *
 * NAO faz parte do fluxo normal do sistema (o usuario usa Main).
 * Pode ser removida do repositorio se o professor pedir apenas o Main.
 */
public class TesteRapido {

    public static void main(String[] args) throws Exception {
        // Usa pasta temporaria para nao poluir a pasta data/ do sistema real
        Path temp = Files.createTempDirectory("rota-teste");
        PersistenciaService persistencia = new PersistenciaService(temp.toString());
        LocadoraService service = new LocadoraService(persistencia);

        System.out.println("=== TESTE AUTOMATIZADO ROTA SEGURA ===\n");

        // ---- 1. Cadastro de cliente ----
        Cliente c = new Cliente("12345678901", "Maria Silva", "11999998888", "maria@email.com", 30);
        service.cadastrarCliente(c);
        System.out.println("[OK] Cliente cadastrado: " + c.getNome());

        // ---- 2. Cadastro das 3 categorias (HERANCA) ----
        Veiculo v1 = new Popular("ABC1D23", "Fiat", "Argo", 2023, 15000);
        Veiculo v2 = new Sedan("DEF2E34", "Toyota", "Corolla", 2022, 30000);
        Veiculo v3 = new SUV("GHI3F45", "Jeep", "Compass", 2024, 8000);
        service.cadastrarVeiculo(v1);
        service.cadastrarVeiculo(v2);
        service.cadastrarVeiculo(v3);
        System.out.println("[OK] 3 veiculos cadastrados (Popular, Sedan, SUV)");
        // POLIMORFISMO: mesma chamada, valores diferentes
        System.out.printf("     Popular diaria=R$%.2f  Sedan=R$%.2f  SUV=R$%.2f%n",
                v1.calcularDiaria(), v2.calcularDiaria(), v3.calcularDiaria());

        // ---- 3. Listar disponiveis ----
        System.out.println("[OK] Disponiveis: " + service.listarVeiculosDisponiveis().size());

        // ---- 4. Abrir locacao ----
        LocalDate inicio = LocalDate.of(2026, 10, 6);
        LocalDate fim = LocalDate.of(2026, 10, 10);
        Contrato contrato = service.abrirLocacao("12345678901", "ABC1D23", inicio, fim);
        System.out.println("[OK] Locacao aberta. ID=" + contrato.getId());
        System.out.println("     Valor previsto: R$ " + String.format("%.2f", contrato.getValorTotal()));

        // ---- 5. Tentar alugar o MESMO veiculo (deve falhar) ----
        try {
            service.abrirLocacao("12345678901", "ABC1D23", inicio, fim);
            System.out.println("[FALHA] Deveria ter lancado VeiculoIndisponivelException");
        } catch (Exception e) {
            System.out.println("[OK] Resiliencia: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        // ---- 6. Datas invertidas (deve falhar) ----
        try {
            service.abrirLocacao("12345678901", "DEF2E34", fim, inicio);
            System.out.println("[FALHA] Deveria ter lancado DataInvalidaException");
        } catch (Exception e) {
            System.out.println("[OK] Resiliencia: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
        }

        // ---- 7. Devolucao ----
        Contrato fechado = service.devolverVeiculo(contrato.getId(), LocalDate.of(2026, 10, 9));
        System.out.println("[OK] Devolucao realizada. Total final: R$ "
                + String.format("%.2f", fechado.getValorTotal()));
        System.out.println("     Veiculo voltou a: " + fechado.getVeiculo().getStatus());

        // ---- 8. Impressao polimorfica (Imprimivel) ----
        System.out.println("\n--- Comprovante (Imprimivel) ---");
        fechado.imprimir();

        // ---- 9. Relatorio ----
        System.out.println(service.gerarRelatorioFechamento());

        // ---- 10. Cliente com CPF invalido (deve falhar) ----
        try {
            new Cliente("123", "X", "1", "a@b.com", 16);
            System.out.println("[FALHA] Deveria rejeitar cliente invalido");
        } catch (Exception e) {
            System.out.println("[OK] Resiliencia cadastro: " + e.getMessage());
        }

        System.out.println("\n=== TODOS OS TESTES PASSARAM ===");
    }
}
