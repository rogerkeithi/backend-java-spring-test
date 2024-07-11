# Requisitos do Projeto

## Funcionalidades

1. **Usuários**:
   - `POST /api/auth/signup`: Registrar um novo usuário.
   - `POST /api/auth/login`: Autenticar o usuário e retornar o token JWT.

2. **Tarefas**:
   - `GET /api/tasks`: Listar todas as tarefas do usuário autenticado.
   - `POST /api/tasks`: Criar uma nova tarefa.
   - `PUT /api/tasks/{id}`: Atualizar uma tarefa existente.
   - `DELETE /api/tasks/{id}`: Deletar uma tarefa.

## Modelagem de Dados

- **User**:
  - `id`: Long
  - `username`: String
  - `password`: String

- **Task**:
  - `id`: Long
  - `title`: String
  - `description`: String
  - `createdAt`: Date
  - `dueDate`: Date
  - `status`: String (pendente, em andamento, concluída)
  - `user`: User

## Critérios de Avaliação

- **Funcionalidade**: O sistema atende a todos os requisitos funcionais especificados?
- **Qualidade do Código**: O código está limpo e bem estruturado? Segue boas práticas de desenvolvimento em Java e Spring Boot?
- **Testes**: Os testes cobrem uma boa parte da aplicação? Os testes são claros e bem organizados?
- **Documentação**: O projeto inclui um README.md com instruções claras sobre como configurar e rodar a aplicação? Os endpoints estão documentados (por exemplo, com Swagger)?

## Recursos Adicionais

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Data JPA Documentation](https://spring.io/projects/spring-data-jpa)
