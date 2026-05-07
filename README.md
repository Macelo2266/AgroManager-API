# 🌱 AgroManager API

Sistema de Gestão Agrícola para Pequenos Produtores Rurais — API REST desenvolvida com Java 21 e Spring Boot 3.

---

## 📋 Sobre o Projeto

O **AgroManager API** é um sistema backend robusto para gerenciar todas as operações de uma propriedade rural, incluindo produção agrícola, controle de estoque, gestão financeira e manutenção de máquinas.

---

## 🚀 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 21 | Linguagem principal |
| Spring Boot | 3.3.2 | Framework principal |
| Spring Security | 6.3.1 | Autenticação e autorização |
| Spring Data JPA | 3.3.2 | Persistência de dados |
| PostgreSQL | 17 | Banco de dados |
| Flyway | 10.10.0 | Versionamento do banco |
| JWT (JJWT) | 0.12.3 | Tokens de autenticação |
| Lombok | 1.18.34 | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |
| MapStruct | 1.5.5 | Mapeamento de DTOs |

---

## 🧩 Módulos

- **Autenticação** — Login com JWT, controle de acesso por perfil (ADMIN, FUNCIONARIO)
- **Propriedades** — Cadastro de propriedades rurais e talhões
- **Produção Agrícola** — Registro de plantios, colheitas e cálculo de produtividade
- **Estoque** — Controle de insumos e colheitas com custo médio ponderado
- **Financeiro** — Receitas, despesas e fluxo de caixa
- **Máquinas** — Cadastro e controle de manutenções
- **Relatórios** — Produção por período, lucro por cultura, indicadores gerais
- **Alertas** — Estoque baixo, manutenção pendente, gastos elevados

---

## 🗂️ Estrutura do Projeto

```
src/
└── main/
    └── java/com/maceloaraujo/AgroManager/API/
        ├── config/          # Segurança, JWT, Swagger
        ├── controller/      # Endpoints REST
        ├── dto/
        │   ├── request/     # DTOs de entrada
        │   └── response/    # DTOs de saída
        ├── exception/       # Tratamento global de erros
        ├── model/
        │   ├── entity/      # Entidades JPA
        │   └── enums/       # Enumerações
        ├── repository/      # Interfaces Spring Data JPA
        └── service/         # Regras de negócio
```

---

## ⚙️ Pré-requisitos

- Java 21+
- Maven 3.8+
- PostgreSQL 15+

---

## 🔧 Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/SEU_USUARIO/AgroManager-API.git
cd AgroManager-API
```

### 2. Crie o banco de dados

```sql
CREATE DATABASE "AgroManager-API";
```

### 3. Configure as variáveis de ambiente

No IntelliJ: **Run → Edit Configurations → Environment Variables**

```
DB_HOST=localhost:5432
DB_USER=postgres
MINHA_SENHA=sua_senha_aqui
JWT_SECRET=agromanager-chave-secreta-super-longa-2026
```

### 4. Execute o projeto

```bash
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

---

## 📚 Documentação da API

Acesse o Swagger UI após iniciar a aplicação:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔐 Autenticação

Todas as rotas (exceto `/api/auth/login`) exigem token JWT no header:

```
Authorization: Bearer <token>
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@agro.com",
  "senha": "admin123"
}
```

---

## 📡 Principais Endpoints

### Propriedades
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/propriedades` | Lista todas as propriedades |
| POST | `/api/propriedades` | Cria nova propriedade |
| PUT | `/api/propriedades/{id}` | Atualiza propriedade |
| DELETE | `/api/propriedades/{id}` | Desativa propriedade |

### Talhões
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/talhoes?propriedadeId=1` | Lista talhões |
| POST | `/api/talhoes` | Cria novo talhão |

### Culturas
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/culturas` | Lista culturas |
| POST | `/api/culturas` | Cria nova cultura |

### Produção
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/producao/plantios?propriedadeId=1` | Lista plantios |
| POST | `/api/producao/plantios` | Registra plantio |
| POST | `/api/producao/colheitas` | Registra colheita |
| GET | `/api/producao/relatorio` | Relatório de produção |

### Estoque
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/produtos?propriedadeId=1` | Lista produtos |
| POST | `/api/produtos` | Cria produto |
| POST | `/api/estoque/movimentacoes` | Entrada ou saída |
| GET | `/api/produtos/estoque-baixo?propriedadeId=1` | Produtos em alerta |

### Financeiro
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/vendas` | Registra venda |
| POST | `/api/financeiro/despesas` | Registra despesa |
| POST | `/api/financeiro/fluxo-caixa` | Calcula fluxo de caixa |
| GET | `/api/financeiro/despesas-por-categoria` | Despesas por categoria |

### Máquinas
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/maquinas?propriedadeId=1` | Lista máquinas |
| POST | `/api/maquinas` | Cadastra máquina |
| POST | `/api/maquinas/manutencoes` | Registra manutenção |
| PATCH | `/api/maquinas/{id}/horas-uso` | Adiciona horas de uso |

### Alertas e Relatórios
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/alertas/{propriedadeId}` | Busca todos os alertas |
| GET | `/api/relatorios/indicadores` | Indicadores gerais |

---

## 📦 Exemplos de Requisição

### Criar Propriedade
```json
POST /api/propriedades
{
  "nome": "Fazenda São João",
  "areaTotalHectares": 500.00,
  "municipio": "Campina Grande",
  "estado": "PB"
}
```

### Registrar Plantio
```json
POST /api/producao/plantios
{
  "talhaoId": 1,
  "culturaId": 1,
  "dataPlantio": "2026-01-10",
  "dataPrevistaColheita": "2026-05-10",
  "areaPlantadaHectares": 80.00,
  "quantidadeSementeKg": 240.000
}
```

### Movimentar Estoque
```json
POST /api/estoque/movimentacoes
{
  "produtoId": 1,
  "tipo": "ENTRADA",
  "quantidade": 500.000,
  "custoUnitario": 3.50,
  "dataMovimentacao": "2026-05-07",
  "motivo": "Compra de insumos"
}
```

### Fluxo de Caixa
```json
POST /api/financeiro/fluxo-caixa
{
  "propriedadeId": 1,
  "dataInicio": "2026-01-01",
  "dataFim": "2026-12-31"
}
```

---

## 🗄️ Modelagem do Banco

```
propriedades
    └── talhoes
    └── usuarios
    └── produtos → estoques → movimentacoes_estoque
    └── maquinas → manutencoes
    └── vendas
    └── despesas

talhoes → plantios → colheitas
culturas → plantios
```

---

## 🔒 Perfis de Acesso

| Perfil | Permissões |
|---|---|
| `ADMIN` | Acesso total — cadastros, relatórios, financeiro |
| `FUNCIONARIO` | Registrar plantios, colheitas e movimentações de estoque |

---

## 🤝 Como Contribuir

```bash
# 1. Fork o projeto
# 2. Crie sua branch
git checkout -b feature/minha-feature

# 3. Commit suas mudanças
git commit -m "feat: adiciona nova funcionalidade"

# 4. Push para a branch
git push origin feature/minha-feature

# 5. Abra um Pull Request
```

---

## 👨‍💻 Autor

Desenvolvido por **Macelo Araujo**

---

## 📄 Licença

Este projeto está sob a licença MIT.
