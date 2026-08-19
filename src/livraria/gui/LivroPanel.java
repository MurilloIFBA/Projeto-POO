package livraria.gui;

import livraria.dao.LivroDAO;
import livraria.model.Livro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Painel de gerenciamento de livros: lista em tabela, com botões para
 * cadastrar, editar e remover. Os formulários de entrada de dados usam
 * caixas de diálogo simples (JOptionPane), sem precisar de telas extras.
 */
public class LivroPanel extends JPanel {

    private final LivroDAO livroDAO = new LivroDAO();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public LivroPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Título", "Autor", "Preço", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabela somente leitura, edição é via diálogo
            }
        };
        tabela = new JTable(modeloTabela);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnCadastrar = new JButton("Cadastrar");
        JButton btnEditar = new JButton("Editar selecionado");
        JButton btnRemover = new JButton("Remover selecionado");

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnCadastrar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnRemover);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarLivros());
        btnCadastrar.addActionListener(e -> abrirFormularioCadastro());
        btnEditar.addActionListener(e -> abrirFormularioEdicao());
        btnRemover.addActionListener(e -> removerSelecionado());

        carregarLivros();
    }

    private void carregarLivros() {
        try {
            modeloTabela.setRowCount(0);
            List<Livro> livros = livroDAO.listarTodos();
            for (Livro l : livros) {
                modeloTabela.addRow(new Object[]{
                        l.getId(), l.getTitulo(), l.getAutor(), l.getPreco(), l.getEstoque()
                });
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar livros", e);
        }
    }

    private void abrirFormularioCadastro() {
        JTextField campoTitulo = new JTextField();
        JTextField campoAutor = new JTextField();
        JTextField campoPreco = new JTextField();
        JTextField campoEstoque = new JTextField();

        JPanel painel = montarFormulario(campoTitulo, campoAutor, campoPreco, campoEstoque);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Cadastrar livro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                String titulo = campoTitulo.getText().trim();
                String autor = campoAutor.getText().trim();
                double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
                int estoque = Integer.parseInt(campoEstoque.getText().trim());

                if (titulo.isEmpty() || autor.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Título e autor são obrigatórios.",
                            "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                livroDAO.inserir(new Livro(titulo, autor, preco, estoque));
                carregarLivros();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço e estoque precisam ser números válidos.",
                        "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                mostrarErro("Erro ao cadastrar livro", ex);
            }
        }
    }

    private void abrirFormularioEdicao() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela primeiro.",
                    "Nenhum livro selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);

        JTextField campoTitulo = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        JTextField campoAutor = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        JTextField campoPreco = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 3)));
        JTextField campoEstoque = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 4)));

        JPanel painel = montarFormulario(campoTitulo, campoAutor, campoPreco, campoEstoque);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar livro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                String titulo = campoTitulo.getText().trim();
                String autor = campoAutor.getText().trim();
                double preco = Double.parseDouble(campoPreco.getText().trim().replace(",", "."));
                int estoque = Integer.parseInt(campoEstoque.getText().trim());

                livroDAO.atualizar(new Livro(id, titulo, autor, preco, estoque));
                carregarLivros();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Preço e estoque precisam ser números válidos.",
                        "Dados inválidos", JOptionPane.WARNING_MESSAGE);
            } catch (SQLException ex) {
                mostrarErro("Erro ao atualizar livro", ex);
            }
        }
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um livro na tabela primeiro.",
                    "Nenhum livro selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);
        String titulo = String.valueOf(modeloTabela.getValueAt(linha, 1));

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Remover o livro \"" + titulo + "\"?", "Confirmar remoção",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                livroDAO.remover(id);
                carregarLivros();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this,
                            "Não é possível remover este livro porque ele já possui vendas registradas.",
                            "Remoção não permitida", JOptionPane.WARNING_MESSAGE);
                } else {
                    mostrarErro("Erro ao remover livro", ex);
                }
            }
        }
    }

    private JPanel montarFormulario(JTextField titulo, JTextField autor, JTextField preco, JTextField estoque) {
        JPanel painel = new JPanel(new GridLayout(4, 2, 8, 8));
        painel.add(new JLabel("Título:"));
        painel.add(titulo);
        painel.add(new JLabel("Autor:"));
        painel.add(autor);
        painel.add(new JLabel("Preço:"));
        painel.add(preco);
        painel.add(new JLabel("Estoque:"));
        painel.add(estoque);
        return painel;
    }

    private void mostrarErro(String titulo, Exception e) {
        JOptionPane.showMessageDialog(this, titulo + ":\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}