package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaReserva extends JFrame {
    private JTextField campoNome;
    private JTextField campoEndereco;
    private JTextField campoDataCheckin;
    private JTextField campoQuantidadePessoas;
    private JComboBox<String> comboAcomodacao;

    public TelaReserva() {
        setTitle("Realizar Reserva");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        configurarComponentes();
    }

    private void configurarComponentes() {
        JPanel painel = new JPanel(new GridLayout(6, 2));
        
        painel.add(new JLabel("Nome:"));
        campoNome = new JTextField();
        painel.add(campoNome);
        
        painel.add(new JLabel("Endereço:"));
        campoEndereco = new JTextField();
        painel.add(campoEndereco);
        
        painel.add(new JLabel("Data Check-In (YYYY-MM-DD):"));
        campoDataCheckin = new JTextField();
        painel.add(campoDataCheckin);
        
        painel.add(new JLabel("Quantidade de Pessoas:"));
        campoQuantidadePessoas = new JTextField();
        painel.add(campoQuantidadePessoas);
        
        painel.add(new JLabel("Acomodação:"));
        comboAcomodacao = new JComboBox<>(new String[] {"Quarto 1", "Quarto 2", "Quarto 3"}); // Altere com seus dados
        painel.add(comboAcomodacao);
        
        JButton btnReservar = new JButton("Reservar");
        btnReservar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarReserva();
            }
        });
        
        painel.add(btnReservar);
        
        add(painel);
    }

    private void realizarReserva() {
        String nome = campoNome.getText();
        String endereco = campoEndereco.getText();
        String dataCheckin = campoDataCheckin.getText();
        int quantidadePessoas;
        String acomodacao = (String) comboAcomodacao.getSelectedItem();
        double valorTotal = calcularValorTotal(acomodacao); // Calcule o valor com base na acomodação

        try {
            quantidadePessoas = Integer.parseInt(campoQuantidadePessoas.getText());
            if (quantidadePessoas <= 0) {
                showError("A quantidade de pessoas deve ser maior que zero.");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Quantidade de pessoas deve ser um número válido.");
            return;
        }

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "INSERT INTO usuarios (nome, endereco) VALUES (?, ?)";
            PreparedStatement stmtUsuario = conexao.prepareStatement(sql);
            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, endereco);
            stmtUsuario.executeUpdate();
            
            // Obter o ID do usuário recém-criado
            String sqlIdUsuario = "SELECT LAST_INSERT_ID()";
            PreparedStatement stmtIdUsuario = conexao.prepareStatement(sqlIdUsuario);
            ResultSet rs = stmtIdUsuario.executeQuery();
            int idUsuario = 0;
            if (rs.next()) {
                idUsuario = rs.getInt(1);
            }

            // Criar reserva
            String sqlReserva = "INSERT INTO reservas (id_usuario, id_acomodacao, data_checkin, quantidade_pessoas, valor_total) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtReserva = conexao.prepareStatement(sqlReserva);
            stmtReserva.setInt(1, idUsuario);
            stmtReserva.setInt(2, getIdAcomodacao(acomodacao)); // Implemente esse método para obter o ID
            stmtReserva.setString(3, dataCheckin);
            stmtReserva.setInt(4, quantidadePessoas);
            stmtReserva.setDouble(5, valorTotal);
            stmtReserva.executeUpdate();

            JOptionPane.showMessageDialog(this, "Reserva realizada com sucesso!");
            limparCampos();
        } catch (SQLException e) {
            showError("Erro ao realizar reserva: " + e.getMessage());
        }
    }

    private double calcularValorTotal(String acomodacao) {
        // Implemente a lógica para calcular o valor total com base na acomodação
        return 100.00; // Exemplo, substitua com o valor real
    }

    private int getIdAcomodacao(String acomodacao) {
        // Implemente a lógica para obter o ID da acomodação a partir do nome
        return 1; // Exemplo, substitua com o ID real
    }

    private void limparCampos() {
        campoNome.setText("");
        campoEndereco.setText("");
        campoDataCheckin.setText("");
        campoQuantidadePessoas.setText("");
        comboAcomodacao.setSelectedIndex(0);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaReserva().setVisible(true));
    }
}
