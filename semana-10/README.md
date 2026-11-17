# Semana 10 — Práticas de TDD

**Alunos:**

- André Filipe Caetano Mulati — RA: 24011149-2
- Pedro Massaki Carnelossi — RA: 24036481-2

Projetos Maven com Java 17 e JUnit 5. Cada pasta é um projeto independente:

| Projeto | O que foi feito | Testes |
|---|---|---|
| `bubblesort-tdd` | Implementação do método `ordenar()` (Bubble Sort crescente). | 3 |
| `carrinho-compras-tdd` | Testes escritos para os requisitos 1 a 6 e implementação da classe `Carrinho`. Produto com preço negativo lança `IllegalArgumentException`; desconto de 10% a partir de R$ 500,00, testado em R$ 499,99, R$ 500,00 e R$ 500,01. | 9 |
| `conta-bancaria-tdd-maven` | Implementação de `depositar()` e `sacar()`; saque maior que o saldo não altera a conta. | 4 |
| `estoque-tdd` | Implementação de `adicionarEstoque()` e `retirarEstoque()`; incluído um teste para entrada igual a zero (requisito 6). | 7 |

## Executar

Dentro da pasta de cada projeto:

```bash
mvn test
```

Todos os testes passam (`BUILD SUCCESS`).
