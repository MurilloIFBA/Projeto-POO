package livraria.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Janela principal da aplicação, com uma aba para cada área do sistema.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Sistema de Livraria");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null); // centraliza na tela

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Livros", new LivroPanel());
        abas.addTab("Clientes", new ClientePanel());
        abas.addTab("Vendas", new VendaPanel());

        add(abas, BorderLayout.CENTER);
    }
}