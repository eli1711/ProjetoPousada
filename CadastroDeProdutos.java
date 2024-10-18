package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class CadastroDeProdutos extends JDialog {

    private JTextField txtNomeProduto;
    private JTextField txtPreco;
    private JButton btnSalvar;
    private JButton btnCancelar;

    // Construtor
    public CadastroDeProdutos(Frame parent) {
        super(parent, "Cadastro de Produtos", true);
        initComponents();  // Inicializa os componentes
    }

    // Inicializar os componentes da interface
    private void initComponents() {
        // Inicializa os rótulos e campos de texto
        JLabel lblNomeProduto = new JLabel("Nome do Produto:");
        txtNomeProduto = new JTextField(20);

        JLabel lblPreco = new JLabel("Preço (ex: 12.50):");
        txtPreco = new JTextField(10);

        // Inicializa os botões
        btnSalvar = new JButton("Salvar");
        btnCancelar = new JButton("Cancelar");

        // Ações dos botões
        btnSalvar.addActionListener(this::salvarProduto);  // Usando method reference para maior clareza
        btnCancelar.addActionListener(e -> dispose());

        // Painel para organizar os componentes
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(lblNomeProduto);
        panel.add(txtNomeProduto);
        panel.add(lblPreco);
        panel.add(txtPreco);
        panel.add(btnSalvar);
        panel.add(btnCancelar);

        // Adiciona o painel na janela
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getParent());  // Centraliza a janela
    }

    // Método para salvar o produto no banco de dados
    private void salvarProduto(ActionEvent e) {
        String nomeProduto = txtNomeProduto.getText().trim();  // Remove espaços em branco no início/fim
        String precoStr = txtPreco.getText().trim();  // Remove espaços em branco no início/fim

        // Validações de entrada
        if (nomeProduto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome do produto é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O preço é obrigatório!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Substitui vírgula por ponto para compatibilidade com o formato numérico
            precoStr = precoStr.replace(",", ".");
            double preco = Double.parseDouble(precoStr);  // Converte para double

            // Chama o método de inserção no banco de dados
            BancoDadosUtil.inserirProduto(nomeProduto, preco);
            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            limparCampos();  // Limpa os campos após salvar
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use o formato correto (ex: 12.50).", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar o produto no banco de dados!", "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();  // Exibe o stack trace para depuração
        }
    }

    // Método para limpar os campos após o cadastro
    private void limparCampos() {
        txtNomeProduto.setText("");
        txtPreco.setText("");
        txtNomeProduto.requestFocus();  // Foca no campo nome do produto para facilitar novo cadastro
    }
}
