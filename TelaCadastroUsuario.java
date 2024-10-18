package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TelaCadastroUsuario extends JDialog {

    private JTextField campoNome;
    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton botaoSalvar;
    private Connection conexao;

    public TelaCadastroUsuario(JFrame parent, Connection conexao) {
        super(parent, "Cadastro de Usuário", true);
        this.conexao = conexao;
        setSize(400, 300);
        setLayout(new GridBagLayout());
        setLocationRelativeTo(parent);
        configurarComponentes();
    }

    private void configurarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblNome = new JLabel("Nome:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(lblNome, gbc);

        campoNome = new JTextField(20);
        gbc.gridx = 1;
        add(campoNome, gbc);

        JLabel lblLogin = new JLabel("Login:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblLogin, gbc);

        campoLogin = new JTextField(20);
        gbc.gridx = 1;
        add(campoLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblSenha, gbc);

        campoSenha = new JPasswordField(20);
        gbc.gridx = 1;
        add(campoSenha, gbc);

        botaoSalvar = new JButton("Salvar");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(botaoSalvar, gbc);

        // Ação do botão salvar
        botaoSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        String nome = campoNome.getText();
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos os campos são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO user (nome, login, senha) VALUES (?, ?, ?)";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, nome);
            stmt.setString(2, login);
            stmt.setString(3, senha); // Em uma aplicação real, as senhas devem ser criptografadas!

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose(); // Fecha o diálogo após salvar
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao cadastrar usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

