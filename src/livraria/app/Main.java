package livraria.app;

import livraria.dao.ClienteDAO;
import livraria.dao.LivroDAO;
import livraria.dao.VendaDAO;
import livraria.model.Cliente;
import livraria.model.Livro;
import livraria.model.Venda;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner sc = new Scanner(System.in);
    private static final LivroDAO livroDAO = new LivroDAO();
    private static final ClienteDAO clienteDAO = new ClienteDAO();
    private static final VendaDAO vendaDAO = new VendaDAO();

    public static void main(String[] args) {
        int opcao;
        do {
            System.out.println("""
                    \n===== LIVRARIA =====
                    1 - Gerenciar Livros
                    2 - Gerenciar Clientes
                    3 - Registrar/Listar Vendas
                    0 - Sair
                    """);
            opcao = lerInt("Escolha uma opção: ");
            try {
                switch (opcao) {
                    case 1 -> menuLivros();
                    case 2 -> menuClientes();
                    case 3 -> menuVendas();
                    case 0 -> System.out.println("Saindo... até mais!");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (SQLException e) {
                System.out.println("Erro de banco de dados: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static void menuLivros() throws SQLException {
        System.out.println("1-Listar 2-Cadastrar 3-Atualizar 4-Remover");
        switch (lerInt("Opção: ")) {
            case 1 -> livroDAO.listarTodos().forEach(System.out::println);
            case 2 -> {
                String titulo = lerTexto("Título: ");
                String autor = lerTexto("Autor: ");
                double preco = lerDouble("Preço: ");
                int estoque = lerInt("Estoque: ");
                livroDAO.inserir(new Livro(titulo, autor, preco, estoque));
                System.out.println("Cadastrado!");
            }
            case 3 -> {
                int id = lerInt("ID: ");
                String titulo = lerTexto("Novo título: ");
                String autor = lerTexto("Novo autor: ");
                double preco = lerDouble("Novo preço: ");
                int estoque = lerInt("Novo estoque: ");
                livroDAO.atualizar(new Livro(id, titulo, autor, preco, estoque));
                System.out.println("Atualizado!");
            }
            case 4 -> {
                livroDAO.remover(lerInt("ID a remover: "));
                System.out.println("Removido!");
            }
        }
    }

    private static void menuClientes() throws SQLException {
        System.out.println("1-Listar 2-Cadastrar 3-Atualizar 4-Remover");
        switch (lerInt("Opção: ")) {
            case 1 -> clienteDAO.listarTodos().forEach(System.out::println);
            case 2 -> {
                String nome = lerTexto("Nome: ");
                String email = lerTexto("Email: ");
                clienteDAO.inserir(new Cliente(nome, email));
                System.out.println("Cadastrado!");
            }
            case 3 -> {
                int id = lerInt("ID: ");
                String nome = lerTexto("Novo nome: ");
                String email = lerTexto("Novo email: ");
                clienteDAO.atualizar(new Cliente(id, nome, email));
                System.out.println("Atualizado!");
            }
            case 4 -> {
                clienteDAO.remover(lerInt("ID a remover: "));
                System.out.println("Removido!");
            }
        }
    }

    private static void menuVendas() throws SQLException {
        System.out.println("1-Listar 2-Registrar venda");
        switch (lerInt("Opção: ")) {
            case 1 -> vendaDAO.listarTodos().forEach(System.out::println);
            case 2 -> {
                int livroId = lerInt("ID do livro: ");
                Livro livro = livroDAO.buscarPorId(livroId);
                if (livro == null) { System.out.println("Livro não encontrado."); return; }

                int clienteId = lerInt("ID do cliente: ");
                Cliente cliente = clienteDAO.buscarPorId(clienteId);
                if (cliente == null) { System.out.println("Cliente não encontrado."); return; }

                int quantidade = lerInt("Quantidade: ");
                boolean sucesso = vendaDAO.registrar(new Venda(livro, cliente, quantidade));
                System.out.println(sucesso ? "Venda registrada!" : "Estoque insuficiente.");
            }
        }
    }

    private static int lerInt(String msg) {
        System.out.print(msg);
        while (!sc.hasNextInt()) { System.out.print("Digite um número: "); sc.next(); }
        int v = sc.nextInt(); sc.nextLine();
        return v;
    }

    private static double lerDouble(String msg) {
        System.out.print(msg);
        while (!sc.hasNextDouble()) { System.out.print("Digite um valor: "); sc.next(); }
        double v = sc.nextDouble(); sc.nextLine();
        return v;
    }

    private static String lerTexto(String msg) {
        System.out.print(msg);
        return sc.nextLine();
    }
}