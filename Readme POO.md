# Projeto-POO
Projeto Programação Orientada a Objetos
A livraria é o tema escolhido por envolver entidades do mundo real bem definidas — livros, clientes e vendas — o que torna o aprendizado de POO mais concreto e fácil de entender.
O sistema permite gerenciar uma livraria do zero: cadastrar o catálogo de livros, registrar clientes e controlar as vendas com baixa automática de estoque. Tudo isso com um banco de dados real por trás, garantindo que os dados não se percam quando o programa fecha.
O projeto está no início e a ideia é ir melhorando conforme aprendo mais na disciplina. Abaixo estão as etapas que planejo implementar:


Etapa 1 — Conectar Java com MySQL (JDBC)

Atualmente o sistema guarda os dados só na memória RAM, ou seja, quando fecha o programa perde tudo. A ideia é conectar as classes Java ao banco MySQL usando JDBC, para que os dados sejam salvos de verdade.

Adicionar dependência do MySQL Connector/J
Criar uma classe Conexao para gerenciar a conexão com o banco
Substituir as listas em memória por consultas SQL reais (INSERT, SELECT, UPDATE, DELETE)


O menu no terminal é funcional, mas não é muito amigável. A próxima versão terá uma interface gráfica com janelas, botões e tabelas para exibir os dados.


Tela de cadastro de livros e clientes
Tabela listando todos os registros
Botões de editar e excluir

Relatórios e histórico de vendas

Listar todas as vendas de um cliente específico
Ver quais livros estão com estoque baixo
Calcular o total faturado por período
