package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaConsultaCheck extends JDialog {  // Mudança para JDialog
    private JTable tabelaClientes;
    private JButton btnConsultar;

    public TelaConsultaCheck(JFrame parent) {
        super(parent, "Consulta de Check-In/Check-Out", true); // Modo modal
        setSize(800, 600);  // Define o tamanho da janela
        setLayout(new BorderLayout());
        inicializarComponentes();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void inicializarComponentes() {
        btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultarClientes());

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnConsultar);
        add(painelBotoes, BorderLayout.NORTH);

        tabelaClientes = new JTable();
        add(new JScrollPane(tabelaClientes), BorderLayout.CENTER);
    }

    private void consultarClientes() {
        try {
            List<String[]> clientes = BancoDadosUtil.obterClientesComStatus();
            String[] colunas = {"ID", "Nome", "Data Check-In", "Data Check-Out"};
            String[][] dados = clientes.toArray(new String[0][]);
            tabelaClientes.setModel(new javax.swing.table.DefaultTableModel(dados, colunas));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao consultar clientes: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método main para testar o diálogo
    public static void main(String[] args) {
        JFrame frame = new JFrame("Teste - Consulta Check-In/Check-Out");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaConsultaCheck telaConsultaCheck = new TelaConsultaCheck(frame);
        telaConsultaCheck.setVisible(true); // Mostra o diálogo
    }
}
