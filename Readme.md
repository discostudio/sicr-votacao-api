# Sicr Votação API

API REST para gerenciamento de pautas, sessões de votação e registro de votos em cooperativas.
Permite criar pautas, abrir sessões de votação, registrar votos e obter resultados consolidados e por sessão.

## Tecnologias

Java 21+  
Spring Boot 3.5.11  
Spring Data JPA  
MySQL (via Docker)  
Lombok  
Maven  

## Estrutura do Projeto
src/main/java/org.fhc.sicrvotacaoapi 
├── controller       -> Controllers REST  
├── service          -> Lógica de negócio  
├── repository       -> Repositórios JPA  
├── model            -> Entidades JPA  
├── dto              -> Data Transfer Objects  
├── configuration    -> Beans de configuração (Swagger e WebClient)  
└── exception        -> Customização de exceções  
src/test/java/org.fhc.sicrvotacaoapi  
├── controller       -> Testes unitários básicos para os controllers REST  
└── service          -> Testes unitários básicos para as services (lógica de negócio)

## 🚀 Como Executar o Projeto - via **Docker**

1. Necessário: **Docker** instalado.
2. Clone o repositório e acesse a pasta raiz.
3. Utilize o comando `docker-compose up --build` para subir a aplicação **java** e o banco **MySQL**.
4. Os testes podem ser executados nos endpoints via **Postman**.

## Endpoints Principais
Método ->	URL -> Descrição  
POST ->	http://localhost:8080/api/v1/pautas -> Criar nova pauta  
POST ->	http://localhost:8080/api/v1/sessoes ->	Abrir sessão de votação  
POST -> http://localhost:8080/api/v1/votos -> Registrar voto de associado  
GET -> http://localhost:8080/api/v1/pautas/{id}/resultado ->	Obter resultado básico da pauta  
GET -> http://localhost:8080/api/v1/pautas/{id}/resultadoDetalhado ->	Obter resultado datalhado da pauta

## Formato das Requisições/Respostas
### Criar Pauta

Request  
{  
"nome": "Aprovação do orçamento",  
"descricao": "Votação do orçamento anual"  
}

Response
{  
"id": 1,  
"nome": "Aprovação do orçamento",  
"descricao": "Votação do orçamento anual"
}

### Abrir Sessão

Request  
{  
"pautaId": 1,  
"duracaoEmMinutos": 5  
}

Response  
{  
"id": 1,  
"pautaId": 1,  
"inicio": "2026-03-14T10:01:00",  
"fim": "2026-03-14T10:06:00"
}

### Registrar Voto

Request  
{  
"pautaId": 1,  
"associadoCPF": "12345678912",  
"valor": "SIM"  
}  
**OBS.**  
Dado que a API de validação externa não está com o comportamento esperado, o mock de callback está ativado no application.yml (external-services.cpf-validator.use-mock-on-failure: true), retornando ABLE_TO_VOTE para CPFs com um número par nos dois últimos dígitos, e UNABLE_TO_VOTE para CPFs com um número ímpar nos dois últimos dígitos (a formatação do CPF não está sendo validada, para fins de simplicidade).  
Para desativar a validação e permitir o voto para qualquer CPF, pode desabilitar a validação no application.yml (external-services.cpf-validator.enabled: false).  

Response  
{  
"id": 1,  
"sessaoId": 1,  
"associadoCPF": "12345678912",  
"valor": "SIM"
}

### Resultado da Pauta  

Response  
{  
"pautaId": 1,  
"resultado": "SIM",  
"possuiSessoesAbertas": true  
}  

### Resultado Detalhado da Pauta

Response  
{  
"pautaId": 1,  
"totalSim": 5,  
"totalNao": 3,  
"totalVotos": 8,  
"resultado": "SIM",  
"sessoesAbertas": false,  
"resultadosPorSessao": [  
{  
"sessaoId": 1,  
"totalSim": 3,  
"totalNao": 2,  
"resultado": "SIM",  
"aberta": false  
},  
{  
"sessaoId": 2,  
"totalSim": 2,  
"totalNao": 1,  
"resultado": "SIM",  
"aberta": false  
}  
]}

## ==> DECISÕES TÉCNICAS  

- Estruras das pastas   
  Organizei o projeto em controller, service, repository, model e dtos para seguir o princípio de separação de responsabilidades.
  Os controllers lidam apenas com HTTP, os services encapsulam regras de negócio, os repositories cuidam da persistência, as entidades do model representam o domínio, e os DTOs definem a interface pública da API.
  Isso deixa o projeto mais limpo, testável e escalável.  
  

- Versionamento API  
  Escolhi versionamento via URL porque deixa explícita a versão para o cliente e facilita manutenção de múltiplas versões para cada controller.


- Banco de dados  
  Escolhi banco relacional (MySQL) porque atende aos requisitos "centenas de milhares de votos" e "pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação".
  Além disso, os dados da aplicação são estruturados e têm relacionamentos claros (Pauta, Sessao, Voto).
  Também precisamos garantir consistência e integridade com transações ACID, e realizar consultas agregadas de forma eficiente, inclusive facilitando os uso de JPA/Hibernate.


- Performance  
  Optei por otimizar o código e o banco através de avaliação de cenários n+1, priorizando queries customizadas em detrimento à queries padrão do Spring Data, nos repositories de Voto e Resultado, buscando maior controle e reduzir carga no banco.
  Também criei índices no banco pensando nas consultas realizadas.


- Consulta serviço externo de validação do CPF  
  O serviço atualmente está respondendo 404 com qualquer CPF, contradizendo o comportamento esperado de retornar 404 para CPF inválido.  
  Fiz uma customização na consulta para diferenciar um 404 técnico (com HTML) de um 404 de validação do CPF, adquando o mais próximopossível a comportamento do serviço.  
  Também criei um "callback" para o serviço externo, através de um mock que retorna ABLE_TO_VOTE para CPFs com um número par nos dois últimos dígitos, e UNABLE_TO_VOTE para CPFs com um número ímpar nos dois últimos dígitos.   

## ==> JOURNAL (passo a passo da implementação):

1 - Criação do repositório vazio no github

2 - Criação do projeto local, simulando scaffolding inicial e incluindo configuração básica de BD MySQL com uso de container docker  
2.1 - complementando scaffolding com estrutura de pastas  
2.2 - Estrutura para tratamento global de exceções e campos do payload

3 - Cadastro de nova pauta  
-> Incluir pauta

4 - Cadastro de nova sessão de votação  
-> Validar se pauta existe  
-> Validar se já existe uma sessão de votação aberta para a pauta  
-> Incluir sessão de votação relacionada com a pauta  
4.1 - Permitir mais de uma sessão de votação por pauta, após encerrada uma sessão anterior - somente uma sessão aberta por pauta

5 - Cadastro de votos na pauta - somente um voto por associado  
-> Validar se pauta existe  
-> Validar se existe uma sessão aberta para a pauta  
-> Validar se associado já votou na pauta (na sessão aberta ou em sessões anteriores)  
-> Validar valor informado para o voto - SIM ou NAO  
-> Incluir voto  

6 - Contabilizar os votos e verificar resultado da votação - resultado básico e consolidado (detalhado)  
-> Valida a pauta  
-> Valida a existência de sessões  
-> Valida a existência de votos na pauta  
-> Contabiliza e retorna resultado  

7 - Refactoring tratamento de exceções  
-> Utilização de BusinessException ao invés de exceções customizadas para erros de negócio

8 - Documentação da API (swagger)  

9 - Estrutura README  

10 - Tarefa bônus: integração verificação CPF  
-> URL e timeout via application.yml, e classe de configuração para WebClient  
-> Feature toggles para chamada da API externa e fallback com mock  
-> Chama a API externa  
-> Diferencia erro 404 de negócio de erro 404 técnico, adequado ao comportamento da API externa  
-> Fallback com mock em caso de erro na API  
-> Permite ou não votar  

11 - Revisão de logs básicos  

12 - Testes unitários básicos  

13 - Tarefa bônus: performance  
-> Índices nas tabelas  
-> queries customizadas no JPA

## Melhorias futuras  

Maior cobertura de testes unitários.

Análise estática de código (com SonarQube, por exemplo).

Testes de performance (com JMeter, por exemplo), quantificando a necessidade de ajustes de arquitetura e escalabilidade citados no próximo item.  

Implementação de segurança (token, https).

## Arquitetura e Escalabilidade

A implementação atual foi projetada para manter simplicidade e clareza, atendendo aos requisitos funcionais com processamento síncrono das operações de voto e apuração.

No entanto, em cenários com maior volume de requisições ou necessidade de maior escalabilidade, algumas evoluções arquiteturais podem ser consideradas.

### Processamento Assíncrono de Votos

Uma possível evolução seria a introdução de um event broker (por exemplo Kafka ou RabbitMQ) para desacoplar o recebimento dos votos do seu processamento. Nesse modelo, o registro de um voto publicaria um evento que poderia ser consumido posteriormente por serviços responsáveis pelo processamento.

Essa abordagem permite redução do acoplamento entre componentes, maior escalabilidade horizontal e melhor absorção de picos de carga.

### Consolidação de Resultados com Workers

Outra evolução possível seria a introdução de workers responsáveis por processar sessões de votação encerradas, que poderiam consolidar os votos de uma sessão e persistir o resultado agregado, simplificando e otimizando a consulta de resultados finais.

Dessa forma, evita-se a necessidade de computar resultados em tempo real sempre que uma consulta for realizada, reduzindo a carga sobre o banco de dados e melhorando a eficiência da aplicação.

Essas estratégias se tornam especialmente relevantes em sistemas com alto volume de eventos ou com múltiplas sessões de votação ocorrendo simultaneamente.

## Validações / Exceções

Campos obrigatórios não podem ser nulos ou vazios.  
Apenas votos "SIM" ou "NÃO" são aceitos.  
Associado só pode votar uma vez por pauta.  
Não é possível abrir mais de uma sessão ativa por pauta.  
Se não houver votos registrados, retorna erro 404 com mensagem informativa.  
Exceções retornam JSON no formato:
{  
"message": "Descrição do erro",  
"fieldErrors": {  
"campo": "Mensagem do erro"  
}  
}  

## Observações   

Configurações de URLs e porta podem ser ajustadas via application.yml.  
Segurança das APIs foi abstraída para fins de teste.  
Todos os endpoints usam JSON para entrada e saída.  
A aplicação pode ser testada com Postman ou qualquer cliente HTTP.  

---
Desenvolvido por Fernando Cardoso.
