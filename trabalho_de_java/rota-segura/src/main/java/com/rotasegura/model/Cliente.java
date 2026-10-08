package com.rotasegura.model;

import com.rotasegura.exception.ClienteInvalidoException;
import java.io.Serializable;
import java.util.Objects;

/**
 * =====================================================================
 * CLASSE: Cliente
 * =====================================================================
 * Representa um cliente da locadora Rota Segura.
 *
 * =====================================================================
 * ENCAPSULAMENTO (conceito obrigatorio do TP)
 * =====================================================================
 * O encapsulamento consiste em ESCONDER os detalhes internos de uma
 * classe e expor apenas uma interface controlada de acesso.
 *
 * Como isso aparece aqui:
 *
 * 1) ATRIBUTOS PRIVATE
 *    - cpf, nome, telefone, email e idade sao private.
 *    - Nenhum codigo de fora (Main, LocadoraService, etc.) consegue
 *      fazer cliente.cpf = "xxx" diretamente. Isso protege os dados
 *      cadastrais sensiveis exigidos pelo enunciado.
 *
 * 2) GETTERS (leitura controlada)
 *    - Permitem CONSULTAR o valor sem abrir a possibilidade de alterar.
 *    - Ex.: getCpf() devolve o CPF; o chamador nao consegue mudar o campo.
 *
 * 3) SETTERS COM VALIDACAO (alteracao controlada)
 *    - Toda alteracao passa por regras de negocio:
 *        setCpf()    -> exige 11 digitos numericos
 *        setNome()   -> exige no minimo 3 caracteres
 *        setEmail()  -> exige a presenca de '@'
 *        setIdade()  -> exige idade >= 18
 *    - Se a regra for violada, lanca ClienteInvalidoException.
 *    - Assim o objeto NUNCA fica em estado inconsistente.
 *
 * 4) CONSTRUTOR DELEGA AOS SETTERS
 *    - Em vez de atribuir this.cpf = cpf diretamente, chama setCpf(cpf).
 *    - Garante que a mesma validacao valha tanto na criacao quanto
 *      em alteracoes futuras.
 *
 * Resumo didatico:
 *   "Encapsular = proteger os dados + controlar como eles sao lidos
 *    e modificados, com validacao centralizada na propria classe."
 *
 * Serializable: permite gravar objetos Cliente em arquivo binario
 * (persistencia via ObjectOutputStream).
 */
public class Cliente implements Serializable {

    /** Versao de serializacao: evita erros ao reler arquivos antigos. */
    private static final long serialVersionUID = 1L;

    // =================================================================
    // ATRIBUTOS PRIVADOS — base do ENCAPSULAMENTO
    // =================================================================
    // private = so metodos DESTA classe acessam estes campos.
    // Codigo externo e obrigado a usar getXxx() / setXxx().
    private String cpf;       // armazenado apenas com digitos (11 caracteres)
    private String nome;
    private String telefone;
    private String email;
    private int idade;

    /**
     * Construtor completo. Delega a validacao aos setters.
     * Se algum dado for invalido, a ClienteInvalidoException e lancada
     * ANTES de o objeto existir de forma inconsistente.
     */
    public Cliente(String cpf, String nome, String telefone, String email, int idade)
            throws ClienteInvalidoException {
        setCpf(cpf);
        setNome(nome);
        setTelefone(telefone);
        setEmail(email);
        setIdade(idade);
    }

    // =================================================================
    // GETTERS — leitura controlada (parte do ENCAPSULAMENTO)
    // =================================================================
    // O mundo externo pode LER os dados, mas nao consegue escrever
    // nos campos private sem passar pelos setters.

    /** @return CPF armazenado somente com digitos (11 caracteres) */
    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public int getIdade() {
        return idade;
    }

    // =================================================================
    // SETTERS COM VALIDACAO — alteracao controlada (ENCAPSULAMENTO)
    // =================================================================
    // Cada setter e a UNICA porta de entrada para modificar o atributo.
    // Antes de gravar, aplica regras de negocio. Se falhar, lanca
    // ClienteInvalidoException e o valor antigo permanece intacto.

    /**
     * Define o CPF (dado cadastral sensivel).
     * ENCAPSULAMENTO: limpa pontuacao e so aceita exatamente 11 digitos.
     * @throws ClienteInvalidoException se o CPF for vazio ou incompleto
     */
    public void setCpf(String cpf) throws ClienteInvalidoException {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new ClienteInvalidoException("CPF nao pode ser vazio.");
        }
        // Remove pontos, tracos e espacos, ficando so numeros
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            throw new ClienteInvalidoException("CPF deve conter 11 digitos numericos.");
        }
        this.cpf = cpfLimpo; // so chega aqui se passou na validacao
    }

    /**
     * Define o nome.
     * ENCAPSULAMENTO: rejeita nome vazio ou com menos de 3 caracteres.
     */
    public void setNome(String nome) throws ClienteInvalidoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ClienteInvalidoException("Nome nao pode ser vazio.");
        }
        if (nome.trim().length() < 3) {
            throw new ClienteInvalidoException("Nome deve ter pelo menos 3 caracteres.");
        }
        this.nome = nome.trim();
    }

    /**
     * Define o telefone.
     * ENCAPSULAMENTO: apenas verifica se nao esta vazio.
     */
    public void setTelefone(String telefone) throws ClienteInvalidoException {
        if (telefone == null || telefone.trim().isEmpty()) {
            throw new ClienteInvalidoException("Telefone nao pode ser vazio.");
        }
        this.telefone = telefone.trim();
    }

    /**
     * Define o e-mail.
     * ENCAPSULAMENTO: exige a presenca do caractere '@'.
     */
    public void setEmail(String email) throws ClienteInvalidoException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            throw new ClienteInvalidoException("E-mail invalido.");
        }
        this.email = email.trim().toLowerCase();
    }

    /**
     * Define a idade.
     * ENCAPSULAMENTO: regra de negocio — cliente deve ter no minimo 18 anos.
     */
    public void setIdade(int idade) throws ClienteInvalidoException {
        if (idade < 18) {
            throw new ClienteInvalidoException("Cliente deve ter no minimo 18 anos.");
        }
        if (idade > 120) {
            throw new ClienteInvalidoException("Idade invalida.");
        }
        this.idade = idade;
    }

    // ---------- equals / hashCode (baseados no CPF, chave unica) ----------

    /**
     * Dois clientes sao iguais se tiverem o mesmo CPF.
     * Necessario para buscas em List e para evitar cadastro duplicado.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    /** Representacao textual util para listagens no console. */
    @Override
    public String toString() {
        return String.format("Cliente{cpf='%s', nome='%s', telefone='%s', email='%s', idade=%d}",
                cpf, nome, telefone, email, idade);
    }
}
