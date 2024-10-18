package pousada;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

public class TelaCheckin extends JDialog {
    private JTable tabelaReservas;
    private DefaultTableModel modeloTabelaReservas;
    private JButton btnConfirmarCheckin;
    private JDateChooser dateChooserCheckin;  // Componente para escolher a data do check-in

    public TelaCheckin(JFrame parent) {
        super(parent, "Realizar Check-In", true); // Modo modal
        setSize(800, 600);  // Define o tamanho da janela
        setLayout(new BorderLayout());
        configurarComponentes();
        carregarReservas();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        // Painel principal da tela
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Modelo da tabela de reservas
        modeloTabelaReservas = new DefaultTableModel();
        modeloTabelaReservas.addColumn("ID");
        modeloTabelaReservas.addColumn("Cliente");
        modeloTabelaReservas.addColumn("Acomodação");
        modeloTabelaReservas.addColumn("Data Reserva");
        modeloTabelaReservas.addColumn("Quantidade de Pessoas");
        modeloTabelaReservas.addColumn("Valor Total");

        // Tabela para exibir reservas
        tabelaReservas = new JTable(modeloTabelaReservas);
        JScrollPane scrollPane = new JScrollPane(tabelaReservas);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Componente para escolher a data de check-in
        JPanel painelDataCheckin = new JPanel();
        painelDataCheckin.add(new JLabel("Data de Check-In:"));
        dateChooserCheckin = new JDateChooser();
        dateChooserCheckin.setDateFormatString("yyyy-MM-dd");
        painelDataCheckin.add(dateChooserCheckin);
        painelPrincipal.add(painelDataCheckin, BorderLayout.NORTH);

        // Botão para confirmar check-in
        btnConfirmarCheckin = new JButton("Confirmar Check-In");
        btnConfirmarCheckin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCheckin();
            }
        });

        // Adicionando componentes ao painel
        painelPrincipal.add(btnConfirmarCheckin, BorderLayout.SOUTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void carregarReservas() {
        // Conexão ao banco de dados para buscar as reservas
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT r.id, u.nome AS cliente, a.nome_quarto AS acomodacao, r.data_entrada, r.quantidade_pessoas, r.valor_total " +
                         "FROM reservas r " +
                         "JOIN usuarios u ON r.id_usuario = u.id " +
                         "JOIN acomodacoes a ON r.id_acomodacao = a.id " +
                         "WHERE r.checkin IS NULL"; // Mostrar apenas reservas sem check-in realizado
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Limpar a tabela antes de carregar novos dados
            modeloTabelaReservas.setRowCount(0); 

            // Carregar os dados na tabela
            while (rs.next()) {
                modeloTabelaReservas.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("acomodacao"),
                    rs.getDate("data_entrada"),
                    rs.getInt("quantidade_pessoas"),
                    rs.getDouble("valor_total")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar reservas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarCheckin() {
        int linhaSelecionada = tabelaReservas.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idReserva = (int) modeloTabelaReservas.getValueAt(linhaSelecionada, 0); // Obter o ID da reserva selecionada

            // Verificar se uma data de check-in foi selecionada
            if (dateChooserCheckin.getDate() != null) {
                java.util.Date dataCheckin = dateChooserCheckin.getDate();
                java.sql.Date dataCheckinSQL = new java.sql.Date(dataCheckin.getTime()); // Converter para SQL Date

                // Confirmar check-in
                int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja confirmar o Check-In para esta reserva?", "Confirmar Check-In", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    try (Connection conexao = ConexaoBanco.getConnection()) {
                        // Atualizar a reserva para incluir a data de check-in
                        String sqlUpdate = "UPDATE reservas SET checkin = ? WHERE id = ?";
                        PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdate);
                        stmtUpdate.setDate(1, dataCheckinSQL);
                        stmtUpdate.setInt(2, idReserva);

                        stmtUpdate.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Check-In realizado com sucesso!");
                        modeloTabelaReservas.removeRow(linhaSelecionada); // Remove a linha da tabela após o check-in
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Erro ao realizar Check-In: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma data de Check-In.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para fazer o Check-In.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Para testar o diálogo
        JFrame frame = new JFrame("Teste - Tela Check-In");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaCheckin telaCheckin = new TelaCheckin(frame);
        telaCheckin.setVisible(true); // Mostra o diálogo
    }
}
