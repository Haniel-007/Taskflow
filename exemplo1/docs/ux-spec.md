# Especificação de UX e Wireframes - TaskFlow

## 1. Introdução

Este documento descreve os fluxos de navegação e os wireframes para as principais funcionalidades do TaskFlow. O design foca em clareza, simplicidade e eficiência para atender às necessidades das personas Lucas (Colaborador) e Sofia (Gerente).

## 2. Fluxo 1: Login e Cadastro

Este fluxo permite que novos usuários se cadastrem e usuários existentes acessem a plataforma.

**Navegação:**
`Página Inicial` -> `(clica em Login)` -> `Tela de Login` -> `(clica em "Cadastre-se")` -> `Tela de Cadastro`
`Tela de Login` -> `(preenche credenciais)` -> `Dashboard`

---

### Wireframe 2.1: Tela de Login

**Objetivo:** Permitir que Sofia e Lucas acessem suas contas de forma rápida e segura. Atende ao requisito **AU-1**.

```
+---------------------------------------------------+
|                                                   |
|                    TaskFlow                       |
|                                                   |
|   +-------------------------------------------+   |
|   | Email:    [___________________________]   |   |
|   |                                           |   |
|   | Senha:    [___________________________]   |   |
|   |                                           |   |
|   |                [   Entrar   ]             |   |
|   |                                           |   |
|   |      --- ou entre com ---                 |   |
|   |      [ Google ]      [ Microsoft ]        |   |
|   +-------------------------------------------+   |
|                                                   |
|      Não tem uma conta? Cadastre-se             |
|                                                   |
+---------------------------------------------------+
```

---

### Wireframe 2.2: Tela de Cadastro

**Objetivo:** Permitir que um novo usuário (como Lucas) crie uma conta. Atende ao requisito **AU-1**.

```
+---------------------------------------------------+
|                                                   |
|                    TaskFlow                       |
|                  Crie sua conta                   |
|                                                   |
|   +-------------------------------------------+   |
|   | Nome:     [___________________________]   |   |
|   |                                           |   |
|   | Email:    [___________________________]   |   |
|   |                                           |   |
|   | Senha:    [___________________________]   |   |
|   |                                           |   |
|   |             [   Cadastrar   ]             |   |
|   +-------------------------------------------+   |
|                                                   |
|      Já tem uma conta? Faça Login               |
|                                                   |
+---------------------------------------------------+
```

## 3. Fluxo 2: Dashboard Principal

Esta é a primeira tela que o usuário vê após o login. O conteúdo é personalizado com base no seu papel.

---

### Wireframe 3.1: Dashboard do Colaborador (Lucas)

**Objetivo:** Dar a Lucas uma visão clara e imediata de suas prioridades, atendendo ao seu objetivo de ter tudo em um só lugar (**DR-1**).

```
+--------------------------------------------------------------------+
| TaskFlow | [Busca] | [Notificações] | [Avatar de Lucas]              |
+--------------------------------------------------------------------+
|                                                                    |
|  < Meus Projetos >                                                 |
|    - Projeto Alpha                                                 |
|    - Projeto Beta                                                  |
|                                                                    |
|                                                                    |
|  **Olá, Lucas!**                                                   |
|                                                                    |
|  **Minhas Tarefas**                                                |
|                                                                    |
|  Atrasadas (2)                                                     |
|  +--------------------------------------------------------------+  |
|  | [ ] Corrigir bug no login   (Proj. Alpha) | Venceu ontem     |  |
|  | [ ] Atualizar documentação  (Proj. Beta)  | Venceu 2 dias atrás|  |
|  +--------------------------------------------------------------+  |
|                                                                    |
|  Próximas Entregas (3)                                             |
|  +--------------------------------------------------------------+  |
|  | [ ] Desenvolver nova feature (Proj. Alpha) | Vence Hoje       |  |
|  | [ ] Testar API de pagamentos(Proj. Alpha) | Vence Amanhã     |  |
|  | [ ] Reunião de planejamento (Proj. Beta)  | Vence 30/10      |  |
|  +--------------------------------------------------------------+  |
|                                                                    |
+--------------------------------------------------------------------+
```

---

### Wireframe 3.2: Dashboard da Gerente (Sofia)

**Objetivo:** Dar a Sofia uma visão macro do progresso de todos os seus projetos, atendendo à sua necessidade de visibilidade e identificação de gargalos (**DR-2**).

```
+--------------------------------------------------------------------+
| TaskFlow | [Busca] | [Notificações] | [Avatar de Sofia]              |
+--------------------------------------------------------------------+
|                                                                    |
|  < Meus Projetos >                                                 |
|    - Projeto Alpha                                                 |
|    - Projeto Beta                                                  |
|    - Projeto Gamma                                                 |
|  [ + Novo Projeto ]                                                |
|                                                                    |
|  **Olá, Sofia!**                                                   |
|                                                                    |
|  **Visão Geral dos Projetos**                                      |
|                                                                    |
|  +----------------------+  +----------------------+  +-----------+ |
|  | Projeto Alpha        |  | Projeto Beta         |  | Projeto G | |
|  |                      |  |                      |  |           | |
|  | 3 tarefas atrasadas  |  | 0 tarefas atrasadas  |  | 1 tarefa  | |
|  |                      |  |                      |  |           | |
|  | Gráfico: [|||||..]   |  | Gráfico: [|||....]   |  | Gráfico:  | |
|  |                      |  |                      |  |           | |
|  +----------------------+  +----------------------+  +-----------+ |
|                                                                    |
+--------------------------------------------------------------------+
```

## 4. Fluxo 3: Visualização de Projeto (Kanban) e Criação de Tarefa

**Navegação:** `Dashboard` -> `(clica em um projeto)` -> `Quadro Kanban` -> `(clica em "+ Adicionar Tarefa")` -> `Modal de Criação de Tarefa`

---

### Wireframe 4.1: Quadro Kanban

**Objetivo:** Fornecer uma visão clara do fluxo de trabalho do projeto, permitindo que Sofia monitore o progresso (**GT-2**) e que Lucas atualize seu status facilmente (**GT-3**).

```
+----------------------------------------------------------------------------------+
| Projeto Alpha | [Filtros] | [Membros: (Avatar1)(Avatar2)] | [Configurações]        |
+----------------------------------------------------------------------------------+
|                                                                                  |
|   A Fazer (3)                  Em Andamento (2)             Concluído (5)        |
| +----------------------------+ +--------------------------+ +-------------------+|
| | + Adicionar Tarefa         | |                          | |                   ||
| |----------------------------| | +----------------------+ | | +---------------+ ||
| | | Título da Tarefa 1     | | | Título da Tarefa 4   | | | | Título da T...| ||
| | | Vence 01/11 (Avatar L) | | | Vence 02/11 (Avatar M)| | | | (Avatar L)    | ||
| | +----------------------+ | | +----------------------+ | | +---------------+ ||
| |                          | |                          | |                   ||
| | +----------------------+ | | +----------------------+ | |                   ||
| | | Título da Tarefa 2     | | | Título da Tarefa 5   | | |                   ||
| | | Vence 03/11 (Avatar L) | | | Vence 04/11 (Avatar J)| | |                   ||
| | +----------------------+ | | +----------------------+ | |                   ||
| +----------------------------+ +--------------------------+ +-------------------+|
|                                                                                  |
+----------------------------------------------------------------------------------+
```

---

### Wireframe 4.2: Modal de Criação de Tarefa

**Objetivo:** Permitir a criação de uma tarefa com todas as informações necessárias, de forma rápida e contextual. Atende ao requisito **GT-1**.

```
      +---------------------------------------------+
      | **Nova Tarefa**                           X |
      |---------------------------------------------|
      |                                             |
      | Título*                                     |
      | [Implementar fluxo de login           ]     |
      |                                             |
      | Descrição                                   |
      | [Detalhes da implementação...         ]     |
      | [                                     ]     |
      |                                             |
      | Responsável          Data de Entrega        |
      | [(Avatar L) Lucas C.]  [ 31/10/2025     ]    |
      |                                             |
      |                                             |
      |                      [ Cancelar ] [ Criar ] |
      +---------------------------------------------+
```
