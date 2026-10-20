# Fluxo de trabalho Kanban

Este projeto pode ser organizado no GitHub Projects com as colunas **Backlog**, **A fazer**, **Em andamento**, **Revisão** e **Concluído**. Limite o trabalho em andamento a uma ou duas tarefas por pessoa; mova um item para revisão somente quando a implementação, os testes e a documentação estiverem prontos.

## Exemplo de história

**Como** analista de suporte, **quero** registrar um chamado com categoria, impacto e urgência, **para** que o sistema calcule a prioridade e o prazo inicial de atendimento.

Critérios de aceite:

- O título, a descrição, o e-mail e a classificação são validados.
- Chamados da categoria Segurança recebem prioridade crítica.
- Prioridade crítica define SLA inicial de quatro horas.
- A resposta contém identificador, status, prioridade e vencimento.
- A regra possui teste automatizado.

## Definição de concluído

- Critérios de aceite atendidos.
- Testes relevantes passando localmente e no CI.
- Revisão de código concluída.
- README ou contrato da API atualizado quando necessário.
- Pull request vinculado à issue.
