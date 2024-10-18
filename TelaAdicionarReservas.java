package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.toedter.calendar.JDateChooser;
import java.util.List;

public class TelaAdicionarReservas extends JDialog {
    private JComboBox<String> comboClientes;
    private JComboBox<String> comboAcomodacoes;
    private JDateChooser dateChooserCheckIn;
    private JDateChooser dateChooserCheckOut;
    private JSpinner spinnerQuantidadePessoas;
    private JComboBox<String> comboReservas;
    private Map<Integer, List<Date[]>> reservasMap; // Mapeia acomodações e suas datas de reservas

    public TelaAdicionarReservas(JFrame parent) {
        super(parent, "Adicionar Reservas", true); // Modo modal
        setSize(600, 400);
        setLayout(new GridBagLayout());

        reservasMap = new HashMap<>(); // Inicializa o mapa de reservas
        configurarComponentes();
        carregarClientes();
        carregarAcomodacoes();
        carregarReservas();

        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Campos da reserva
        JLabel lblCliente = new JLabel("Cliente:");
        gbc.gridx = 0; gbc.gridy = 0;
        add(lblCliente, gbc);

        comboClientes = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 0;
        add(comboClientes, gbc);

        JLabel lblAcomodacao = new JLabel("Acomodação:");
        gbc.gridx = 0; gbc.gridy = 1;
        add(lblAcomodacao, gbc);

        comboAcomodacoes = new JComboBox<>();
        comboAcomodacoes.addActionListener(e -> verificarDisponibilidadeAcomodacao());
        gbc.gridx = 1; gbc.gridy = 1;
        add(comboAcomodacoes, gbc);

        JLabel lblCheckIn = new JLabel("Data de Check-In:");
        gbc.gridx = 0; gbc.gridy = 2;
        add(lblCheckIn, gbc);

        dateChooserCheckIn = new JDateChooser();
        dateChooserCheckIn.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1; gbc.gridy = 2;
        add(dateChooserCheckIn, gbc);

        JLabel lblCheckOut = new JLabel("Data de Check-Out:");
        gbc.gridx = 0; gbc.gridy = 3;
        add(lblCheckOut, gbc);

        dateChooserCheckOut = new JDateChooser();
        dateChooserCheckOut.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1; gbc.gridy = 3;
        add(dateChooserCheckOut, gbc);

        JLabel lblQuantidadePessoas = new JLabel("Quantidade de Pessoas:");
        gbc.gridx = 0; gbc.gridy = 4;
        add(lblQuantidadePessoas, gbc);

        spinnerQuantidadePessoas = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        gbc.gridx = 1; gbc.gridy = 4;
        add(spinnerQuantidadePessoas, gbc);

        JLabel lblReservas = new JLabel("Reservas Existentes:");
        gbc.gridx = 0; gbc.gridy = 5;
        add(lblReservas, gbc);

        comboReservas = new JComboBox<>();
        gbc.gridx = 1; gbc.gridy = 5;
        add(comboReservas, gbc);

        // Painel para os botões
        JPanel painelBotoes = new JPanel(new FlowLayout());

        JButton btnRegistrarReserva = new JButton("Registrar Reserva");
        btnRegistrarReserva.addActionListener(e -> registrarReserva());
        painelBotoes.add(btnRegistrarReserva);

        JButton btnCancelarReserva = new JButton("Cancelar Reserva");
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        painelBotoes.add(btnCancelarReserva);

        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> dispose());
        painelBotoes.add(btnVoltar);

        // Adicionar o painel de botões à tela
        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(painelBotoes, gbc);
    }

    private void carregarClientes() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id, nome FROM usuarios";
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

    private void carregarAcomodacoes() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id, nome_quarto FROM acomodacoes";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nomeQuarto = rs.getString("nome_quarto");
                comboAcomodacoes.addItem(id + " - " + nomeQuarto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as acomodações: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarReservas() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT id_acomodacao, data_entrada, data_saida FROM reservas";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (rs.next()) {
                int idAcomodacao = rs.getInt("id_acomodacao");
                Date dataEntrada = rs.getDate("data_entrada");
                Date dataSaida = rs.getDate("data_saida");

                // Adiciona a reserva ao mapa de reservas por acomodação
                reservasMap.computeIfAbsent(idAcomodacao, k -> new ArrayList<>()).add(new Date[]{dataEntrada, dataSaida});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar as reservas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarDisponibilidadeAcomodacao() {
        String acomodacaoSelecionada = (String) comboAcomodacoes.getSelectedItem();
        if (acomodacaoSelecionada != null) {
            int idAcomodacao = Integer.parseInt(acomodacaoSelecionada.split(" - ")[0]);
            List<Date[]> reservas = reservasMap.get(idAcomodacao);

            if (reservas != null) {
                dateChooserCheckIn.getDateEditor().setEnabled(true);
                dateChooserCheckOut.getDateEditor().setEnabled(true);
                dateChooserCheckIn.getDateEditor().addPropertyChangeListener("date", evt -> validarDatas(idAcomodacao));
                dateChooserCheckOut.getDateEditor().addPropertyChangeListener("date", evt -> validarDatas(idAcomodacao));
            }
        }
    }

    private void validarDatas(int idAcomodacao) {
        List<Date[]> reservas = reservasMap.get(idAcomodacao);
        Date dataCheckIn = dateChooserCheckIn.getDate();
        Date dataCheckOut = dateChooserCheckOut.getDate();

        if (dataCheckIn != null && dataCheckOut != null && reservas != null) {
            for (Date[] reserva : reservas) {
                if ((dataCheckIn.before(reserva[1]) && dataCheckOut.after(reserva[0])) || (dataCheckOut.before(reserva[1]) && dataCheckIn.after(reserva[0]))) {
                    JOptionPane.showMessageDialog(this, "Acomodação já reservada para este período.", "Erro", JOptionPane.ERROR_MESSAGE);
                    dateChooserCheckIn.setDate(null);
                    dateChooserCheckOut.setDate(null);
                    return;
                }
            }
        }
    }

    private void registrarReserva() {
        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        String acomodacaoSelecionada = (String) comboAcomodacoes.getSelectedItem();
        Date dataCheckIn = dateChooserCheckIn.getDate();
        Date dataCheckOut = dateChooserCheckOut.getDate();
        int quantidadePessoas = (int) spinnerQuantidadePessoas.getValue();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataCheckInFormatada = sdf.format(dataCheckIn);
        String dataCheckOutFormatada = sdf.format(dataCheckOut);

        if (clienteSelecionado != null && acomodacaoSelecionada != null && dataCheckIn != null && dataCheckOut != null) {
            int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);
            int idAcomodacao = Integer.parseInt(acomodacaoSelecionada.split(" - ")[0]);

            if (verificarDisponibilidade(idAcomodacao, dataCheckIn, dataCheckOut)) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    String sql = "INSERT INTO reservas (id_usuario, id_acomodacao, data_entrada, data_saida, quantidade_pessoas, valor_total) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

                    double valorTotal = calcularValorTotal(idAcomodacao, quantidadePessoas, dataCheckIn, dataCheckOut);

                    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                        stmt.setInt(1, idCliente);
                        stmt.setInt(2, idAcomodacao);
                        stmt.setString(3, dataCheckInFormatada);
                        stmt.setString(4, dataCheckOutFormatada);
                        stmt.setInt(5, quantidadePessoas);
                        stmt.setDouble(6, valorTotal);

                        stmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Reserva registrada com sucesso!");
                        dispose(); // Fecha a tela após a confirmação
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao registrar a reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Acomodação já está reservada para as datas selecionadas.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean verificarDisponibilidade(int idAcomodacao, Date dataCheckIn, Date dataCheckOut) {
        List<Date[]> reservas = reservasMap.get(idAcomodacao);

        if (reservas != null) {
            for (Date[] reserva : reservas) {
                if ((dataCheckIn.before(reserva[1]) && dataCheckOut.after(reserva[0])) || (dataCheckOut.before(reserva[1]) && dataCheckIn.after(reserva[0]))) {
                    return false;
                }
            }
        }
        return true;
    }

    private double calcularValorTotal(int idAcomodacao, int quantidadePessoas, Date dataCheckIn, Date dataCheckOut) {
        long dias = (dataCheckOut.getTime() - dataCheckIn.getTime()) / (1000 * 60 * 60 * 24);
        if (dias == 0) dias = 1; // Se for o mesmo dia, conta 1 diária

        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT valor_diaria FROM acomodacoes WHERE id = ?";
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, idAcomodacao);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double valorDiaria = rs.getDouble("valor_diaria");
                return valorDiaria * quantidadePessoas * dias;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void cancelarReserva() {
        int reservaSelecionada = comboReservas.getSelectedIndex();
        if (reservaSelecionada != -1) {
            String reservaInfo = (String) comboReservas.getSelectedItem();
            int idReserva = Integer.parseInt(reservaInfo.split(" - ")[0]); // Obter o ID da reserva

            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja cancelar esta reserva?", "Confirmar Cancelamento", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                try (Connection conexao = ConexaoBanco.getConnection()) {
                    String sql = "DELETE FROM reservas WHERE id = ?";
                    PreparedStatement stmt = conexao.prepareStatement(sql);
                    stmt.setInt(1, idReserva);
                    stmt.executeUpdate();

                    comboReservas.removeItemAt(reservaSelecionada);
                    JOptionPane.showMessageDialog(this, "Reserva cancelada com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao cancelar a reserva: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma reserva para cancelar.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Reservas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Criar a tela de adicionar reservas
        TelaAdicionarReservas telaReservas = new TelaAdicionarReservas(frame);
        telaReservas.setVisible(true);
    }
}
