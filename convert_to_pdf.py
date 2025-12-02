import markdown
from xhtml2pdf import pisa
from pathlib import Path
import io
import urllib.parse
import re
import base64

# Read markdown file
md_file = Path('docs/DOCUMENTATION.md').absolute()
docs_dir = md_file.parent

with open(md_file, 'r', encoding='utf-8') as f:
    md_content = f.read()

# Convert markdown to HTML
md = markdown.Markdown(extensions=['extra', 'codehilite', 'tables', 'fenced_code'])
html_content = md.convert(md_content)

# Fix image paths - convert to base64 data URIs
def fix_image_paths(html):
    def replace_src(match):
        full_img_tag = match.group(0)
        src_match = re.search(r'src="([^"]+)"', full_img_tag)
        if src_match:
            img_path = src_match.group(1)
            # Skip if already data URI or http
            if img_path.startswith('http') or img_path.startswith('data:'):
                return full_img_tag
            # Decode URL encoding
            decoded_path = urllib.parse.unquote(img_path)
            # Build absolute path
            abs_path = (docs_dir / decoded_path).absolute()
            if abs_path.exists():
                try:
                    # Read image and convert to base64
                    with open(abs_path, 'rb') as img_file:
                        img_data = img_file.read()
                        img_base64 = base64.b64encode(img_data).decode('utf-8')
                        
                        # Determine MIME type from extension
                        ext = abs_path.suffix.lower()
                        mime_types = {
                            '.png': 'image/png',
                            '.jpg': 'image/jpeg',
                            '.jpeg': 'image/jpeg',
                            '.gif': 'image/gif',
                            '.svg': 'image/svg+xml'
                        }
                        mime_type = mime_types.get(ext, 'image/png')
                        
                        # Create data URI
                        data_uri = f"data:{mime_type};base64,{img_base64}"
                        return full_img_tag.replace(f'src="{img_path}"', f'src="{data_uri}"')
                except Exception as e:
                    print(f"Erro ao processar imagem {abs_path}: {e}")
                    return full_img_tag
        return full_img_tag
    
    # Replace all img tags with base64 data URIs
    return re.sub(r'<img[^>]+src="[^"]+"[^>]*>', replace_src, html)

print("Processando imagens...")
html_content = fix_image_paths(html_content)

# Add CSS styling
html_doc = f"""
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Documentação - API de Recomendações</title>
    <style>
        @page {{
            margin: 2cm;
        }}
        body {{
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            font-size: 11pt;
        }}
        h1 {{
            color: #2c3e50;
            border-bottom: 3px solid #3498db;
            padding-bottom: 10px;
            margin-top: 1em;
        }}
        h2 {{
            color: #34495e;
            border-bottom: 2px solid #95a5a6;
            padding-bottom: 5px;
            margin-top: 1.5em;
        }}
        h3 {{
            color: #555;
            margin-top: 1.2em;
        }}
        code {{
            background-color: #f4f4f4;
            padding: 2px 6px;
            border-radius: 3px;
            font-family: 'Courier New', monospace;
        }}
        pre {{
            background-color: #f4f4f4;
            padding: 15px;
            border-radius: 5px;
            overflow-x: auto;
            border-left: 4px solid #3498db;
        }}
        img {{
            max-width: 100%;
            height: auto;
            margin: 10px 0;
            display: block;
        }}
        table {{
            border-collapse: collapse;
            width: 100%;
            margin: 15px 0;
        }}
        th, td {{
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }}
        th {{
            background-color: #3498db;
            color: white;
        }}
        ul, ol {{
            margin: 10px 0;
            padding-left: 30px;
        }}
        blockquote {{
            border-left: 4px solid #3498db;
            padding-left: 15px;
            margin: 15px 0;
            color: #666;
        }}
        hr {{
            border: none;
            border-top: 2px solid #ecf0f1;
            margin: 30px 0;
        }}
        a {{
            color: #3498db;
            text-decoration: none;
        }}
    </style>
</head>
<body>
{html_content}
</body>
</html>
"""

print("Gerando PDF...")
# Convert HTML to PDF
output_file = Path('docs/DOCUMENTATION.pdf')
result_file = open(output_file, "w+b")
pisa_status = pisa.CreatePDF(
    src=io.StringIO(html_doc),
    dest=result_file,
    encoding='utf-8'
)
result_file.close()

if pisa_status.err:
    print(f"Erro ao gerar PDF: {pisa_status.err}")
else:
    print(f"PDF gerado com sucesso: {output_file}")
    print(f"Tamanho do arquivo: {output_file.stat().st_size / 1024:.2f} KB")
    
    # Also save HTML for reference
    html_output = Path('docs/DOCUMENTATION.html')
    with open(html_output, 'w', encoding='utf-8') as f:
        f.write(html_doc)
    print(f"HTML também salvo em: {html_output}")
