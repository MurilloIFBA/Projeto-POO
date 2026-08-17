package livraria.dao;

import livraria.config.ConexaoFactory;
import livraria.model.Livro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    private Connection conectar() throws SQLException {
        return ConexaoFactory.conectar();
    }

    public void inserir(Livro livro) throws SQLException {
        String sql = "INSERT INTO livros (titulo, autor, preco, estoque) VALUES (?, ?, ?, ?)";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setDouble(3, livro.getPreco());
            stmt.setInt(4, livro.getEstoque());
            stmt.executeUpdate();
        }
    }

    public void atualizar(Livro livro) throws SQLException {
        String sql = "UPDATE livros SET titulo=?, autor=?, preco=?, estoque=? WHERE id=?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setDouble(3, livro.getPreco());
            stmt.setInt(4, livro.getEstoque());
            stmt.setInt(5, livro.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM livros WHERE id=?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Livro buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, titulo, autor, preco, estoque FROM livros WHERE id=?";
        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Livro(rs.getInt("id"), rs.getString("titulo"),
                            rs.getString("autor"), rs.getDouble("preco"), rs.getInt("estoque"));
                }
                return null;
            }
        }
    }

    public List<Livro> listarTodos() throws SQLException {
        String sql = "SELECT id, titulo, autor, preco, estoque FROM livros ORDER BY titulo";
        List<Livro> livros = new ArrayList<>();

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                livros.add(new Livro(rs.getInt("id"), rs.getString("titulo"),
                        rs.getString("autor"), rs.getDouble("preco"), rs.getInt("estoque")));
            }
        }
        return livros;
    }
}