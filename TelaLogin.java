package pousada;



import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TelaLogin extends JFrame {

    private JTextField campoLogin;
    private JPasswordField campoSenha;
    private JButton botaoLogin;
    private Connection conexao;
    private BufferedImage imagemFundo;
    

    // Construtor que recebe a conexão existente
    public TelaLogin() {
        setTitle("Tela de Login - Gerenciamento de Pousada");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configurarComponentes();
        setLocationRelativeTo(null); // Centraliza a janela
        trocarIcone(); // Chama o método para trocar o ícone da janela
        obterConexao();
    }

    // Método para trocar o ícone da janela
    private void trocarIcone() {
        try {
            Image icone = ImageIO.read(getClass().getResource("/img/icone.png")); // Caminho para o ícone
            setIconImage(icone); // Define o ícone da janela
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar o ícone.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void obterConexao() {
        try {
            conexao = ConexaoBanco.getConnection();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void configurarComponentes() {
        // Carrega a imagem de fundo e aplica o desfoque
        try {
            imagemFundo = ImageIO.read(getClass().getResource("/img/ImagenLogin.jpg")); // Coloque o caminho da sua imagem aqui
          
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Painel principal com a imagem de fundo
        JPanel painelFundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagemFundo != null) {
                    g.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        painelFundo.setLayout(new GridBagLayout()); // Usaremos o GridBagLayout para posicionar os componentes

        // Painel para os campos de login e senha, sobreposto à imagem
        JPanel painelLogin = new JPanel(new GridBagLayout());
        painelLogin.setOpaque(false); // Deixe o painel transparente

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Labels e campos de login e senha
        JLabel lblLogin = new JLabel("Login:");
        lblLogin.setForeground(Color.BLACK); // Define a cor do texto como branco para maior contraste
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        painelLogin.add(lblLogin, gbc);

        campoLogin = new JTextField(15);
        gbc.gridx = 1;
        painelLogin.add(campoLogin, gbc);

        JLabel lblSenha = new JLabel("Senha:");
        lblSenha.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        painelLogin.add(lblSenha, gbc);

        campoSenha = new JPasswordField(15);
        gbc.gridx = 1;
        painelLogin.add(campoSenha, gbc);

        // Botão de login
        botaoLogin = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        painelLogin.add(botaoLogin, gbc);

        // Adiciona ação ao botão de login
        botaoLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });

        // Adiciona o painel de login ao painel de fundo
        painelFundo.add(painelLogin, gbc);

        // Adiciona o painel de fundo à janela
        add(painelFundo, BorderLayout.CENTER);
    }

    private void autenticar() {
        String login = campoLogin.getText();
        String senha = new String(campoSenha.getPassword());

        if (validarCredenciaisNoBanco(login, senha)) {
            JOptionPane.showMessageDialog(this, "Login bem-sucedido!");
            TelaInicial telaInicial = new TelaInicial(conexao);
            telaInicial.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Login ou senha incorretos.", "Erro de Autenticação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCredenciaisNoBanco(String login, String senha) {
        boolean autenticado = false;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM user WHERE login = ? AND senha = ?";
            stmt = conexao.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, senha); // Em uma aplicação real, as senhas devem ser criptografadas!

            rs = stmt.executeQuery();

            if (rs.next()) {
                autenticado = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return autenticado;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }
}

