# Consumidor de Relatórios por Email

Este script Python consome mensagens do RabbitMQ contendo relatórios de categorias de recomendações e envia emails formatados via Gmail.

## 📋 Pré-requisitos

1. **Python 3.7+**
2. **Conta Gmail** com App Password configurada
3. **RabbitMQ** rodando (via Docker Compose ou instalação local)

## 🔧 Configuração

### 1. Instalar Dependências

```bash
pip install -r requirements_email_consumer.txt
```

### 2. Configurar App Password do Gmail

Para enviar emails via Gmail, você precisa criar uma **App Password**:

1. Acesse: https://myaccount.google.com/apppasswords
2. Selecione "Email" e "Outro (Nome personalizado)"
3. Digite um nome (ex: "RabbitMQ Consumer")
4. Clique em "Gerar"
5. Copie a senha gerada (16 caracteres)

### 3. Configurar Variáveis de Ambiente

Crie um arquivo `.env` ou exporte as variáveis:

```bash
# RabbitMQ
export RABBITMQ_HOST=localhost
export RABBITMQ_PORT=5672
export RABBITMQ_USER=guest
export RABBITMQ_PASSWORD=guest

# Gmail
export GMAIL_USER=pedrogamerp@gmail.com
export GMAIL_PASSWORD=sua-app-password-aqui
```

**Windows PowerShell:**
```powershell
$env:RABBITMQ_HOST="localhost"
$env:RABBITMQ_PORT="5672"
$env:RABBITMQ_USER="guest"
$env:RABBITMQ_PASSWORD="guest"
$env:GMAIL_USER="pedrogamerp@gmail.com"
$env:GMAIL_PASSWORD="sua-app-password-aqui"
```

**Windows CMD:**
```cmd
set RABBITMQ_HOST=localhost
set RABBITMQ_PORT=5672
set RABBITMQ_USER=guest
set RABBITMQ_PASSWORD=guest
set GMAIL_USER=pedrogamerp@gmail.com
set GMAIL_PASSWORD=sua-app-password-aqui
```

## 🚀 Execução

### Executar o Script

```bash
python email_report_consumer.py
```

O script ficará aguardando mensagens na fila `email.reports.queue` e enviará emails automaticamente quando receber relatórios.

### Executar em Background (Linux/Mac)

```bash
nohup python email_report_consumer.py > email_consumer.log 2>&1 &
```

### Executar como Serviço (Windows)

Você pode usar o Task Scheduler do Windows ou criar um serviço usando NSSM (Non-Sucking Service Manager).

## 📧 Gerar Relatórios

### Via API REST

```bash
# Gerar relatório de uma categoria específica
curl -X POST "http://localhost:8080/api/reports/category/DevOps?email=pedrogamerp@gmail.com"

# Gerar relatórios de todas as categorias
curl -X POST "http://localhost:8080/api/reports/all-categories?email=pedrogamerp@gmail.com"
```

### Via Swagger

1. Acesse: http://localhost:8080/swagger-ui.html
2. Navegue até `/api/reports`
3. Use os endpoints disponíveis

## 📊 Formato do Email

O email enviado contém:

- **Estatísticas Gerais**: Total de recomendações, estudantes únicos, salvos, úteis
- **Top 10 Recomendações**: Cursos mais recomendados com taxa de utilidade
- **Design Responsivo**: HTML formatado e estilizado

## 🔍 Logs

O script gera logs detalhados de:
- Conexão com RabbitMQ
- Recebimento de mensagens
- Processamento de relatórios
- Envio de emails
- Erros e exceções

## 🐛 Troubleshooting

### Erro: "GMAIL_PASSWORD não configurada"
- Verifique se a variável de ambiente está configurada
- Certifique-se de usar a App Password, não a senha normal do Gmail

### Erro: "Connection refused" (RabbitMQ)
- Verifique se o RabbitMQ está rodando: `docker-compose ps`
- Verifique as credenciais de conexão

### Email não chega
- Verifique a pasta de Spam
- Verifique os logs do script para erros
- Confirme que a App Password está correta

### Mensagens não são processadas
- Verifique se a fila existe no RabbitMQ
- Acesse o Management UI: http://localhost:15672
- Verifique se há mensagens na fila

## 🔒 Segurança

⚠️ **IMPORTANTE**: Nunca commite a App Password do Gmail no código!

- Use variáveis de ambiente
- Adicione `.env` ao `.gitignore`
- Use um gerenciador de secrets em produção

## 📝 Exemplo de Uso Completo

```bash
# 1. Iniciar RabbitMQ (se não estiver rodando)
docker-compose up -d rabbitmq

# 2. Configurar variáveis de ambiente
export GMAIL_PASSWORD="sua-app-password"

# 3. Iniciar o consumidor
python email_report_consumer.py

# 4. Em outro terminal, gerar um relatório
curl -X POST "http://localhost:8080/api/reports/category/DevOps"

# 5. O email será enviado automaticamente!
```

## 🎯 Próximos Passos

- [ ] Adicionar suporte a múltiplos destinatários
- [ ] Implementar templates de email customizáveis
- [ ] Adicionar agendamento de relatórios periódicos
- [ ] Implementar retry automático em caso de falha
- [ ] Adicionar métricas e monitoramento

