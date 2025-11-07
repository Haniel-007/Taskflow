# Visão Geral do Produto - TaskFlow

## 1. Objetivo Geral

O TaskFlow é uma aplicação web de gerenciamento de tarefas e projetos, desenhada para equipes modernas que buscam **simplicidade, agilidade e inteligência** em suas ferramentas de trabalho.

O principal problema que o TaskFlow visa solucionar é a fragmentação da comunicação e a falta de clareza nos fluxos de trabalho, que geram ineficiência, atrasos e desmotivação. Muitas ferramentas no mercado são ou simples demais, carecendo de funcionalidades essenciais para gestores, ou complexas demais, sobrecarregando os colaboradores e exigindo longos períodos de adaptação.

Nossa visão é posicionar o TaskFlow como a plataforma que equilibra perfeitamente esses dois mundos:
- **Para o colaborador:** Uma experiência de usuário limpa, intuitiva e focada, que centraliza todas as informações e facilita a execução das tarefas.
- **Para o gerente:** Uma ferramenta poderosa que oferece visibilidade completa sobre o progresso dos projetos, a carga de trabalho da equipe e insights para a tomada de decisão.
- **Para o administrador:** Um sistema seguro, controlável e de fácil integração, que garante a governança e a conformidade dos dados.

## 2. Público-Alvo

Nossa solução é desenhada para atender às necessidades de três perfis de usuários interconectados dentro de uma organização:

### **O Administrador (Persona: Carlos Mendes)**
Responsável pela infraestrutura de TI, Carlos busca uma ferramenta que seja segura, fácil de gerenciar em escala e que se integre com os sistemas existentes da empresa (como login único). Suas prioridades são o controle de acesso, as permissões granulares e a capacidade de auditar o uso do sistema.

### **A Gerente (Persona: Sofia Andrade)**
Como Gerente de Projetos, Sofia precisa de uma visão macro e em tempo real do que está acontecendo em suas equipes. Ela busca planejar projetos, delegar tarefas de forma clara, identificar gargalos rapidamente e gerar relatórios de progresso sem esforço. Sua maior dor é a falta de visibilidade que a obriga a microgerenciar e a gastar tempo excessivo em reuniões de status.

### **O Colaborador (Persona: Lucas Costa)**
Membro de uma equipe de desenvolvimento, Lucas precisa de clareza sobre suas prioridades e um local único para encontrar todas as informações, arquivos e discussões relacionadas às suas tarefas. Ele se frustra com a comunicação espalhada por e-mails e chats, e busca uma forma rápida e simples de atualizar seu progresso e colaborar com os colegas.

## 3. Principais Funcionalidades (MVP)

O MVP (Minimum Viable Product) do TaskFlow está focado em entregar o valor essencial para cada uma das personas através das seguintes funcionalidades:

### **Para Todos os Usuários:**
- **Autenticação Segura:** Cadastro e login com e-mail e senha.
- **Gestão de Tarefas em Quadro Kanban:** Visualização clara do fluxo de trabalho com colunas "A Fazer", "Em Andamento" e "Concluído".
- **Criação e Detalhamento de Tarefas:** Possibilidade de criar tarefas com título, descrição, responsável e data de entrega.
- **Colaboração Contextualizada:** Adição de comentários em tarefas para centralizar a comunicação.

### **Para Gerentes (Sofia):**
- **Criação e Gestão de Projetos:** Autonomia para criar novos projetos e adicionar ou remover membros.
- **Dashboard de Projetos:** Uma visão geral com o status agregado das tarefas de cada projeto para facilitar o acompanhamento.
- **Filtros Avançados:** Capacidade de filtrar tarefas por responsável ou status para identificar rapidamente a carga de trabalho e os pontos de bloqueio.

### **Para Administradores (Carlos):**
- **Painel de Gerenciamento de Usuários:** Uma área central para visualizar todos os usuários, desativar contas e atribuir papéis (promover a gerente).
- **Sistema de Permissões:** Três níveis de acesso bem definidos (Administrador, Gerente, Colaborador) para garantir que cada usuário veja e faça apenas o que é pertinente à sua função.

---

## 4. Status dos Requisitos no MVP

Com base no [Product Requirements Document (PRD)](./prd.md), o status atual das funcionalidades planejadas para o MVP é o seguinte:

### **4.1. Requisitos Implementados**

- **(AU-1)** Cadastro de usuários com e-mail and senha.
- **(GP-3)** Visualização da lista de projetos para membros.
- **(GT-1)** Criação de tarefas com título, descrição, responsável e data.
- **(GT-4)** Filtro de tarefas por responsável e status.
- **(GT-5)** Exclusão de tarefas.
- **(CL-1)** Adição de comentários em tarefas.
- **(CA-2)** Sistema com 3 níveis de permissão (Admin, Manager, Collaborator).

### **4.2. Requisitos Parcialmente Implementados**

- **(GP-1)** Gestão de Projetos (Apenas a criação foi implementada).
- **(GP-2)** Gestão de Membros (Apenas a adição foi implementada).
- **(GT-2/GT-3)** Quadro Kanban (Backend preparado, mas a interface de arrastar e soltar não está implementada).
- **(DR-1/DR-2)** Dashboards (Apenas resumos básicos por status foram implementados, sem gráficos).
- **(CA-1)** Painel de Administração (Implementado listar, desativar e promover a gerente; falta convidar owner).

### **4.3. Requisitos Não Implementados**

- **(AU-2)** Login via SSO (Google, Microsoft).
- **(AU-3)** Página de perfil de usuário.
- **(CL-2)** Anexo de arquivos em tarefas.
- **(CL-3)** Notificações de menção (`@nome`).
- **(DR-3)** Exportação de dashboard para PDF.
- **(CA-3)** Logs de auditoria para administradores.
