Feature: Recomendação de cursos
  Como aluno EAD,
  Quero receber recomendações e interagir com elas,
  Para continuar minha trilha de aprendizagem.

  Background:
    Given que existe base de recomendações para os alunos

  @happy_path
  Scenario: Exibir recomendações ao concluir curso
    When eu solicito recomendações para o aluno "1"
    Then a lista de recomendações não deve ser vazia

  @email
  Scenario: Enviar e-mail de recomendações para aluno com e-mail válido
    When eu envio recomendações por e-mail "aluno@teste.com" para o aluno "2"
    Then o envio deve retornar sucesso

  @filter
  Scenario: Filtrar recomendações pela categoria
    When eu filtro recomendações do aluno "3" pela categoria "Tecnologia"
    Then a lista filtrada não deve ser vazia
    And todos os itens devem conter "Tecnologia"

  @save
  Scenario: Salvar curso para ver depois
    When eu salvo o curso "Curso de DevOps" para o aluno "4"
    Then o retorno de salvar deve ser sucesso

  @useful
  Scenario: Marcar recomendação como útil
    When eu marco o curso "Curso de Testes" como útil para o aluno "5"
    Then o retorno de útil deve ser sucesso

  @negativos
  Scenario Outline: Não enviar e-mail em condições inválidas
    When eu tento enviar recomendações por e-mail "<email>" para o aluno "<alunoId>"
    Then o envio deve falhar
    Examples:
      | email        | alunoId |
      |              | 2       |
      | aluno@       | 2       |
      | ok@teste.com | 999     |
