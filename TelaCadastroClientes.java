package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaCadastroClientes extends JDialog {
    private JTextField txtNome, txtEndereco, txtCep, txtEstado, txtPais, txtTelefone, txtEmail, txtDataNascimento;

    public TelaCadastroClientes(JFrame parent) {
        super(parent, "Cadastro de Clientes - Gerenciamento de Pousada", true); // Modo modal
        setSize(800, 600);  // Tamanho padrão da janela
        getContentPane().setBackground(Color.DARK_GRAY);  // Fundo escuro
        setLayout(new BorderLayout());
        configurarComponentes();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        JPanel painelCadastro = new JPanel(new GridLayout(9, 2, 20, 20));  // GridLayout com espaçamento
        painelCadastro.setBackground(Color.DARK_GRAY);
        painelCadastro.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));  // Margem interna

        Color corTexto = Color.WHITE;
        Color corCampo = Color.BLACK;

        JLabel lblNome = criarLabel("Nome:", corTexto);
        txtNome = criarCampoTexto(corCampo, corTexto);

        JLabel lblEndereco = criarLabel("Endereço:", corTexto);
        txtEndereco = criarCampoTexto(corCampo, corTexto);

        JLabel lblCep = criarLabel("CEP:", corTexto);
        txtCep = criarCampoTexto(corCampo, corTexto);

        JLabel lblEstado = criarLabel("Estado:", corTexto);
        txtEstado = criarCampoTexto(corCampo, corTexto);

        JLabel lblPais = criarLabel("País:", corTexto);
        txtPais = criarCampoTexto(corCampo, corTexto);

        JLabel lblTelefone = criarLabel("Telefone:", corTexto);
        txtTelefone = criarCampoTexto(corCampo, corTexto);

        JLabel lblEmail = criarLabel("E-mail:", corTexto);
        txtEmail = criarCampoTexto(corCampo, corTexto);

        JLabel lblDataNascimento = criarLabel("Data de Nascimento (YYYY-MM-DD):", corTexto);
        txtDataNascimento = criarCampoTexto(corCampo, corTexto);

        // Adicionando os componentes ao painel
        painelCadastro.add(lblNome);
        painelCadastro.add(txtNome);
        painelCadastro.add(lblEndereco);
        painelCadastro.add(txtEndereco);
        painelCadastro.add(lblCep);
        painelCadastro.add(txtCep);
        painelCadastro.add(lblEstado);
        painelCadastro.add(txtEstado);
        painelCadastro.add(lblPais);
        painelCadastro.add(txtPais);
        painelCadastro.add(lblTelefone);
        painelCadastro.add(txtTelefone);
        painelCadastro.add(lblEmail);
        painelCadastro.add(txtEmail);
        painelCadastro.add(lblDataNascimento);
        painelCadastro.add(txtDataNascimento);

        add(painelCadastro, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.DARK_GRAY);

        JButton btnSalvar = criarBotao("Salvar", corTexto, Color.GRAY);
        JButton btnCancelar = criarBotao("Cancelar", corTexto, Color.GRAY);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações dos botões
        btnSalvar.addActionListener(e -> salvarCliente());
        btnCancelar.addActionListener(e -> dispose());  // Fecha o diálogo ao cancelar
    }

    private JLabel criarLabel(String texto, Color cor) {
        JLabel label = new JLabel(texto);
        label.setForeground(cor);
        label.setFont(new Font("Tahoma", Font.PLAIN, 24));  // Fonte maior e consistente
        return label;
    }

    private JTextField criarCampoTexto(Color corFundo, Color corTexto) {
        JTextField campoTexto = new JTextField();
        campoTexto.setBackground(corFundo);
        campoTexto.setForeground(corTexto);
        campoTexto.setFont(new Font("Tahoma", Font.PLAIN, 20));  // Fonte maior para os campos de texto
        return campoTexto;
    }

    private JButton criarBotao(String texto, Color corTexto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Tahoma", Font.PLAIN, 28));  // Botões com estilo e fonte maiores
        botao.setFocusPainted(false);
        return botao;
    }

    private void salvarCliente() {
        String nome = txtNome.getText();
        String endereco = txtEndereco.getText();
        String cep = txtCep.getText();
        String estado = txtEstado.getText();
        String pais = txtPais.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();
        String dataNascimento = txtDataNascimento.getText();

        if (nome.isEmpty() || endereco.isEmpty() || cep.isEmpty() || estado.isEmpty() ||
            pais.isEmpty() || telefone.isEmpty() || email.isEmpty() || dataNascimento.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "INSERT INTO usuarios (nome, endereco, cep, estado, pais, telefone, email, data_nascimento) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, nome);
                stmt.setString(2, endereco);
                stmt.setString(3, cep);
                stmt.setString(4, estado);
                stmt.setString(5, pais);
                stmt.setString(6, telefone);
                stmt.setString(7, email);
                stmt.setString(8, dataNascimento);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Cliente cadastrado com sucesso!");
                limparCampos();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar o cliente: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtEndereco.setText("");
        txtCep.setText("");
        txtEstado.setText("");
        txtPais.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        txtDataNascimento.setText("");
    }

    public static void main(String[] args) {
        // Para testar o diálogo
        JFrame frame = new JFrame("Teste - Tela Cadastro Clientes");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaCadastroClientes telaCadastroClientes = new TelaCadastroClientes(frame);
        telaCadastroClientes.setVisible(true); // Mostra o diálogo
    }
}
