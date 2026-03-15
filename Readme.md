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
src/main/java  
├── controller       -> Controllers REST  
├── service          -> Lógica de negócio  
├── repository       -> Repositórios JPA  
├── model            -> Entidades JPA  
└── dto              -> Data Transfer Objects  

## Como rodar

Inicie o banco MySQL via Docker:  
docker-compose up -d

application.yml já está configurado para conexão no banco conforme docker compose.

## Build e run:

mvn clean install
mvn spring-boot:run

## Endpoints Principais
Método	URL	Descrição  
POST ->	/api/v1/pautas -> Criar nova pauta  
POST ->	/api/v1/sessoes ->	Abrir sessão de votação  
POST -> /api/v1/votos -> Registrar voto de associado  
GET -> /api/v1/pautas/{id}/resultado ->	Obter resultado consolidado da pauta  

## ==> ESCOLHAS

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

8 - Documentação da API (swagger)  

9 - Estrutura README

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
"descricao": "Votação do orçamento anual",  
"criadoEm": "2026-03-14T10:00:00"  
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
"fim": "2026-03-14T10:06:00",  
"aberta": true  
}  

### Registrar Voto

Request  
{  
"pautaId": 1,  
"associadoId": 123,  
"valor": "SIM"  
}  

Response  
{  
"id": 1,  
"sessaoId": 1,  
"associadoId": 123,  
"valor": "SIM",  
"criadoEm": "2026-03-14T10:02:00"  
}  

### Resultado Consolidado da Pauta

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