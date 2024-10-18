package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaFecharConta extends JDialog {
    private JComboBox<String> comboClientes;
    private JTextArea txtAreaResumo;
    private JButton btnCalcularTotal;
    private JButton btnPagar;

    public TelaFecharConta(JFrame parent) {
        super(parent, "Fechar Conta", true); // Modo modal
        setSize(400, 300);  // Tamanho padrão da janela
        getContentPane().setBackground(Color.DARK_GRAY);  
        setLayout(new BorderLayout());

        configurarComponentes();
        carregarClientesComCheckout();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        JPanel painelPrincipal = new JPanel(new GridLayout(5, 1, 10, 10));  // Alterado para 5 linhas
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // Margem interna

        JLabel lblCliente = new JLabel("Selecionar Cliente:");
        comboClientes = new JComboBox<>();

        txtAreaResumo = new JTextArea();
        txtAreaResumo.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(txtAreaResumo);

        btnCalcularTotal = new JButton("Calcular Total");
        btnCalcularTotal.addActionListener(e -> calcularValorTotal());

        btnPagar = new JButton("Pagar Conta");
        btnPagar.addActionListener(e -> pagarConta());

        // Adicionando os componentes ao painel
        painelPrincipal.add(lblCliente);
        painelPrincipal.add(comboClientes);
        painelPrincipal.add(scrollPane);
        painelPrincipal.add(btnCalcularTotal);
        painelPrincipal.add(btnPagar);

        add(painelPrincipal, BorderLayout.CENTER);
    }

    // Método para carregar apenas clientes que já fizeram o checkout
    private void carregarClientesComCheckout() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT DISTINCT u.id, u.nome FROM usuarios u JOIN reservas r ON u.id = r.id_usuario WHERE r.checkout IS NOT NULL";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                comboClientes.addItem(id + " - " + nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para calcular o valor total (diária + consumo) de um cliente
    private void calcularValorTotal() {
        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        if (clienteSelecionado != null) {
            int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

            double valorTotalGeral = calcularValorTotalConta(idCliente);
            txtAreaResumo.setText("Total a pagar: R$ " + String.format("%.2f", valorTotalGeral));
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para calcular o valor total da conta
    private double calcularValorTotalConta(int idCliente) {
        double valorTotal = 0.0;
        try (Connection conexao = ConexaoBanco.getConnection()) {
            // Obter valor da acomodação (diária)
            String sqlAcomodacao = "SELECT valor_total FROM reservas WHERE id_usuario = ? AND checkout IS NOT NULL";
            PreparedStatement stmtAcomodacao = conexao.prepareStatement(sqlAcomodacao);
            stmtAcomodacao.setInt(1, idCliente);
            ResultSet rsAcomodacao = stmtAcomodacao.executeQuery();

            if (rsAcomodacao.next()) {
                valorTotal += rsAcomodacao.getDouble("valor_total");
            }

            // Obter valor dos consumos
            String sqlConsumo = "SELECT SUM(valor_total) AS total_consumo FROM consumo WHERE id_reserva IN (SELECT id FROM reservas WHERE id_usuario = ? AND checkout IS NOT NULL)";
            PreparedStatement stmtConsumo = conexao.prepareStatement(sqlConsumo);
            stmtConsumo.setInt(1, idCliente);
            ResultSet rsConsumo = stmtConsumo.executeQuery();

            if (rsConsumo.next()) {
                valorTotal += rsConsumo.getDouble("total_consumo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return valorTotal;
    }

    // Método para pagar a conta do cliente e salvar no histórico
    private void pagarConta() {
        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        if (clienteSelecionado != null) {
            int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

            try (Connection conexao = ConexaoBanco.getConnection()) {
                // Calcula o valor total
                double valorTotalGeral = calcularValorTotalConta(idCliente);

                // Registrar no histórico
                registrarHistorico(idCliente, conexao);

                // Excluir as informações das tabelas reservas e consumo
                excluirDadosReservaEConsumo(idCliente, conexao);

                JOptionPane.showMessageDialog(this, "Conta paga com sucesso! Total: R$ " + String.format("%.2f", valorTotalGeral));
                txtAreaResumo.setText("");  // Limpa o resumo após o pagamento
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao processar o pagamento: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para pagar a conta.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para registrar informações no histórico
    private void registrarHistorico(int idCliente, Connection conexao) throws SQLException {
    	String sql = "INSERT INTO Historico (id_cliente, id_acomodacao, data_checkin, data_checkout, consumo_total, valor_diaria) " +
                "SELECT r.id_usuario, r.id_acomodacao, r.data_entrada, r.data_saida, " +
                "IFNULL(SUM(c.valor_total), 0), " +
                "(DATEDIFF(r.data_saida, r.data_entrada) * a.valor_diaria) AS valor_total_hospedagem " +
                "FROM reservas r " +
                "JOIN acomodacoes a ON r.id_acomodacao = a.id " +
                "LEFT JOIN consumo c ON r.id = c.id_reserva " +
                "WHERE r.id_usuario = ? AND r.checkout IS NOT NULL " +
                "GROUP BY r.id";


        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }

    // Método para excluir os dados das tabelas de reserva e consumo
    private void excluirDadosReservaEConsumo(int idCliente, Connection conexao) throws SQLException {
        // Exclui primeiro o consumo, pois depende da reserva
        String sqlExcluirConsumo = "DELETE FROM consumo WHERE id_reserva IN (SELECT id FROM reservas WHERE id_usuario = ?)";
        try (PreparedStatement stmtExcluirConsumo = conexao.prepareStatement(sqlExcluirConsumo)) {
            stmtExcluirConsumo.setInt(1, idCliente);
            stmtExcluirConsumo.executeUpdate();
        }

        // Exclui a reserva após o consumo
        String sqlExcluirReserva = "DELETE FROM reservas WHERE id_usuario = ?";
        try (PreparedStatement stmtExcluirReserva = conexao.prepareStatement(sqlExcluirReserva)) {
            stmtExcluirReserva.setInt(1, idCliente);
            stmtExcluirReserva.executeUpdate();
        }
    }

    public static void main(String[] args) {
        // Para testar, abrir o diálogo dentro de um JFrame
        JFrame frame = new JFrame("Teste - Fechar Conta");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaFecharConta telaFecharConta = new TelaFecharConta(frame);
        telaFecharConta.setVisible(true); // Mostra o diálogo
    }
}
