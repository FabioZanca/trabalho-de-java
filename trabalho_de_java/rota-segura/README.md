# Sistema de Locação "Rota Segura" — TP1

**Nome completo:** [fabi henrique zanca pereira]  
**Matrícula:** [1301392611042]

---

## 1. Resumo da Arquitetura

O sistema é uma aplicação console orientada a objetos em Java que gerencia o ciclo de vida completo de locações de veículos (cadastro de clientes e frota, abertura de locação, devolução, cálculo de valores e emissão de comprovantes).

### Pacotes

| Pacote | Responsabilidade |
|--------|------------------|
| `com.rotasegura.model` | Entidades de domínio (Cliente, Veiculo e subclasses, Contrato, StatusVeiculo) e interface `Imprimivel` |
| `com.rotasegura.service` | Regras de negócio (`LocadoraService`) e persistência (`PersistenciaService`) |
| `com.rotasegura.exception` | Exceções de domínio (DataInvalida, VeiculoIndisponivel, ClienteInvalido, VeiculoInvalido) |
| `com.rotasegura.util` | Utilitário de entrada segura do console |
| `com.rotasegura.main` | Ponto de entrada e menu interativo |

### Diagrama simples de classes

```
                    <<interface>>
                     Imprimivel
                          ^
                          |
                      Contrato
                     /    |    \
               Cliente  Veiculo  (datas, valores)
                          ^
                          |
          +---------------+---------------+
          |               |               |
       Popular          Sedan            SUV
   (diária/seguro/    (diária/seguro/  (diária/seguro/
    manutenção)        manutenção)      manutenção)

LocadoraService  ----usa---->  List<Cliente>
                 ----usa---->  List<Veiculo>
                 ----usa---->  List<Contrato>
                 ----usa---->  PersistenciaService
```

### Conceitos de POO aplicados

| Conceito | Onde foi aplicado |
|----------|-------------------|
| **Encapsulamento** | Atributos `private` em `Cliente` e `Veiculo`; getters/setters com validação (CPF, idade, placa, ano, etc.) |
| **Herança / Abstração** | Classe abstrata `Veiculo` com especializações obrigatórias `Popular`, `Sedan` e `SUV` |
| **Polimorfismo** | `calcularDiaria()`, `calcularSeguro()`, `calcularManutencao()` sobrescritos em cada categoria; interface `Imprimivel` para contratos/relatórios |
| **Generics** | `List<Cliente>`, `List<Veiculo>`, `List<Contrato>` no serviço e na persistência |
| **Tratamento de exceções** | Validação rigorosa de datas, veículo ocupado/indisponível, dados inválidos de cliente/veículo |
| **Persistência** | Serialização binária Java (`*.dat`) + exportação de comprovantes em texto (`.txt`) na pasta `data/` |

---

## 2. Como compilar e executar

### Requisitos
- JDK 17 ou superior (testado com Java 17+)

### Compilação

A partir da pasta raiz do projeto (`rota-segura`):

```bash
# Criar diretório de classes
mkdir -p out

# Compilar todos os fontes
javac -d out -sourcepath src/main/java $(find src/main/java -name "*.java")
```

### Execução

```bash
# A pasta data/ será criada automaticamente para persistência
java -cp out com.rotasegura.main.Main
```

Os arquivos de dados ficam em `./data/` (relativo ao diretório de onde o comando é executado).

---

## 3. Cenários de teste realizados

### Fluxo completo (caminho feliz)
1. Cadastrar cliente (CPF, nome, telefone, e-mail, idade ≥ 18)
2. Cadastrar veículos das 3 categorias (Popular, Sedan, SUV)
3. Listar veículos disponíveis
4. Abrir locação (CPF + placa + datas válidas)
5. Listar contratos ativos
6. Devolver veículo (informar ID do contrato + data de devolução)
7. Imprimir/exportar comprovante
8. Gerar relatório de fechamento

### Resiliência (exceções tratadas)
| Cenário | Exceção esperada |
|---------|------------------|
| Tentar alugar veículo já ocupado | `VeiculoIndisponivelException` |
| Data de devolução ≤ data de início | `DataInvalidaException` |
| CPF com menos de 11 dígitos | `ClienteInvalidoException` |
| Cliente menor de 18 anos | `ClienteInvalidoException` |
| Placa duplicada | `VeiculoInvalidoException` |
| Ano de veículo inválido | `VeiculoInvalidoException` |
| Contrato inexistente na devolução | mensagem de erro tratada |
| Colocar em manutenção veículo ocupado | `VeiculoIndisponivelException` |

Em todos os casos o sistema **não encerra** e continua operando normalmente após exibir a mensagem de erro.

### Persistência
- Ao sair (opção 0) ou após cada operação crítica, os dados são gravados em `data/clientes.dat`, `data/veiculos.dat` e `data/contratos.dat`.
- Ao reiniciar a aplicação, o histórico é carregado automaticamente.
- Comprovantes de devolução são exportados como `data/comprovante_XXXXXXXX.txt`.

---

## 4. Estrutura do repositório

```
rota-segura/
├── README.md
├── data/                          # gerado em tempo de execução
│   ├── clientes.dat
│   ├── veiculos.dat
│   ├── contratos.dat
│   └── comprovante_*.txt
└── src/main/java/com/rotasegura/
    ├── exception/
    │   ├── ClienteInvalidoException.java
    │   ├── DataInvalidaException.java
    │   ├── VeiculoIndisponivelException.java
    │   └── VeiculoInvalidoException.java
    ├── model/
    │   ├── Cliente.java
    │   ├── Contrato.java
    │   ├── Imprimivel.java
    │   ├── Popular.java
    │   ├── Sedan.java
    │   ├── StatusVeiculo.java
    │   ├── SUV.java
    │   └── Veiculo.java
    ├── service/
    │   ├── LocadoraService.java
    │   └── PersistenciaService.java
    ├── util/
    │   └── EntradaUtil.java
    └── main/
        └── Main.java
```

---

## 5. Observações para a apresentação

- Demonstre o **fluxo completo** (cadastro → locação → devolução → comprovante).
- Force **dados inválidos** (veículo ocupado, datas inconsistentes) para mostrar o tratamento de exceções.
- No código, aponte:
  - atributos `private` + validação nos setters (**encapsulamento**);
  - classe abstrata `Veiculo` e subclasses (**herança/abstração**);
  - métodos `calcularDiaria/Seguro/Manutencao` e `Imprimivel.gerarDocumento()` (**polimorfismo**);
  - `List<Cliente>`, `List<Veiculo>`, `List<Contrato>` (**generics**);
  - blocos `try/catch` e classes de exceção personalizadas (**resiliência**).
