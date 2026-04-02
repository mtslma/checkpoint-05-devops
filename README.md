# Checkpoint 05 - DevOps Tools & Cloud Computing

Esse projeto apresenta uma API Java Spring Boot para gestão de pedidos de esfihas, integrada ao Azure SQL Database e monitorada pelo Application Insights. O deploy foi realizado via Azure CLI.

---

## Menu de Navegação
1. [Configuração do Banco de Dados (Azure SQL)](#configuração-do-banco-de-dados-azure-sql)
2. [Scripts de Estrutura (DDL)](#scripts-de-estrutura-ddl)
3. [Configuração do Web App (API Java)](#configuração-do-web-app-api-java)
4. [Deploy da Aplicação](#deploy-da-aplicação)
5. [Documentação da API (Endpoints)](#documentação-da-api-endpoints)
6. [Vídeo Demonstrativo](#vídeo-demonstrativo)

---

## Configuração do Banco de Dados (Azure SQL)

### Executar no Azure Cloud Shell

1. Criar Grupo de Recursos
```bash
az group create --name rg-esfiha-rm559728 --location southafricanorth
```

2. Criar o Servidor SQL
```bash
az sql server create \
--name sql-server-esfiha-rm559728-sa \
--resource-group rg-esfiha-rm559728 \
--location southafricanorth \
--admin-user user-esfiha \
--admin-password 'Fiap@2tdsvms' \
--enable-public-network true
```

3. Criar o Banco de Dados (PaaS)
```bash
az sql db create \
--resource-group rg-esfiha-rm559728 \
--server sql-server-esfiha-rm559728-sa \
--name db-esfiha \
--service-objective Basic \
--backup-storage-redundancy Local \
--zone-redundant false
```

4. Liberar Regras de Firewall
```bash
az sql server firewall-rule create \
--resource-group rg-esfiha-rm559728 \
--server sql-server-esfiha-rm559728-sa \
--name liberaGeral \
--start-ip-address 0.0.0.0 \
--end-ip-address 255.255.255.255
```

---

## Scripts de Estrutura (DDL)

### Executar no Azure PowerShell 

5. Criação das Tabelas
```bash
Invoke-Sqlcmd -ServerInstance "sql-server-esfiha-rm559728-sa.database.windows.net" `
-Database "db-esfiha" `
-Username "user-esfiha" `
-Password "Fiap@2tdsvms" `
-Query @"
-- Tabela de Pedidos
CREATE TABLE pedidos (
    id_pedido INT IDENTITY(1,1) PRIMARY KEY,
    cliente VARCHAR(100) NOT NULL,
    data_pedido DATETIME DEFAULT GETDATE(),
    valor_total DECIMAL(10,2)
);

-- Tabela de Itens (Com Relacionamento)
CREATE TABLE itens_pedido (
    id_item INT IDENTITY(1,1) PRIMARY KEY,
    id_pedido INT NOT NULL,
    sabor VARCHAR(50) NOT NULL,
    quantidade INT NOT NULL,
    CONSTRAINT FK_Pedido FOREIGN KEY (id_pedido) REFERENCES pedidos(id_pedido)
);
"@
```

---

## Configuração do Web App (API Java)

### Executar no Azure Cloud Shell

1. Criar o Plano de Serviço
```bash
az appservice plan create --name plan-esfiha-rm559728 --resource-group rg-esfiha-rm559728 --location southafricanorth --sku F1 --is-linux
```

2. Criar o Web App
```bash
az webapp create \
--name web-esfiha-rm559728 \
--resource-group rg-esfiha-rm559728 \
--plan plan-esfiha-rm559728 \
--runtime "JAVA|21-java21"
```

3. Configurar Variáveis de Ambiente 
```bash
az webapp config appsettings set --name web-esfiha-rm559728 --resource-group rg-esfiha-rm559728 --settings \
SPRING_DATASOURCE_URL="jdbc:sqlserver://sql-server-esfiha-rm559728-sa.database.windows.net:1433;database=db-esfiha;encrypt=true;trustServerCertificate=false;" \
SPRING_DATASOURCE_USERNAME="user-esfiha" \
SPRING_DATASOURCE_PASSWORD="Fiap@2tdsvms"
```

---

## Deploy da Aplicação

### Executar na sua Máquina Local (Raiz do Projeto)

1. Gerar o artefato `.jar`
```bash
.\mvnw clean package -DskipTests
```

### Executar no Azure Cloud Shell

2. Fazer upload do arquivo `.jar`para o Azure

<p align="center">
  <img src="https://i.imgur.com/grcl49v.png" width="32%" />
  <img src="https://i.imgur.com/7VUStnX.png" width="32%" />
  <img src="https://i.imgur.com/glQl0Zc.png" width="32%" />
</p>

3. Iniciar o Deploy do arquivo `.jar`
```bash
az webapp deploy \
--resource-group rg-esfiha-rm559728 \
--name web-esfiha-rm559728 \
--src-path checkpoint05devops-0.0.1-SNAPSHOT.jar \
--type jar
```

---

## Documentação da API (Endpoints)

A URL base para todas as requisições é: `https://web-esfiha-rm559728.azurewebsites.net`

| Método | Endpoint | Descrição |
|:--- |:--- |:--- |
| **GET** | `/esfihas` | Lista todos os pedidos realizados. |
| **POST** | `/esfihas` | Registra um novo pedido e os seus itens. |
| **PUT** | `/esfihas/{id}` | Atualiza os dados de um pedido existente. |
| **DELETE** | `/esfihas/{id}` | Remove um pedido permanentemente. |

### Exemplos de Requisição

**POST /esfihas**
```json
{
    "cliente": "Mateus RM559728",
    "valorTotal": 55.50,
    "itens": [
        { "sabor": "Esfiha de Carne", "quantidade": 5 },
        { "sabor": "Esfiha de Queijo", "quantidade": 3 },
        { "sabor": "Esfiha de Frango com Catupiry", "quantidade": 2 }
    ]
}
```

**PUT /esfihas/1**
```json
{
    "cliente": "Mateus RM559728 - ATUALIZADO",
    "valorTotal": 75.00,
    "itens": [
        { "sabor": "Esfiha de Chocolate", "quantidade": 2 },
        { "sabor": "Esfiha de Banana", "quantidade": 1 }
    ]
}
```
---

## Vídeo Demonstrativo

Demonstração das funcionalidades e persistência dos dados está disponível no link abaixo:

- https://youtu.be/Z5BHYP3x1Rw

---

## Integrantes do Grupo

- RM561061 - Arthur Thomas Mariano de Souza
- RM559873 - Davi Cavalcanti Jorge 
- RM559728 - Mateus da Silveira Lima
