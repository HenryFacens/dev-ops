# Configuração do Sistema de Relatórios por Email

Este documento descreve como configurar e usar o sistema de relatórios de categorias que envia emails via Gmail.

## 🏗️ Arquitetura

```
Spring Boot App → RabbitMQ → Python Consumer → Gmail SMTP
```

1. **Spring Boot**: Gera relatórios e publica no RabbitMQ
2. **RabbitMQ**: Fila de mensagens (`email.reports.queue`)
3. **Python Consumer**: Consome mensagens e envia emails
4. **Gmail**: Envia os emails formatados

## 🚀 Configuração Rápida

### 1. Configurar App Password do Gmail

1. Acesse: https://myaccount.google.com/apppasswords
2. Gere uma App Password para "Email"
3. Copie a senha gerada (16 caracteres)

### 2. Configurar o arquivo `.env`

1. Copie o arquivo de exemplo e ajuste os valores:
   ```bash
   cp env.example .env
   ```
2. Edite a linha `GMAIL_PASSWORD` com sua App Password **sem espaços** (ex.: `abcdefghijklmnop`).
3. Ajuste qualquer outra variável conforme necessidade (hostnames, flags do simulador, etc).

Se preferir exportar manualmente ao invés de usar `.env`, basta definir `GMAIL_PASSWORD` (e os demais valores) no terminal antes de rodar o `docker compose up`.

### 3. Iniciar com Docker Compose

```bash
docker-compose up -d
```

O serviço `email-consumer` iniciará automaticamente e ficará aguardando mensagens.

### 4. Gerar um Relatório

```bash
# Relatório de uma categoria específica
curl -X POST "http://localhost:8080/api/reports/category/DevOps"

# Relatórios de todas as categorias
curl -X POST "http://localhost:8080/api/reports/all-categories"
```

## 📧 Endpoints Disponíveis

### POST `/api/reports/category/{category}`
Gera e envia relatório de uma categoria específica.

**Parâmetros:**
- `category` (path): Nome da categoria
- `email` (query, opcional): Email destinatário (padrão: pedrogamerp@gmail.com)

**Exemplo:**
```bash
curl -X POST "http://localhost:8080/api/reports/category/DevOps?email=pedrogamerp@gmail.com"
```

### POST `/api/reports/all-categories`
Gera e envia relatórios de todas as categorias.

**Parâmetros:**
- `email` (query, opcional): Email destinatário (padrão: pedrogamerp@gmail.com)

**Exemplo:**
```bash
curl -X POST "http://localhost:8080/api/reports/all-categories?email=pedrogamerp@gmail.com"
```

## 🔍 Verificar Status

### Ver logs do consumidor
```bash
docker-compose logs -f email-consumer
```

### Verificar fila no RabbitMQ
1. Acesse: http://localhost:15672
2. Login: `guest` / `guest`
3. Vá em "Queues" → `email.reports.queue`

### Verificar serviços
```bash
docker-compose ps
```

## 🐛 Troubleshooting

### Email não está sendo enviado

1. **Verificar logs:**
   ```bash
   docker-compose logs email-consumer
   ```

2. **Verificar variável de ambiente:**
   ```bash
   docker-compose exec email-consumer env | grep GMAIL
   ```

3. **Verificar conexão com RabbitMQ:**
   ```bash
   docker-compose exec email-consumer ping rabbitmq
   ```

### Erro: "GMAIL_PASSWORD não configurada"

Certifique-se de que a variável de ambiente está configurada antes de iniciar o docker-compose:

```bash
export GMAIL_PASSWORD="sua-senha"
docker-compose up -d email-consumer
```

### Mensagens ficam na fila mas não são processadas

1. Verifique se o consumidor está rodando:
   ```bash
   docker-compose ps email-consumer
   ```

2. Reinicie o consumidor:
   ```bash
   docker-compose restart email-consumer
   ```

### Relatório sem dados

- Quando não existem recomendações para a categoria solicitada, um email ainda é enviado com um aviso claro de que não há dados no momento.
- Se nenhum email chegar mesmo assim, verifique:
  - Logs do serviço `email-consumer`
  - Variável `GMAIL_PASSWORD`
  - Se a API realmente publicou uma mensagem (fila `email.reports.queue` no RabbitMQ)

## 📊 Estrutura do Relatório

O email contém:

- **Cabeçalho**: Categoria e data do relatório
- **Estatísticas**:
  - Total de recomendações
  - Estudantes únicos
  - Recomendações salvas (com percentual)
  - Recomendações úteis (com percentual)
- **Top 10 Cursos**: Lista dos cursos mais recomendados com taxa de utilidade
- **Design**: HTML responsivo e estilizado

## 🔒 Segurança

⚠️ **IMPORTANTE**: 

- Nunca commite a App Password no código
- Use variáveis de ambiente
- Em produção, use um gerenciador de secrets (ex: Docker Secrets, Kubernetes Secrets)

## 📝 Exemplo de Uso Completo

```bash
# 1. Configurar senha do Gmail
export GMAIL_PASSWORD="abcd efgh ijkl mnop"

# 2. Iniciar todos os serviços
docker-compose up -d

# 3. Aguardar serviços iniciarem (30 segundos)
sleep 30

# 4. Gerar relatório
curl -X POST "http://localhost:8080/api/reports/category/DevOps"

# 5. Verificar logs
docker-compose logs -f email-consumer

# 6. Verificar email na caixa de entrada!
```

## 🎯 Próximos Passos

- [ ] Adicionar suporte a templates de email customizáveis
- [ ] Implementar agendamento de relatórios periódicos
- [ ] Adicionar suporte a múltiplos destinatários
- [ ] Implementar retry automático com backoff exponencial
- [ ] Adicionar métricas e monitoramento (Prometheus/Grafana)

