# Teste e Qualidade de Software — Atividades das Semanas 10, 11 e 14

**Alunos:**

- André Filipe Caetano Mulati — RA: 24011149-2
- Pedro Massaki Carnelossi — RA: 24036481-2

## Atividades

| Pasta | Atividade | Conteúdo |
|---|---|---|
| [`semana-10`](semana-10) | Práticas de TDD | Quatro projetos Maven (Java 17 + JUnit 5): Bubble Sort, carrinho de compras, conta bancária e estoque. |
| [`semana-11`](semana-11) | Testes de integração com Spring Boot | Testes de integração de Service, Repository e banco H2, e da API HTTP com MockMvc (CT01 a CT05). |
| [`semana-14`](semana-14) | Teste de usuário final | Teste de usabilidade no Mercado Livre, com planilha de observação, métricas, critérios de sucesso e questionário SUS. |

## Semana 10 — Práticas de TDD

Cada subpasta é um projeto independente, resolvido no ciclo RED → GREEN → REFACTOR:

- **`bubblesort-tdd`:** implementação do método `ordenar()`, com os testes do professor sem alteração (3 testes).
- **`carrinho-compras-tdd`:** testes escritos para os requisitos 1 a 6 e implementação da classe `Carrinho`. Produto com preço negativo lança `IllegalArgumentException`, e o desconto de 10% a partir de R$ 500,00 é testado em R$ 499,99, R$ 500,00 e R$ 500,01 (9 testes).
- **`conta-bancaria-tdd-maven`:** implementação de depósito e saque; saque maior que o saldo não altera a conta (4 testes).
- **`estoque-tdd`:** implementação de entrada e retirada de estoque, mais um teste para entrada igual a zero, pedida no requisito 6 (7 testes).

Para executar, dentro da pasta de cada projeto: `mvn test`. Os 23 testes passam.

## Semana 11 — Testes de integração com Spring Boot

Projeto Spring Boot 3 com Spring Data JPA e H2 em memória, sem mocks:

- **Exercício 1 (`ProdutoServiceIntegrationTest`):** cadastro válido (CT01), nome em branco sem gravar nada (CT02) e atualização de preço conferida no banco (CT03).
- **Exercício 2 (`ProdutoControllerIntegrationTest`):** `POST /produtos` (CT04) e `GET /produtos/{id}` com o ID extraído da resposta (CT05).

O [README da pasta](semana-11/README.md) traz a evidência da execução, a explicação do isolamento dos testes e as respostas das perguntas de discussão. Para executar: `mvn test` dentro de `semana-11`. Os 5 testes passam.

## Semana 14 — Teste de usuário final

Teste de usabilidade do fluxo de busca, filtro e simulação de compra de um fone Bluetooth no Mercado Livre. André foi o usuário final e Pedro, o avaliador.

O documento ([PDF](semana-14/TESTE%20DE%20USU%C3%81RIO%20FINAL.pdf) e Word) traz:

- objetivo, cenário, participantes e as 7 tarefas;
- planilha de observação com tempo, sucesso, erros e observações de cada tarefa;
- resultados: 51 segundos no total, 100% de sucesso sem ajuda e nenhum erro do usuário;
- critérios de sucesso atingidos: produto adicionado ao carrinho, e tempo abaixo de 4 minutos;
- questionário SUS com pontuação de 87,5 (Excelente);
- conclusão, com o problema de usabilidade encontrado e a limitação de ter um único participante.
