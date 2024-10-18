package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaCadastroAcomodacoes extends JDialog {
    private JTextField txtNumeroQuarto, txtNomeQuarto, txtValor, txtDescricao;
    private JComboBox<String> comboStatus;

    public TelaCadastroAcomodacoes(JFrame parent) {
        super(parent, "Cadastro de Acomodações - Gerenciamento de Pousada", true); // Modo modal
        setSize(800, 600);  // Define um tamanho padrão para a janela
        configurarComponentes();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        JPanel painelFicha = new JPanel(new GridLayout(6, 2, 20, 20));  // Layout grade espaçado
        painelFicha.setBackground(Color.DARK_GRAY);
        painelFicha.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));  // Margem interna maior para estilo

        Color corTexto = Color.WHITE;
        Color corCampo = Color.BLACK;

        JLabel lblNumeroQuarto = criarLabel("Número do Quarto:", corTexto);
        txtNumeroQuarto = criarCampoTexto(corCampo, corTexto);

        JLabel lblNomeQuarto = criarLabel("Nome do Quarto:", corTexto);
        txtNomeQuarto = criarCampoTexto(corCampo, corTexto);

        JLabel lblValor = criarLabel("Valor Diária:", corTexto);
        txtValor = criarCampoTexto(corCampo, corTexto);

        JLabel lblDescricao = criarLabel("Descrição:", corTexto);
        txtDescricao = criarCampoTexto(corCampo, corTexto);

        JLabel lblStatus = criarLabel("Status:", corTexto);
        comboStatus = new JComboBox<>(new String[]{"Disponível", "Indisponível"});
        comboStatus.setBackground(corCampo);
        comboStatus.setForeground(corTexto);

        // Adicionar componentes ao painel
        painelFicha.add(lblNumeroQuarto);
        painelFicha.add(txtNumeroQuarto);
        painelFicha.add(lblNomeQuarto);
        painelFicha.add(txtNomeQuarto);
        painelFicha.add(lblValor);
        painelFicha.add(txtValor);
        painelFicha.add(lblDescricao);
        painelFicha.add(txtDescricao);
        painelFicha.add(lblStatus);
        painelFicha.add(comboStatus);

        add(painelFicha, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout());
        painelBotoes.setBackground(Color.DARK_GRAY);

        JButton btnSalvar = criarBotao("Salvar", Color.WHITE, Color.GRAY);
        JButton btnCancelar = criarBotao("Cancelar", Color.WHITE, Color.GRAY);

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnCancelar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ações dos botões
        btnSalvar.addActionListener(e -> salvarAcomodacao());
        btnCancelar.addActionListener(e -> dispose());  // Fecha o diálogo ao cancelar
    }

    private JLabel criarLabel(String texto, Color cor) {
        JLabel label = new JLabel(texto);
        label.setForeground(cor);
        label.setFont(new Font("Tahoma", Font.PLAIN, 24));  // Fonte maior
        return label;
    }

    private JTextField criarCampoTexto(Color corFundo, Color corTexto) {
        JTextField campoTexto = new JTextField();
        campoTexto.setBackground(corFundo);
        campoTexto.setForeground(corTexto);
        campoTexto.setFont(new Font("Tahoma", Font.PLAIN, 20));  // Fonte maior para campos de texto
        return campoTexto;
    }

    private JButton criarBotao(String texto, Color corTexto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Tahoma", Font.PLAIN, 28));  // Fonte grande nos botões
        botao.setFocusPainted(false);
        return botao;
    }

    private void salvarAcomodacao() {
        String numeroQuarto = txtNumeroQuarto.getText();
        String nomeQuarto = txtNomeQuarto.getText();
        String descricao = txtDescricao.getText();

        double valorDiaria;
        try {
            valorDiaria = Double.parseDouble(txtValor.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O valor da diária deve ser um número válido.");
            return;
        }

        String status = (String) comboStatus.getSelectedItem();

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "INSERT INTO acomodacoes (numero_quarto, nome_quarto, descricao, valor_diaria, status_disponibilidade) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                stmt.setString(1, numeroQuarto);
                stmt.setString(2, nomeQuarto);
                stmt.setString(3, descricao);
                stmt.setDouble(4, valorDiaria);
                stmt.setString(5, status);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Acomodação salva com sucesso!");
                limparCampos();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar a acomodação: " + ex.getMessage());
        }
    }

    private void limparCampos() {
        txtNumeroQuarto.setText("");
        txtNomeQuarto.setText("");
        txtDescricao.setText("");
        txtValor.setText("");
        comboStatus.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        // Para testar, abra a janela dentro de um JFrame
        JFrame frame = new JFrame("Teste - Tela Cadastro Acomodações");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);
        
        TelaCadastroAcomodacoes telaCadastroAcomodacoes = new TelaCadastroAcomodacoes(frame);
        telaCadastroAcomodacoes.setVisible(true); // Mostra o diálogo
    }
}
