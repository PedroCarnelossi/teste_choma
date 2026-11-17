# Testes de integração com Spring Boot e JUnit 5

Resolução da lista prática de testes de integração (Semana 11) a partir do projeto-base fornecido.

**Alunos:**

- André Filipe Caetano Mulati — RA: 24011149-2
- Pedro Massaki Carnelossi — RA: 24036481-2

**Tecnologias:** Java 17, Maven, Spring Boot 3.3.13, Spring Data JPA, H2 (em memória), JUnit 5 e MockMvc.

Nenhum mock foi usado: `ProdutoService` e `ProdutoRepository` são os beans reais, e os dados são gravados no H2.

## Como executar

```bash
mvn test
```

## Testes implementados

### Exercício 1 — Service + Repository + H2 (`ProdutoServiceIntegrationTest`)

| Caso | Método | O que o teste faz | Resultado |
|---|---|---|---|
| CT01 — Cadastro válido | `deveCadastrarERecuperarProduto` | Cadastra `Teclado` por `150.0` pelo serviço, confere que recebeu ID e busca o registro com `repository.findById()`, conferindo nome e preço. | Passou |
| CT02 — Nome inválido | `naoDevePersistirProdutoComNomeEmBranco` | Guarda `repository.count()`, tenta cadastrar nome em branco com preço `80.0`, espera `IllegalArgumentException` e confere que a contagem não mudou. | Passou |
| CT03 — Atualização (desafio) | `deveAtualizarPrecoPersistido` | Cadastra `Mouse` por `80.0`, atualiza para `95.0` com `atualizarPreco()` e busca de novo no repositório, conferindo preço `95.0`. | Passou |

Em CT01 e CT03, antes da consulta, o teste chama `entityManager.flush()` e `entityManager.clear()`. Assim o `findById()` faz um `SELECT` de verdade no H2, em vez de devolver o objeto que já estava na memória do Hibernate (explicado na discussão abaixo).

### Exercício 2 — HTTP com MockMvc (`ProdutoControllerIntegrationTest`)

| Caso | Método | O que o teste faz | Resultado |
|---|---|---|---|
| CT04 — Cadastro via API | `deveCadastrarProdutoPelaApi` | `POST /produtos` com `{"nome":"Mouse","preco":80.0}`; confere HTTP `200`, `$.id` numérico, `$.nome = "Mouse"` e `$.preco = 80.0`. | Passou |
| CT05 — Consulta via API | `deveBuscarProdutoPelaApi` | Faz o `POST`, lê o corpo da resposta com `ObjectMapper`/`JsonNode` para extrair o ID e faz `GET /produtos/{id}`; confere HTTP `200` e que ID, nome e preço são os do produto cadastrado. | Passou |

No CT05 também há `flush()` e `clear()` entre o `POST` e o `GET`, pelo mesmo motivo do Exercício 1: assim o `GET` lê o produto do H2, e não da memória do Hibernate.

## Evidência da execução

Saída de `mvn test`:

```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running br.edu.exemplo.ProdutoControllerIntegrationTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 7.755 s -- in br.edu.exemplo.ProdutoControllerIntegrationTest
[INFO] Running br.edu.exemplo.ProdutoServiceIntegrationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 1.165 s -- in br.edu.exemplo.ProdutoServiceIntegrationTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] BUILD SUCCESS
```

Resultado por teste (relatórios do Surefire em `target/surefire-reports`):

| Classe | Teste | Situação |
|---|---|---|
| ProdutoServiceIntegrationTest | deveCadastrarERecuperarProduto (CT01) | Passou |
| ProdutoServiceIntegrationTest | naoDevePersistirProdutoComNomeEmBranco (CT02) | Passou |
| ProdutoServiceIntegrationTest | deveAtualizarPrecoPersistido (CT03) | Passou |
| ProdutoControllerIntegrationTest | deveCadastrarProdutoPelaApi (CT04) | Passou |
| ProdutoControllerIntegrationTest | deveBuscarProdutoPelaApi (CT05) | Passou |

## Isolamento dos testes

- **Banco em memória:** o H2 (`jdbc:h2:mem:produtos`) existe só enquanto a aplicação de teste roda e as tabelas são criadas do zero (`ddl-auto=create-drop`). Nenhum banco externo é tocado.
- **Rollback a cada teste:** as duas classes usam `@Transactional`. O Spring abre uma transação antes de cada método de teste e a desfaz (rollback) no final, então o que um teste grava não aparece para o próximo.
- **Sem dependência de ordem:** cada teste cria os próprios dados. O CT02 compara a contagem de antes e de depois, em vez de esperar um número fixo de registros.
- **MockMvc na mesma thread:** a requisição simulada roda na mesma thread do teste, então também participa da transação e sofre o rollback (isso não aconteceria com um servidor real em porta aleatória).

## Discussão

### Exercício 1

**Por que `repository.findById()` é uma evidência diferente de olhar só o objeto devolvido pelo serviço?**
O objeto que o serviço devolve é o que está na memória: ver nele nome e preço corretos só prova que o serviço montou e devolveu o objeto, não que o registro foi gravado. Consultar pelo repositório passa pelo Spring Data JPA, pelo Hibernate e pelo H2, e prova que o dado está persistido e pode ser lido de volta pelo ID gerado.

Há um detalhe: dentro da mesma transação, o Hibernate guarda as entidades num cache (o contexto de persistência), e o `findById()` pode devolver o mesmo objeto sem ir ao banco. Por isso os testes chamam `flush()` (envia ao H2 as alterações pendentes) e `clear()` (esvazia o cache) antes de consultar. No CT01 o `INSERT` já acontece no `save()`, porque o ID é gerado pelo banco (`IDENTITY`); o que garante a leitura do H2 é o `clear()`. No CT03 os dois importam: a alteração de preço só vai para o banco no flush, e sem ele o teste passaria mesmo que nada tivesse sido gravado.

**O que `@Transactional` faz ao término de cada teste?**
Em classes de teste, o Spring desfaz a transação (rollback) ao final de cada método. Os produtos cadastrados no teste são descartados, o banco volta ao estado anterior e um teste não interfere no outro.

### Exercício 2

**Quais componentes são efetivamente exercitados?**
O fluxo inteiro da aplicação, sem substitutos: o `DispatcherServlet` do Spring MVC, a conversão do JSON de entrada para `ProdutoRequest` (Jackson), o `ProdutoController`, as regras do `ProdutoService`, o `ProdutoRepository` (Spring Data JPA/Hibernate), o banco H2 e a conversão do `Produto` de volta para JSON. A única parte que não entra é a rede (servidor Tomcat e porta HTTP).

**Por que o MockMvc não precisa iniciar um servidor HTTP real?**
Com `@SpringBootTest` (ambiente `MOCK`, o padrão) e `@AutoConfigureMockMvc`, o Spring sobe o contexto web com um ambiente de servlet simulado. O MockMvc monta objetos de requisição e resposta em memória (`MockHttpServletRequest`/`MockHttpServletResponse`) e os entrega direto ao `DispatcherServlet`, no mesmo processo. Não há porta aberta nem tráfego de rede, mas o tratamento da requisição (mapeamento de rota, conversão de JSON, código de status) é o mesmo da aplicação real.

**Qual a diferença entre esse teste e um teste unitário do controller com dependências simuladas?**
Num teste unitário do controller (por exemplo, `@WebMvcTest` com o `ProdutoService` como mock), só a camada web é carregada e o serviço devolve respostas programadas. Ele verifica rota, status e formato do JSON, é rápido e aponta exatamente onde está o erro, mas não pega problemas de validação do serviço, de mapeamento JPA, de geração de ID ou de persistência. O teste de integração executa a cadeia real até o banco e encontra esses problemas de integração, ao custo de ser mais lento (o contexto do Spring leva alguns segundos para subir) e de deixar menos óbvio em qual camada está a falha.
