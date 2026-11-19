# 📚 Biblioteca Web

Sistema web para cadastro de **Autores** e **Livros** (CRUD completo), com relacionamento
**1 : N** — um autor escreve vários livros e cada livro pertence a um autor.
Projeto da disciplina de Engenharia de Software (Prof. Sandro Laudares), Parte 2 — Laboratório,
seguindo o mesmo fluxo do projeto **Agenda Web**.

## 👥 Dupla e divisão do trabalho

| Integrante | Papel | Responsabilidade | Branch |
|---|---|---|---|
| **Pedro Leite Pereira** | DEV 1 | CRUD de Autores: `Autor.java`, `AutorRepository`, `AutorController`, `AutorControllerTest`, `AutorList.js`, `AutorForm.js` + estrutura inicial, `docker-compose.yml` | `feature/autores` |
| **Felipe Birchal Borges** | DEV 2 | CRUD de Livros: `Livro.java`, `LivroRepository`, `LivroController`, `LivroControllerTest`, `LivroList.js`, `LivroForm.js` + `IntegracaoTest`, CI e README | `feature/livros` |

Arquivos comuns (`api.js`, `App.js`) foram editados pelos dois; cada um revisou o Pull Request do outro.

## 🛠️ Tecnologias

| Camada | Tecnologia |
|---|---|
| Backend | Java 17 + Spring Boot 3.2 (Web, Data JPA, Validation, Lombok) + Maven |
| Frontend | React 18 + React Router 6 + Axios (react-scripts 5) • Node.js 20 + npm |
| Banco de dados | PostgreSQL 15 (desenvolvimento) • H2 em memória (perfil `test`) |
| Testes | JUnit 5 + Mockito (`@WebMvcTest`, `@MockBean`, MockMvc) • `@SpringBootTest` • Jest • JaCoCo |
| Versão / CI / Containers | Git + GitHub • GitHub Actions • Docker + Docker Compose |

## 🗂️ Modelo de dados

```
AUTORES (1) ──── escreve ────< (N) LIVROS
id, nome, nacionalidade,          id, titulo, isbn, ano_publicacao,
data_nascimento, criado_em        genero, autor_id (FK), criado_em
```

A chave estrangeira `autor_id` fica na tabela `livros` (`@ManyToOne` + `@JoinColumn(name = "autor_id")`).
`Autor` **não** tem `List<Livro>`, para evitar loop infinito no JSON.

## 📁 Estrutura de pastas

```
biblioteca-web/
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/biblioteca/
│       │   ├── BibliotecaApplication.java
│       │   ├── model/        Autor.java, Livro.java
│       │   ├── repository/   AutorRepository.java, LivroRepository.java
│       │   └── controller/   AutorController.java, LivroController.java
│       ├── main/resources/application.properties
│       └── test/
│           ├── java/com/biblioteca/IntegracaoTest.java
│           ├── java/com/biblioteca/controller/AutorControllerTest.java, LivroControllerTest.java
│           └── resources/application-test.properties
├── frontend/
│   ├── package.json, package-lock.json, Dockerfile, nginx.conf
│   ├── public/index.html
│   └── src/
│       ├── App.js, App.css, index.js
│       ├── services/api.js
│       └── components/  AutorList.js, AutorForm.js, LivroList.js, LivroForm.js
├── docker-compose.yml
├── .github/workflows/ci-cd.yml
└── README.md
```

## 🔌 Endpoints REST

| Verbo | URL | Ação | Status |
|---|---|---|---|
| POST | `/api/autores` | Cria um autor | 201 |
| GET | `/api/autores` | Lista todos (ordem: nome) | 200 |
| GET | `/api/autores/{id}` | Busca um autor | 200 / 404 |
| PUT | `/api/autores/{id}` | Atualiza um autor | 200 / 404 |
| DELETE | `/api/autores/{id}` | Remove um autor (409 se tiver livros) | 200 / 404 / 409 |
| POST | `/api/livros` | Cria um livro ligado a um autor | 201 |
| GET | `/api/livros` | Lista todos (ordem: título) | 200 |
| GET | `/api/livros/{id}` | Busca um livro | 200 / 404 |
| PUT | `/api/livros/{id}` | Atualiza um livro (inclusive o autor) | 200 / 404 |
| DELETE | `/api/livros/{id}` | Remove um livro | 200 / 404 |
| GET | `/api/livros/autor/{autorId}` | Lista os livros de um autor | 200 |

Exemplo para criar um livro (o autor vai só com o `id`):

```json
{ "titulo": "Dom Casmurro", "isbn": "978-8535910667", "anoPublicacao": 1899,
  "genero": "Romance", "autor": { "id": 1 } }
```

Swagger: http://localhost:8080/swagger-ui.html

## ▶️ Como executar

### Tudo com Docker

```bash
docker-compose up -d --build
```

- Frontend: http://localhost:3000
- Backend: http://localhost:8080/api/autores
- PostgreSQL: localhost:5432 (banco `biblioteca_db`, usuário/senha `postgres`/`postgres`)

### Em modo desenvolvimento

```bash
docker-compose up -d postgres        # só o banco
cd backend && mvn spring-boot:run    # API na porta 8080
cd frontend && npm install && npm start   # telas na porta 3000
```

## ✅ Como rodar os testes

```bash
cd backend
mvn test                              # todos (13 testes) + relatório JaCoCo em target/site/jacoco
mvn test -Dtest="*ControllerTest"     # unitários (JUnit 5 + Mockito)
mvn test -Dtest="IntegracaoTest"      # integração (@SpringBootTest + H2)

cd frontend
npm test -- --watchAll=false          # Jest
npm run build
```

### Testes de aceitação com curl

```bash
curl -X POST http://localhost:8080/api/autores -H "Content-Type: application/json" \
  -d '{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}'

curl -X POST http://localhost:8080/api/livros -H "Content-Type: application/json" \
  -d '{"titulo":"Dom Casmurro","isbn":"978-8535910667","anoPublicacao":1899,"genero":"Romance","autor":{"id":1}}'

curl http://localhost:8080/api/livros/autor/1
```

## 🔄 CI (GitHub Actions)

O workflow `.github/workflows/ci-cd.yml` roda em `push` e `pull_request` na `main`:

- **test-backend** — Java 17, testes unitários, integração e JaCoCo;
- **test-frontend** — Node 20, `npm ci`, `npm test`, `npm run build`;
- **deploy** (extra, só em push na `main`) — `docker compose build`, sobe os containers e roda testes de aceitação com curl.
