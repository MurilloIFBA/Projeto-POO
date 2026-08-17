package livraria.dao;

import livraria.config.ConexaoFactory;
import livraria.model.Cliente;
import livraria.model.Livro;
import livraria.model.Venda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VendaDAO {

    private Connection conectar() throws SQLException {
        return ConexaoFactory.conectar();
    }

    // Registra a venda e desconta o estoque. Retorna false se não houver
    // estoque suficiente (em vez de lançar uma exceção customizada, só
    // pra manter simples).
    public boolean registrar(Venda venda) throws SQLException {
        String sqlEstoque = "SELECT estoque FROM livros WHERE id = ?";
        String sqlInsere  = "INSERT INTO vendas (livro_id, cliente_id, quantidade) VALUES (?, ?, ?)";
        String sqlAtualizaEstoque = "UPDATE livros SET estoque = estoque - ? WHERE id = ?";

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false); // transação: ou tudo funciona, ou nada é gravado

            try (PreparedStatement stmt = conn.prepareStatement(sqlEstoque)) {
                stmt.setInt(1, venda.getLivro().getId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("estoque") < venda.getQuantidade()) {
                        conn.rollback();
                        return false; // estoque insuficiente
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlInsere)) {
                stmt.setInt(1, venda.getLivro().getId());
                stmt.setInt(2, venda.getCliente().getId());
                stmt.setInt(3, venda.getQuantidade());
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlAtualizaEstoque)) {
                stmt.setInt(1, venda.getQuantidade());
                stmt.setInt(2, venda.getLivro().getId());
                stmt.executeUpdate();
            }

            conn.commit();
            return true;
        }
    }

    public List<Venda> listarTodos() throws SQLException {
        String sql = "SELECT venda_id, cliente, livro, quantidade, preco_unitario, data_venda FROM vw_vendas ORDER BY data_venda DESC";
        List<Venda> vendas = new ArrayList<>();

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Livro livroResumo = new Livro(0, rs.getString("livro"), "", rs.getDouble("preco_unitario"), 0);
                Cliente clienteResumo = new Cliente(0, rs.getString("cliente"), "");
                vendas.add(new Venda(
                        rs.getInt("venda_id"), livroResumo, clienteResumo,
                        rs.getInt("quantidade"), rs.getTimestamp("data_venda").toLocalDateTime()));
            }
        }
        return vendas;
    }
}