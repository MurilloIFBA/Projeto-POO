package livraria.gui;

import livraria.dao.ClienteDAO;
import livraria.dao.LivroDAO;
import livraria.dao.VendaDAO;
import livraria.model.Cliente;
import livraria.model.Livro;
import livraria.model.Venda;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class VendaPanel extends JPanel {

    private final VendaDAO vendaDAO = new VendaDAO();
    private final LivroDAO livroDAO = new LivroDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final DefaultTableModel modeloTabela;
    private final JTable tabela;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public VendaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(
                new Object[]{"ID", "Cliente", "Livro", "Qtd", "Total", "Data"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAtualizar = new JButton("Atualizar lista");
        JButton btnRegistrar = new JButton("Registrar nova venda");

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRegistrar);
        add(painelBotoes, BorderLayout.SOUTH);

        btnAtualizar.addActionListener(e -> carregarVendas());
        btnRegistrar.addActionListener(e -> abrirFormularioVenda());

        carregarVendas();
    }

    private void carregarVendas() {
        try {
            modeloTabela.setRowCount(0);
            List<Venda> vendas = vendaDAO.listarTodos();
            for (Venda v : vendas) {
                modeloTabela.addRow(new Object[]{
                        v.getId(),
                        v.getCliente().getNome(),
                        v.getLivro().getTitulo(),
                        v.getQuantidade(),
                        String.format("R$ %.2f", v.total()),
                        v.getDataVenda() != null ? v.getDataVenda().format(FORMATO_DATA) : ""
                });
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar vendas", e);
        }
    }

    private void abrirFormularioVenda() {
        try {
            List<Livro> livros = livroDAO.listarTodos();
            List<Cliente> clientes = clienteDAO.listarTodos();

            if (livros.isEmpty() || clientes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "É preciso ter pelo menos um livro e um cliente cadastrados antes de registrar uma venda.",
                        "Cadastro incompleto", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<Livro> comboLivros = new JComboBox<>(livros.toArray(new Livro[0]));
            JComboBox<Cliente> comboClientes = new JComboBox<>(clientes.toArray(new Cliente[0]));
            JTextField campoQuantidade = new JTextField("1");

            JPanel painel = new JPanel(new GridLayout(3, 2, 8, 8));
            painel.add(new JLabel("Livro:"));
            painel.add(comboLivros);
            painel.add(new JLabel("Cliente:"));
            painel.add(comboClientes);
            painel.add(new JLabel("Quantidade:"));
            painel.add(campoQuantidade);

            int opcao = JOptionPane.showConfirmDialog(this, painel, "Registrar venda",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcao == JOptionPane.OK_OPTION) {
                int quantidade = Integer.parseInt(campoQuantidade.getText().trim());
                if (quantidade <= 0) {
                    JOptionPane.showMessageDialog(this, "A quantidade precisa ser maior que zero.",
                            "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Livro livroEscolhido = (Livro) comboLivros.getSelectedItem();
                Cliente clienteEscolhido = (Cliente) comboClientes.getSelectedItem();

                boolean sucesso = vendaDAO.registrar(new Venda(livroEscolhido, clienteEscolhido, quantidade));

                if (sucesso) {
                    JOptionPane.showMessageDialog(this, "Venda registrada com sucesso!");
                    carregarVendas();
                } else {
                    JOptionPane.showMessageDialog(this, "Estoque insuficiente para essa venda.",
                            "Venda não realizada", JOptionPane.WARNING_MESSAGE);
                }
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantidade precisa ser um número válido.",
                    "Dados inválidos", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarErro("Erro ao registrar venda", ex);
        }
    }

    private void mostrarErro(String titulo, Exception e) {
        JOptionPane.showMessageDialog(this, titulo + ":\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}