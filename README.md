# Gestão de Chamados de TI

Projeto demonstrativo de portfólio para praticar e apresentar desenvolvimento com Java, APIs REST, microsserviços, banco relacional e regras de negócio. O cenário é familiar à rotina de suporte de TI: registrar chamados, calcular prioridade e SLA, acompanhar o atendimento e notificar mudanças de status.

> Aplicação educacional. Não possui autenticação nem foi preparada para receber dados reais ou ser publicada diretamente na internet.

## Arquitetura

```text
Cliente
  |
  v
Gateway Nginx (:8080)
  |                         |
  v                         v
Ticket Service (:8081) ---> Notification Service (:8082)
  |
  v
PostgreSQL (:5432)
```

- **ticket-service**: API REST de chamados, persistência com Spring Data JPA e PostgreSQL, validações e regras do ciclo de vida.
- **notification-service**: recebe eventos de criação e mudança de status e mantém as notificações recentes em memória (até 500 por execução).
- **gateway**: encaminha as rotas públicas para os serviços internos.
- **PostgreSQL**: guarda chamados de forma relacional.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring MVC, Spring Data JPA, Bean Validation e Actuator
- PostgreSQL 17
- Maven e Docker Compose
- JUnit 5
- GitHub Actions para build e testes

## Regras de negócio

### Prioridade e SLA inicial

| Condição | Prioridade | Prazo de SLA |
|---|---:|---:|
| Categoria Segurança | Crítica | 4 horas |
| Impacto alto e urgência alta | Crítica | 4 horas |
| Impacto alto ou urgência alta | Alta | 8 horas |
| Impacto e urgência baixos | Baixa | 48 horas |
| Demais combinações | Média | 24 horas |

O prazo é calculado a partir da abertura do chamado. Esta versão não pausa ou recalcula o SLA quando o chamado aguarda o usuário.

### Transições de status

| Status atual | Próximos status permitidos |
|---|---|
| OPEN | IN_PROGRESS, CANCELLED |
| IN_PROGRESS | WAITING_USER, RESOLVED, CANCELLED |
| WAITING_USER | IN_PROGRESS, CANCELLED |
| RESOLVED | CLOSED, IN_PROGRESS |
| CLOSED | Nenhum (terminal) |
| CANCELLED | Nenhum (terminal) |

Tentativas de transição inválida retornam HTTP 409. Um chamado só pode ser encerrado após ser resolvido; um chamado resolvido pode voltar para atendimento.

## Executar localmente

Pré-requisitos: Docker com Docker Compose. O primeiro build precisa baixar dependências Maven e imagens.

~~~bash
docker compose up --build
~~~

O gateway fica disponível em **http://localhost:8080**. O PostgreSQL persiste os chamados no volume `postgres-data`. Para parar os containers:

~~~bash
docker compose down
~~~

Para apagar também os dados persistidos de desenvolvimento:

~~~bash
docker compose down -v
~~~

Não use a senha padrão do Compose em ambientes compartilhados. Sobrescreva `POSTGRES_PASSWORD` no ambiente antes de iniciar os containers.

## Exemplos da API

Criar chamado:

~~~bash
curl -X POST http://localhost:8080/api/tickets \\
  -H 'Content-Type: application/json' \\
  -d '{
    "title": "VPN indisponível para equipe financeira",
    "description": "Usuários remotos não conseguem estabelecer conexão.",
    "requesterEmail": "usuario@example.com",
    "category": "NETWORK",
    "impact": "HIGH",
    "urgency": "HIGH"
  }'
~~~

Listar chamados:

~~~bash
curl http://localhost:8080/api/tickets
~~~

Consultar um chamado:

~~~bash
curl http://localhost:8080/api/tickets/{id}
~~~

Alterar status:

~~~bash
curl -X PATCH http://localhost:8080/api/tickets/{id}/status \\
  -H 'Content-Type: application/json' \\
  -d '{"status":"IN_PROGRESS"}'
~~~

Listar notificações recebidas:

~~~bash
curl http://localhost:8080/api/notifications
~~~

Categorias válidas: `ACCESS`, `HARDWARE`, `SOFTWARE`, `NETWORK`, `SECURITY`, `OTHER`. Impacto e urgência: `LOW`, `MEDIUM`, `HIGH`. Os endpoints de saúde ficam em `/actuator/health` em cada serviço, mas não são publicados pelo gateway.

## Testes

~~~bash
mvn test
~~~

Os testes cobrem matriz de prioridade/SLA, transições permitidas e registro de notificações. O workflow de CI executa `mvn verify` em pushes e pull requests para `main`.

## Organização ágil

O trabalho pode ser acompanhado em um quadro Kanban do GitHub Projects: **Backlog → A fazer → Em andamento → Revisão → Concluído**. Issues representam histórias ou tarefas; pull requests passam por revisão e CI antes de serem incorporados. Consulte [docs/kanban.md](docs/kanban.md) para exemplos de histórias e critérios de aceite.

## Próximos passos

- Persistir notificações e publicar eventos de forma assíncrona com uma fila.
- Adicionar autenticação e autorização por perfil.
- Implementar paginação, filtros e busca de chamados.
- Calcular SLA em horário útil e sinalizar chamados próximos do vencimento.
- Adicionar testes de integração com PostgreSQL e Testcontainers.

## Uso responsável

Use nomes e dados fictícios nos testes e demonstrações. Antes de qualquer implantação real, inclua autenticação, autorização, proteção de dados, gestão de segredos, observabilidade e políticas operacionais adequadas.
