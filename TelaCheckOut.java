package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class TelaCheckOut extends JDialog {
    private JTable tabelaCheckouts;
    private DefaultTableModel modeloTabelaCheckouts;
    private JButton btnConfirmarCheckout;
    private JDateChooser dateChooserCheckout;  // Componente para escolher a data do check-out

    public TelaCheckOut(JFrame parent) {
        super(parent, "Realizar Check-Out", true); // Modo modal
        setSize(800, 600);  // Define o tamanho da janela
        setLayout(new BorderLayout());
        configurarComponentes();
        carregarCheckouts();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        // Painel principal da tela
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Modelo da tabela de checkouts
        modeloTabelaCheckouts = new DefaultTableModel();
        modeloTabelaCheckouts.addColumn("ID");
        modeloTabelaCheckouts.addColumn("Cliente");
        modeloTabelaCheckouts.addColumn("Acomodação");
        modeloTabelaCheckouts.addColumn("Data Check-In");
        modeloTabelaCheckouts.addColumn("Quantidade de Pessoas");
        modeloTabelaCheckouts.addColumn("Valor Total");

        // Tabela para exibir os checkouts pendentes
        tabelaCheckouts = new JTable(modeloTabelaCheckouts);
        JScrollPane scrollPane = new JScrollPane(tabelaCheckouts);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Componente para escolher a data de check-out
        JPanel painelDataCheckout = new JPanel();
        painelDataCheckout.add(new JLabel("Data de Check-Out:"));
        dateChooserCheckout = new JDateChooser();
        dateChooserCheckout.setDateFormatString("yyyy-MM-dd");
        painelDataCheckout.add(dateChooserCheckout);
        painelPrincipal.add(painelDataCheckout, BorderLayout.NORTH);

        // Botão para confirmar check-out
        btnConfirmarCheckout = new JButton("Confirmar Check-Out");
        btnConfirmarCheckout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarCheckout();
            }
        });

        // Adicionando componentes ao painel
        painelPrincipal.add(btnConfirmarCheckout, BorderLayout.SOUTH);
        add(painelPrincipal, BorderLayout.CENTER);
    }

    private void carregarCheckouts() {
        // Conexão ao banco de dados para buscar as reservas com check-in realizado, mas sem check-out
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT r.id, u.nome AS cliente, a.nome_quarto AS acomodacao, r.checkin, r.quantidade_pessoas, r.valor_total " +
                         "FROM reservas r " +
                         "JOIN usuarios u ON r.id_usuario = u.id " +
                         "JOIN acomodacoes a ON r.id_acomodacao = a.id " +
                         "WHERE r.checkin IS NOT NULL AND r.checkout IS NULL"; // Mostrar apenas reservas sem check-out realizado
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // Limpar a tabela antes de carregar novos dados
            modeloTabelaCheckouts.setRowCount(0);

            // Carregar os dados na tabela
            while (rs.next()) {
                modeloTabelaCheckouts.addRow(new Object[] {
                    rs.getInt("id"),
                    rs.getString("cliente"),
                    rs.getString("acomodacao"),
                    rs.getDate("checkin"),
                    rs.getInt("quantidade_pessoas"),
                    rs.getDouble("valor_total")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar checkouts: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarCheckout() {
        int linhaSelecionada = tabelaCheckouts.getSelectedRow();
        if (linhaSelecionada != -1) {
            int idReserva = (int) modeloTabelaCheckouts.getValueAt(linhaSelecionada, 0); // Obter o ID da reserva selecionada

            // Verificar se uma data de check-out foi selecionada
            if (dateChooserCheckout.getDate() != null) {
                java.util.Date dataCheckout = dateChooserCheckout.getDate();
                java.sql.Date dataCheckoutSQL = new java.sql.Date(dataCheckout.getTime()); // Converter para SQL Date

                // Confirmar check-out
                int confirmacao = JOptionPane.showConfirmDialog(this, "Deseja confirmar o Check-Out para esta reserva?", "Confirmar Check-Out", JOptionPane.YES_NO_OPTION);
                if (confirmacao == JOptionPane.YES_OPTION) {
                    try (Connection conexao = ConexaoBanco.getConnection()) {
                        // Atualizar a reserva para incluir a data de check-out
                        String sqlUpdate = "UPDATE reservas SET checkout = ? WHERE id = ?";
                        PreparedStatement stmtUpdate = conexao.prepareStatement(sqlUpdate);
                        stmtUpdate.setDate(1, dataCheckoutSQL);
                        stmtUpdate.setInt(2, idReserva);
                        stmtUpdate.executeUpdate();

                        // Obter o ID da acomodação para liberar
                        String sqlAcomodacao = "SELECT id_acomodacao FROM reservas WHERE id = ?";
                        PreparedStatement stmtAcomodacao = conexao.prepareStatement(sqlAcomodacao);
                        stmtAcomodacao.setInt(1, idReserva);
                        ResultSet rsAcomodacao = stmtAcomodacao.executeQuery();

                        if (rsAcomodacao.next()) {
                            int idAcomodacao = rsAcomodacao.getInt("id_acomodacao");

                            // Atualizar o status da acomodação para "Disponível"
                            String sqlUpdateAcomodacao = "UPDATE acomodacoes SET status_disponibilidade = 'Disponível' WHERE id = ?";
                            PreparedStatement stmtUpdateAcomodacao = conexao.prepareStatement(sqlUpdateAcomodacao);
                            stmtUpdateAcomodacao.setInt(1, idAcomodacao);
                            stmtUpdateAcomodacao.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(this, "Check-Out realizado com sucesso!");
                        modeloTabelaCheckouts.removeRow(linhaSelecionada); // Remove a linha da tabela após o check-out
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Erro ao realizar Check-Out: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma data de Check-Out.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para fazer o Check-Out.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Para testar o diálogo
        JFrame frame = new JFrame("Teste - Tela Check-Out");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaCheckOut telaCheckOut = new TelaCheckOut(frame);
        telaCheckOut.setVisible(true); // Mostra o diálogo
    }
}
