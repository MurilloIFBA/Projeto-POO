package livraria.gui;

import livraria.dao.ClienteDAO;
import livraria.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientePanel extends JPanel {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DefaultTableModel modeloTabela;
    private final JTable tabela;

    public ClientePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modeloTabela = new DefaultTableModel(new Object[]{"ID", "Nome", "Email"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
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

        btnAtualizar.addActionListener(e -> carregarClientes());
        btnCadastrar.addActionListener(e -> abrirFormularioCadastro());
        btnEditar.addActionListener(e -> abrirFormularioEdicao());
        btnRemover.addActionListener(e -> removerSelecionado());

        carregarClientes();
    }

    private void carregarClientes() {
        try {
            modeloTabela.setRowCount(0);
            List<Cliente> clientes = clienteDAO.listarTodos();
            for (Cliente c : clientes) {
                modeloTabela.addRow(new Object[]{c.getId(), c.getNome(), c.getEmail()});
            }
        } catch (SQLException e) {
            mostrarErro("Erro ao carregar clientes", e);
        }
    }

    private void abrirFormularioCadastro() {
        JTextField campoNome = new JTextField();
        JTextField campoEmail = new JTextField();
        JPanel painel = montarFormulario(campoNome, campoEmail);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Cadastrar cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();

            if (nome.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e email são obrigatórios.",
                        "Dados inválidos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                clienteDAO.inserir(new Cliente(nome, email));
                carregarClientes();
            } catch (SQLException ex) {
                mostrarErro("Erro ao cadastrar cliente", ex);
            }
        }
    }

    private void abrirFormularioEdicao() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.",
                    "Nenhum cliente selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);
        JTextField campoNome = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 1)));
        JTextField campoEmail = new JTextField(String.valueOf(modeloTabela.getValueAt(linha, 2)));
        JPanel painel = montarFormulario(campoNome, campoEmail);

        int opcao = JOptionPane.showConfirmDialog(this, painel, "Editar cliente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcao == JOptionPane.OK_OPTION) {
            try {
                clienteDAO.atualizar(new Cliente(id, campoNome.getText().trim(), campoEmail.getText().trim()));
                carregarClientes();
            } catch (SQLException ex) {
                mostrarErro("Erro ao atualizar cliente", ex);
            }
        }
    }

    private void removerSelecionado() {
        int linha = tabela.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.",
                    "Nenhum cliente selecionado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int id = (int) modeloTabela.getValueAt(linha, 0);
        String nome = String.valueOf(modeloTabela.getValueAt(linha, 1));

        int confirmacao = JOptionPane.showConfirmDialog(this,
                "Remover o cliente \"" + nome + "\"?", "Confirmar remoção",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                clienteDAO.remover(id);
                carregarClientes();
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    JOptionPane.showMessageDialog(this,
                            "Não é possível remover este cliente porque ele já possui vendas registradas.",
                            "Remoção não permitida", JOptionPane.WARNING_MESSAGE);
                } else {
                    mostrarErro("Erro ao remover cliente", ex);
                }
            }
        }
    }

    private JPanel montarFormulario(JTextField nome, JTextField email) {
        JPanel painel = new JPanel(new GridLayout(2, 2, 8, 8));
        painel.add(new JLabel("Nome:"));
        painel.add(nome);
        painel.add(new JLabel("Email:"));
        painel.add(email);
        return painel;
    }

    private void mostrarErro(String titulo, Exception e) {
        JOptionPane.showMessageDialog(this, titulo + ":\n" + e.getMessage(),
                "Erro", JOptionPane.ERROR_MESSAGE);
    }
}