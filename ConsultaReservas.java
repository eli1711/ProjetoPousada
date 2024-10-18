package pousada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultaReservas extends JDialog {
    private JTable tabelaReservas;
    private DefaultTableModel modeloTabela;

    public ConsultaReservas(JFrame parent) {
        super(parent, "Consulta de Reservas", true);
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(parent);

        configurarComponentes();
        carregarReservas();
    }

    private void configurarComponentes() {
        // Configuração da tabela de reservas
        modeloTabela = new DefaultTableModel();
        modeloTabela.addColumn("ID Reserva");
        modeloTabela.addColumn("Cliente");
        modeloTabela.addColumn("Acomodacao");
        modeloTabela.addColumn("Data Check-In");
        modeloTabela.addColumn("Data Check-Out");
        modeloTabela.addColumn("Quantidade de Pessoas");

        tabelaReservas = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaReservas);
        add(scrollPane, BorderLayout.CENTER);

        // Painel para botões de editar e excluir
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEditarReserva = new JButton("Editar Reserva");
        JButton btnExcluirReserva = new JButton("Excluir Reserva");

        btnEditarReserva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editarReserva();
            }
        });

        btnExcluirReserva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                excluirReserva();
            }
        });

        painelBotoes.add(btnEditarReserva);
        painelBotoes.add(btnExcluirReserva);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void carregarReservas() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT r.id, u.nome, a.nome_quarto, r.data_entrada, r.data_saida, r.quantidade_pessoas " +
                         "FROM reservas r " +
                         "JOIN usuarios u ON r.id_usuario = u.id " +
                         "JOIN acomodacoes a ON r.id_acomodacao = a.id";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idReserva = rs.getInt("id");
                String nomeCliente = rs.getString("nome");
                String nomeAcomodacao = rs.getString("nome_quarto");
                String dataEntrada = rs.getString("data_entrada");
                String dataSaida = rs.getString("data_saida");
                int quantidadePessoas = rs.getInt("quantidade_pessoas");

                modeloTabela.addRow(new Object[]{idReserva, nomeCliente, nomeAcomodacao, dataEntrada, dataSaida, quantidadePessoas});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as reservas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarReserva() {
        int linhaSelecionada = tabelaReservas.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idReserva = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            TelaAdicionarReservas telaEditarReserva = new TelaAdicionarReservas((JFrame) getParent());
            telaEditarReserva.setVisible(true);
            dispose(); // Fechar a tela de consulta
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para editar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirReserva() {
        int linhaSelecionada = tabelaReservas.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idReserva = (int) modeloTabela.getValueAt(linhaSelecionada, 0);
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir esta reserva?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    String sql = "DELETE FROM reservas WHERE id = ?";
                    PreparedStatement stmt = conexao.prepareStatement(sql);
                    stmt.setInt(1, idReserva);
                    stmt.executeUpdate();

                    modeloTabela.removeRow(linhaSelecionada);
                    JOptionPane.showMessageDialog(this, "Reserva excluída com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao excluir a reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        ConsultaReservas telaConsultaReservas = new ConsultaReservas(frame);
        telaConsultaReservas.setVisible(true);
    }
}
