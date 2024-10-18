package pousada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TelaConsultaAcomodacoes extends JDialog {  // Mudança para JDialog
    private DefaultTableModel modeloTabelaAcomodacoes;
    private JTable tabelaAcomodacoes;

    public TelaConsultaAcomodacoes(JFrame parent) {
        super(parent, "Consulta de Acomodações - Gerenciamento de Pousada", true); // Modo modal
        setSize(1000, 600);  // Define um tamanho padrão para a janela
        getContentPane().setBackground(Color.DARK_GRAY);
        setLayout(new BorderLayout());
        configurarComponentes();
        carregarAcomodacoes();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        // Tabela de Acomodações
        modeloTabelaAcomodacoes = new DefaultTableModel();
        modeloTabelaAcomodacoes.addColumn("ID");
        modeloTabelaAcomodacoes.addColumn("Número do Quarto");
        modeloTabelaAcomodacoes.addColumn("Nome do Quarto");
        modeloTabelaAcomodacoes.addColumn("Descrição");
        modeloTabelaAcomodacoes.addColumn("Valor Diária");
        modeloTabelaAcomodacoes.addColumn("Status");

        tabelaAcomodacoes = new JTable(modeloTabelaAcomodacoes);
        tabelaAcomodacoes.setBackground(Color.BLACK);
        tabelaAcomodacoes.setForeground(Color.WHITE);
        tabelaAcomodacoes.setFont(new Font("Arial", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(tabelaAcomodacoes);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de Botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new GridLayout(1, 3, 10, 10));  // Grade com 3 botões
        painelBotoes.setBackground(Color.DARK_GRAY);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Botões
        JButton btnVoltar = criarBotao("Voltar", Color.WHITE, Color.GRAY);
        JButton btnAtualizar = criarBotao("Atualizar", Color.WHITE, new Color(70, 130, 180));  // Azul para destaque
        JButton btnDeletar = criarBotao("Deletar Acomodação", Color.WHITE, Color.RED);

        // Adicionando ação aos botões
        btnVoltar.addActionListener(e -> dispose()); // Fecha o diálogo
        btnAtualizar.addActionListener(e -> carregarAcomodacoes());
        btnDeletar.addActionListener(e -> deletarAcomodacao());

        // Adicionando botões ao painel
        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnDeletar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto, Color corTexto, Color corFundo) {
        JButton botao = new JButton(texto);
        botao.setBackground(corFundo);
        botao.setForeground(corTexto);
        botao.setFont(new Font("Tahoma", Font.PLAIN, 28));
        botao.setFocusPainted(false);
        return botao;
    }

    private void carregarAcomodacoes() {
        // Limpar a tabela antes de carregar os dados
        modeloTabelaAcomodacoes.setRowCount(0);

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT * FROM acomodacoes";
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String numeroQuarto = rs.getString("numero_quarto");
                String nomeQuarto = rs.getString("nome_quarto");
                String descricao = rs.getString("descricao");
                double valorDiaria = rs.getDouble("valor_diaria");
                String status = rs.getString("status_disponibilidade");

                modeloTabelaAcomodacoes.addRow(new Object[]{id, numeroQuarto, nomeQuarto, descricao, valorDiaria, status});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as acomodações: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletarAcomodacao() {
        int linhaSelecionada = tabelaAcomodacoes.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idAcomodacao = (int) modeloTabelaAcomodacoes.getValueAt(linhaSelecionada, 0);
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja deletar esta acomodação?", "Confirmar Deleção", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    String sql = "DELETE FROM acomodacoes WHERE id = " + idAcomodacao;
                    Statement stmt = conexao.createStatement();
                    stmt.executeUpdate(sql);
                    modeloTabelaAcomodacoes.removeRow(linhaSelecionada);
                    JOptionPane.showMessageDialog(this, "Acomodação deletada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao deletar acomodação: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma acomodação para deletar.", "Nenhuma Seleção", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Para testar, abrir a janela dentro de um JFrame
        JFrame frame = new JFrame("Teste - Consulta de Acomodações");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaConsultaAcomodacoes telaConsultaAcomodacoes = new TelaConsultaAcomodacoes(frame);
        telaConsultaAcomodacoes.setVisible(true); // Mostra o diálogo
    }
}
