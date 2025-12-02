#!/usr/bin/env python3
"""
Script para consumir relatórios de categoria do RabbitMQ e enviar via Gmail
"""

import pika
import json
import smtplib
import os
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from datetime import datetime
from typing import Dict, Any
import logging

# Configuração de logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

# Configurações
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = int(os.getenv('RABBITMQ_PORT', '5672'))
RABBITMQ_USER = os.getenv('RABBITMQ_USER', 'guest')
RABBITMQ_PASSWORD = os.getenv('RABBITMQ_PASSWORD', 'guest')
RABBITMQ_QUEUE = 'email.reports.queue'

GMAIL_USER = os.getenv('GMAIL_USER', 'pedroandrade202004@gmail.com')
GMAIL_PASSWORD = os.getenv('GMAIL_PASSWORD', 'ayvfzfbqxappymrs')
SMTP_SERVER = 'smtp.gmail.com'
SMTP_PORT = 587


def create_email_html(report_data: Dict[str, Any]) -> str:
    """Cria o HTML do email com base nos dados do relatório"""
    
    category = report_data.get('category', 'N/A')
    total_recommendations = report_data.get('totalRecommendations', 0)
    total_students = report_data.get('totalStudents', 0)
    saved_count = report_data.get('savedCount', 0)
    useful_count = report_data.get('usefulCount', 0)
    top_recommendations = report_data.get('topRecommendations', [])
    report_date = report_data.get('reportDate', datetime.now().isoformat())
    has_recommendations = report_data.get('hasRecommendations', True)
    notice_message = report_data.get('noticeMessage')
    
    # Calcular estatísticas
    saved_percentage = (saved_count / total_recommendations * 100) if total_recommendations > 0 else 0
    useful_percentage = (useful_count / total_recommendations * 100) if total_recommendations > 0 else 0
    
    notice_html = f'<div class="notice">⚠️ {notice_message}</div>' if notice_message else ""

    html = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <meta charset="UTF-8">
        <style>
            body {{
                font-family: Arial, sans-serif;
                line-height: 1.6;
                color: #333;
                max-width: 800px;
                margin: 0 auto;
                padding: 20px;
                background-color: #f4f4f4;
            }}
            .container {{
                background-color: white;
                padding: 30px;
                border-radius: 10px;
                box-shadow: 0 0 10px rgba(0,0,0,0.1);
            }}
            h1 {{
                color: #2c3e50;
                border-bottom: 3px solid #3498db;
                padding-bottom: 10px;
            }}
            h2 {{
                color: #34495e;
                margin-top: 30px;
            }}
            .stats-grid {{
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 20px;
                margin: 20px 0;
            }}
            .stat-card {{
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 20px;
                border-radius: 8px;
                text-align: center;
            }}
            .stat-card h3 {{
                margin: 0;
                font-size: 2em;
            }}
            .stat-card p {{
                margin: 5px 0 0 0;
                opacity: 0.9;
            }}
            .recommendations-table {{
                width: 100%;
                border-collapse: collapse;
                margin: 20px 0;
            }}
            .recommendations-table th {{
                background-color: #3498db;
                color: white;
                padding: 12px;
                text-align: left;
            }}
            .recommendations-table td {{
                padding: 10px;
                border-bottom: 1px solid #ddd;
            }}
            .recommendations-table tr:hover {{
                background-color: #f5f5f5;
            }}
            .badge {{
                display: inline-block;
                padding: 5px 10px;
                border-radius: 15px;
                font-size: 0.85em;
                font-weight: bold;
            }}
            .badge-success {{
                background-color: #27ae60;
                color: white;
            }}
            .badge-info {{
                background-color: #3498db;
                color: white;
            }}
            .notice {{
                margin: 15px 0;
                padding: 15px;
                border-left: 4px solid #f39c12;
                background-color: #fff8e6;
                color: #a66a00;
                border-radius: 6px;
            }}
            .footer {{
                margin-top: 30px;
                padding-top: 20px;
                border-top: 2px solid #ecf0f1;
                text-align: center;
                color: #7f8c8d;
                font-size: 0.9em;
            }}
        </style>
    </head>
    <body>
        <div class="container">
            <h1>📊 Relatório de Categoria: {category}</h1>
            <p><strong>Data do Relatório:</strong> {report_date}</p>
            {notice_html}
            
            <div class="stats-grid">
                <div class="stat-card">
                    <h3>{total_recommendations}</h3>
                    <p>Total de Recomendações</p>
                </div>
                <div class="stat-card">
                    <h3>{total_students}</h3>
                    <p>Estudantes Únicos</p>
                </div>
                <div class="stat-card">
                    <h3>{saved_count}</h3>
                    <p>Salvas ({saved_percentage:.1f}%)</p>
                </div>
                <div class="stat-card">
                    <h3>{useful_count}</h3>
                    <p>Úteis ({useful_percentage:.1f}%)</p>
                </div>
            </div>
            
            <h2>🏆 Top Recomendações</h2>
            <table class="recommendations-table">
                <thead>
                    <tr>
                        <th>Curso</th>
                        <th>Quantidade</th>
                        <th>Taxa de Utilidade</th>
                    </tr>
                </thead>
                <tbody>
    """
    
    if top_recommendations:
        for rec in top_recommendations[:10]:
            course_name = rec.get('courseName', 'N/A')
            count = rec.get('count', 0)
            useful_pct = rec.get('usefulPercentage', 0.0)
            
            badge_class = 'badge-success' if useful_pct >= 50 else 'badge-info'
            
            html += f"""
                        <tr>
                            <td><strong>{course_name}</strong></td>
                            <td>{count}</td>
                            <td><span class="badge {badge_class}">{useful_pct:.1f}%</span></td>
                        </tr>
            """
    else:
        html += """
                    <tr>
                        <td colspan="3" style="text-align:center; color:#7f8c8d;">
                            Nenhuma recomendação disponível para esta categoria.
                        </td>
                    </tr>
        """
    
    html += """
                </tbody>
            </table>
            
            <div class="footer">
                <p>Este é um relatório automático gerado pelo sistema de recomendações.</p>
                <p>© 2024 Sistema de Recomendações - DevOps</p>
            </div>
        </div>
    </body>
    </html>
    """
    
    return html


def send_email(report_data: Dict[str, Any]) -> bool:
    """Envia o email com o relatório"""
    
    recipient_email = report_data.get('recipientEmail', GMAIL_USER)
    category = report_data.get('category', 'N/A')
    
    if not GMAIL_PASSWORD:
        logger.error("GMAIL_PASSWORD não configurada! Configure a variável de ambiente.")
        return False
    
    try:
        # Criar mensagem
        msg = MIMEMultipart('alternative')
        msg['Subject'] = f'Relatório de Categoria: {category}'
        msg['From'] = GMAIL_USER
        msg['To'] = recipient_email
        
        # Criar conteúdo HTML
        html_content = create_email_html(report_data)
        html_part = MIMEText(html_content, 'html', 'utf-8')
        msg.attach(html_part)
        
        # Enviar email
        logger.info(f"Conectando ao servidor SMTP: {SMTP_SERVER}:{SMTP_PORT}")
        server = smtplib.SMTP(SMTP_SERVER, SMTP_PORT)
        server.starttls()
        server.login(GMAIL_USER, GMAIL_PASSWORD)
        
        logger.info(f"Enviando email para: {recipient_email}")
        text = msg.as_string()
        server.sendmail(GMAIL_USER, recipient_email, text)
        server.quit()
        
        logger.info(f"Email enviado com sucesso para {recipient_email}!")
        return True
        
    except Exception as e:
        logger.error(f"Erro ao enviar email: {str(e)}")
        return False


def process_message(ch, method, properties, body):
    """Processa mensagem recebida do RabbitMQ"""
    
    try:
        logger.info("Nova mensagem recebida do RabbitMQ")
        
        # Decodificar JSON
        report_data = json.loads(body.decode('utf-8'))
        logger.info(f"Dados do relatório: {json.dumps(report_data, indent=2, ensure_ascii=False)}")
        
        # Enviar email
        success = send_email(report_data)
        
        if success:
            # Confirmar processamento
            ch.basic_ack(delivery_tag=method.delivery_tag)
            logger.info("Mensagem processada e confirmada com sucesso!")
        else:
            # Rejeitar e reenfileirar
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)
            logger.warning("Falha ao processar mensagem. Mensagem será reenfileirada.")
            
    except json.JSONDecodeError as e:
        logger.error(f"Erro ao decodificar JSON: {str(e)}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    except Exception as e:
        logger.error(f"Erro ao processar mensagem: {str(e)}")
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)


def main():
    """Função principal"""
    
    logger.info("=" * 60)
    logger.info("Iniciando consumidor de relatórios RabbitMQ")
    logger.info("=" * 60)
    logger.info(f"RabbitMQ: {RABBITMQ_HOST}:{RABBITMQ_PORT}")
    logger.info(f"Fila: {RABBITMQ_QUEUE}")
    logger.info(f"Gmail: {GMAIL_USER}")
    logger.info("=" * 60)
    
    if not GMAIL_PASSWORD:
        logger.error("⚠️  ATENÇÃO: GMAIL_PASSWORD não configurada!")
        logger.error("Configure a variável de ambiente GMAIL_PASSWORD com sua App Password do Gmail")
        logger.error("Para criar uma App Password:")
        logger.error("1. Acesse: https://myaccount.google.com/apppasswords")
        logger.error("2. Gere uma senha para 'Email'")
        logger.error("3. Use essa senha como GMAIL_PASSWORD")
        return
    
    try:
        # Conectar ao RabbitMQ
        credentials = pika.PlainCredentials(RABBITMQ_USER, RABBITMQ_PASSWORD)
        parameters = pika.ConnectionParameters(
            host=RABBITMQ_HOST,
            port=RABBITMQ_PORT,
            credentials=credentials
        )
        
        connection = pika.BlockingConnection(parameters)
        channel = connection.channel()
        
        # Declarar fila (caso não exista)
        channel.queue_declare(queue=RABBITMQ_QUEUE, durable=True)
        
        # Configurar QoS para processar uma mensagem por vez
        channel.basic_qos(prefetch_count=1)
        
        # Configurar consumidor
        logger.info(f"Aguardando mensagens na fila '{RABBITMQ_QUEUE}'. Para sair pressione CTRL+C")
        channel.basic_consume(
            queue=RABBITMQ_QUEUE,
            on_message_callback=process_message
        )
        
        # Iniciar consumo
        channel.start_consuming()
        
    except KeyboardInterrupt:
        logger.info("\nInterrompido pelo usuário. Encerrando...")
        if 'channel' in locals():
            channel.stop_consuming()
        if 'connection' in locals():
            connection.close()
    except Exception as e:
        logger.error(f"Erro fatal: {str(e)}")
        raise


if __name__ == '__main__':
    main()

