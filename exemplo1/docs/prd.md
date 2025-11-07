# Product Requirements Document (PRD) - TaskFlow MVP

## 1. Introdução

TaskFlow é uma aplicação web de gerenciamento de tarefas projetada para ser **simples, rápida e inteligente**. O problema central que buscamos resolver é a desorganização e a falta de clareza na gestão de projetos, que afeta desde o colaborador até a gestão e a administração de TI.

A visão para o TaskFlow é ser uma plataforma completa que ofereça uma experiência de usuário intuitiva para o colaborador, insights poderosos para o gerente e controle robusto para o administrador. Este documento descreve os requisitos para o **Minimum Viable Product (MVP)**, que visa entregar valor para as três personas principais.

## 2. Personas-Alvo

- **Carlos Mendes (Administrador):** Busca segurança, controle e integração.
- **Sofia Andrade (Gerente):** Busca visibilidade, planejamento e relatórios.
- **Lucas Costa (Colaborador):** Busca clareza, foco e colaboração centralizada.

## 3. Requisitos Funcionais do MVP

### 3.1. Autenticação e Perfis

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| AU-1 | Os usuários devem poder se cadastrar usando e-mail e senha. | **Lucas:** Permite o acesso inicial à plataforma para que ele possa ver suas tarefas. | **Implementado** |
| AU-2 | O sistema deve suportar login via SSO (Google, Microsoft). | **Carlos:** Atende ao seu objetivo de integrar com sistemas existentes e frustração com múltiplas credenciais. | **Não Implementado** |
| AU-3 | Os usuários devem ter uma página de perfil simples para visualizar seu nome e e-mail. | **Todos:** Funcionalidade básica de identidade dentro do sistema. | **Não Implementado** |

### 3.2. Gestão de Projetos

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| GP-1 | Gerentes (Sofia) devem poder criar, renomear e arquivar projetos. | **Sofia:** Atende diretamente ao seu objetivo de planejar projetos e organizar o trabalho de suas equipes. | **Parcialmente Implementado (Criação)** |
| GP-2 | Gerentes (Sofia) devem poder adicionar e remover membros de um projeto. | **Sofia:** Permite a ela montar as equipes corretas para cada iniciativa, resolvendo a frustração de não ter as pessoas certas com a visibilidade correta. | **Parcialmente Implementado (Adição)** |
| GP-3 | Todos os membros do projeto devem poder visualizar a lista de projetos aos quais pertencem. | **Lucas:** Permite que ele tenha clareza sobre em quais frentes de trabalho está envolvido. | **Implementado** |

### 3.3. Gestão de Tarefas

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| GT-1 | Dentro de um projeto, usuários devem poder criar tarefas com título, descrição, responsável e data de entrega. | **Sofia:** Atende à sua necessidade de delegar tarefas de forma clara. <br> **Lucas:** Resolve sua frustração de receber demandas sem contexto ou prazo. | **Implementado** |
| GT-2 | As tarefas devem ser exibidas em um quadro Kanban (colunas: A Fazer, Em Andamento, Concluído). | **Sofia:** Oferece a visibilidade em tempo real que ela precisa para acompanhar o progresso. <br> **Lucas:** Permite que ele atualize o status de forma simples e visual. | **Parcialmente Implementado (Backend)** |
| GT-3 | Usuários devem poder arrastar e soltar tarefas entre as colunas do quadro. | **Lucas:** Atende ao seu objetivo de atualizar seu status de forma rápida, resolvendo a frustração de ser constantemente interrompido para isso. | **Parcialmente Implementado (Backend)** |
| GT-4 | Usuários devem poder filtrar tarefas (por responsável, por status). | **Sofia:** Ajuda a identificar gargalos e a carga de trabalho da equipe. <br> **Lucas:** Permite que ele foque apenas nas suas tarefas. | **Implementado** |
| GT-5 | Usuários devem poder excluir tarefas. | **Todos:** Funcionalidade básica de gerenciamento. | **Implementado** |

### 3.4. Colaboração

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| CL-1 | Usuários devem poder adicionar comentários em uma tarefa. | **Lucas:** Resolve sua principal frustração de ter informações espalhadas, centralizando a discussão no contexto da tarefa. | **Implementado** |
| CL-2 | Usuários devem poder anexar arquivos (upload do computador) a uma tarefa. | **Lucas:** Atende ao seu objetivo de encontrar facilmente todos os documentos necessários para concluir seu trabalho. | **Não Implementado** |
| CL-3 | O sistema deve enviar notificações (dentro do app) quando um usuário é mencionado em um comentário (`@nome`). | **Todos:** Garante que a comunicação seja vista pelas pessoas certas sem depender de canais externos. | **Não Implementado** |

### 3.5. Dashboard e Relatórios

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| DR-1 | A página inicial (Dashboard) deve mostrar um resumo das tarefas do usuário (atribuídas a mim, atrasadas). | **Lucas:** Atende ao seu objetivo de ver todas as suas prioridades em um único lugar. | **Parcialmente Implementado (Resumo por status)** |
| DR-2 | Gerentes (Sofia) devem ter acesso a um dashboard de projeto com gráficos simples (ex: tarefas por status, tarefas por membro da equipe). | **Sofia:** Atende diretamente ao seu objetivo de ter uma visão geral do projeto e identificar quem está sobrecarregado, resolvendo sua frustração com a falta de visibilidade. | **Parcialmente Implementado (Resumo por status)** |
| DR-3 | Gerentes (Sofia) devem poder exportar a visualização do dashboard em PDF. | **Sofia:** Atende à sua necessidade de gerar relatórios de progresso para a diretoria com poucos cliques. | **Não Implementado** |

### 3.6. Configurações e Acesso

| ID | Requisito | Conexão com a Persona |
| :-- | :--- | :--- |
| CA-1 | Administradores (Carlos) devem ter um painel para gerenciar todos os usuários (convidar a ser o líder de um projeto (owner), desativar, promover a gerente). | **Carlos:** Resolve sua frustração com processos manuais para gerenciar usuários e atende ao seu objetivo de controle centralizado. | **Parcialmente Implementado (Listar, Desativar, Promover a Gerente)** |
| CA-2 | O sistema deve ter 3 níveis de permissão: Administrador, Gerente e Colaborador. | **Carlos:** Resolve sua frustração com ferramentas que não possuem controles de permissão granulares. | **Implementado** |
| CA-3 | Administradores (Carlos) devem poder visualizar logs de auditoria básicos (ex: login, criação de projeto). | **Carlos:** Atende à sua necessidade de auditar o sistema e garantir a conformidade. | **Não Implementado** |

## 4. Fora do Escopo do MVP

As seguintes funcionalidades **NÃO** serão incluídas nesta versão inicial para garantir o foco na entrega de valor principal:

-   Chat em tempo real.
-   Visualização de projetos em Diagrama de Gantt ou Calendário.
-   Integração com faturamento ou sistemas de time tracking.
-   Automações de regras (ex: "Quando a tarefa for para 'Concluído', notificar no Slack").
-   Campos personalizados nas tarefas.
-   Gestão de dependências entre tarefas.
-   Recursos avançados de IA (análise preditiva, sugestão de prioridades).
