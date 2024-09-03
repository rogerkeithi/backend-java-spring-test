
# Teste prático para desenvolvedor pleno Back-End em Java e Spring Boot

Este repositório contém o teste prático para a vaga de Desenvolvedor Pleno Back-End em Java e Spring Boot.

## Requisitos
- Java 11 ou superior;
- Maven


## Configuração Automática

Para facilitar a configuração do ambiente, você pode usar o script `setup.sh` incluído no repositório. 

Caso esteja utilizando Windows, use o Git Bash para executar os comandos.

## Instruções para configuração e utilização da aplicação

1. Clone o repositório:
   ```bash
   git clone https://github.com/rogerkeithi/backend-java-spring-test.git
   cd backend-java-spring-test

2. Dê permissão de execução ao script:
    ```bash
    chmod +x setup.sh
    ```

3. Execute o script:
    ```bash
    ./setup.sh
    ```

O script irá verificar se você tem Java 11 e Maven instalados, instalar as dependências do Maven e iniciar a aplicação Spring Boot.

4. Se preferir executar a aplicação sem o script, utilize os seguintes comandos:
    ```bash
    mvn clean install
    ```
    Este comando instala as dependências do projeto.

    ```bash
    mvn spring-boot:run
    ```
    Este executa a nossa aplicação.
    
5. Para executar os testes, utilize o seguinte comando:
    ```bash
    mvn test
    ```
    

6. Acessando a aplicação:
Após o passo acima, a aplicação se encontrará rodando em: http://127.0.0.1:8080/

Para fins de desenvolvimento, há 2 usuários mockados:


<details>
<summary><b>Mostrar usuários</b></summary>

    - Usuário: admin
        - Senha: 123

    - Usuário: testuser
        - Senha: 456

</details>
    

