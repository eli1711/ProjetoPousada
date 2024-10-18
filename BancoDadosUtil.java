package pousada;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BancoDadosUtil {

    public static Connection getConnection() throws SQLException {
        return ConexaoBanco.getConnection();
    }

    // Método para obter a lista de clientes com status de check-in e check-out
    public static List<String[]> obterClientesComStatus() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String sql = "SELECT u.id, u.nome, r.checkin, r.checkout " +
                     "FROM usuarios u " +
                     "JOIN reservas r ON u.id = r.id_usuario";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String[] cliente = new String[4];
                cliente[0] = String.valueOf(rs.getInt("id"));
                cliente[1] = rs.getString("nome");
                cliente[2] = rs.getDate("checkin") != null ? rs.getDate("checkin").toString() : "Não realizou Check-In";
                cliente[3] = rs.getDate("checkout") != null ? rs.getDate("checkout").toString() : "Ainda em Check-In";
                clientes.add(cliente);
            }
        }
        return clientes;
    }

    public static int obterIdReserva(int idCliente, int idAcomodacao) throws SQLException {
        String sql = "SELECT id FROM reservas WHERE id_usuario = ? AND id_acomodacao = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.setInt(2, idAcomodacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }
    public static int obterIdReservaPeloCliente(int idCliente) throws SQLException {
        String sql = "SELECT id FROM reservas WHERE id_usuario = ? AND checkin IS NOT NULL AND checkout IS NULL";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id"); // Retorna o id da reserva
            }
        }

        return -1; // Retorna -1 se nenhuma reserva for encontrada
    }

    public static List<String[]> obterConsumoCliente(int idReserva) throws SQLException {
        List<String[]> consumos = new ArrayList<>();
        String sql = "SELECT c.produto, c.quantidade, c.valor_unitario, (c.quantidade * c.valor_unitario) AS valor_total " +
                     "FROM consumo c " +
                     "WHERE c.id_reserva = ?";  // Ajuste feito para buscar com base no id_reserva

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] consumo = new String[4];
                consumo[0] = rs.getString("produto");
                consumo[1] = String.valueOf(rs.getInt("quantidade"));
                consumo[2] = String.format("%.2f", rs.getDouble("valor_unitario"));
                consumo[3] = String.format("%.2f", rs.getDouble("valor_total"));
                consumos.add(consumo);
            }
        }

        return consumos;
    }


    // Método unificado para carregar clientes com reservas e/ou check-ins
    public static void carregarClientes(JComboBox<String> comboClientes, boolean apenasComCheckin) throws SQLException {
        String sql = "SELECT u.id, u.nome " +
                     "FROM usuarios u " +
                     "JOIN reservas r ON u.id = r.id_usuario " +
                     "WHERE r.checkin IS NOT NULL " +
                     (apenasComCheckin ? "AND r.checkout IS NULL" : "");

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            comboClientes.removeAllItems();

            while (rs.next()) {
                comboClientes.addItem(rs.getInt("id") + " - " + rs.getString("nome"));
            }

        } catch (SQLException e) {
            throw new SQLException("Erro ao carregar clientes com reservas: " + e.getMessage(), e);
        }
    }

    // Registrar check-in e check-out de clientes
    public static void registrarCheckIn(int idReserva, int idCliente, Date dataCheckIn) throws SQLException {
        String sql = "UPDATE reservas SET checkin = ? WHERE id = ?";  // Corrigido para 'checkin'
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dataCheckIn.getTime()));
            stmt.setInt(2, idReserva);
            stmt.executeUpdate();
        }
    }

    public static void registrarCheckOut(int idReserva, Date dataCheckOut) throws SQLException {
        String sql = "UPDATE reservas SET checkout = ? WHERE id = ?";  // Corrigido para 'checkout'
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(dataCheckOut.getTime()));
            stmt.setInt(2, idReserva);
            stmt.executeUpdate();
        }
    }

    // Calcular o total de consumo
    public static double obterTotalConsumo(int idReserva) throws SQLException {
        String sql = "SELECT SUM(valor_total) AS total FROM consumo WHERE id_reserva = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0;
    }

    public static double obterValorDiaria(int idAcomodacao) throws SQLException {
        String sql = "SELECT valor_diaria FROM acomodacoes WHERE id = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idAcomodacao);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("valor_diaria");
            }
        }
        return 0;
    }

    // Registrar o consumo de produtos por cliente
    public static void registrarConsumo(int idReserva, String produto, int quantidade, double valorUnitario, double valorTotal) throws SQLException {
        String sql = "INSERT INTO consumo (id_reserva, produto, quantidade, valor_unitario, valor_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.setString(2, produto);
            stmt.setInt(3, quantidade);
            stmt.setDouble(4, valorUnitario);
            stmt.setDouble(5, valorTotal);
            stmt.executeUpdate();
        }
    }

    // Método para inserir produto no banco de dados
    public static void inserirProduto(String nomeProduto, double preco) throws SQLException {
        String sql = "INSERT INTO produtos_consumo (nome_produto, preco) VALUES (?, ?)";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nomeProduto);
            stmt.setDouble(2, preco);
            stmt.executeUpdate();
        }
    }

    // Método para excluir um produto
    public static void excluirProduto(int idProduto) throws SQLException {
        String sql = "DELETE FROM produtos_consumo WHERE id_produto = ?";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir o produto: " + e.getMessage(), e);
        }
    }

    // Método para obter a lista de produtos disponíveis
    public static List<String[]> obterListaProdutos() throws SQLException {
        List<String[]> produtos = new ArrayList<>();
        String sql = "SELECT id_produto, nome_produto, preco FROM produtos_consumo";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String[] produto = new String[3];
                produto[0] = String.valueOf(rs.getInt("id_produto"));
                produto[1] = rs.getString("nome_produto");
                produto[2] = String.valueOf(rs.getDouble("preco"));
                produtos.add(produto);
            }
        }

        return produtos;
    }
    public static void excluirConsumoPorDetalhes(int idReserva, String nomeProduto, double quantidade, double valorUnitario) throws SQLException {
        String sql = "DELETE FROM consumo WHERE id_reserva = ? AND produto = ? AND quantidade = ? AND ROUND(valor_unitario, 2) = ROUND(?, 2)";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.setString(2, nomeProduto);
            stmt.setDouble(3, quantidade);
            stmt.setDouble(4, valorUnitario);  // Passa o valor unitário arredondado para evitar problemas de precisão

            int rowsAffected = stmt.executeUpdate();  // Executa a exclusão

            if (rowsAffected == 0) {
                System.out.println("Nenhuma linha foi excluída. Verifique os parâmetros de exclusão.");
            } else {
                System.out.println("Consumo excluído com sucesso!");
            }
        }
    }


    // Carregar acomodações reservadas por cliente
    public static void carregarAcomodacoesPorCliente(JComboBox<String> comboAcomodacoes, int idCliente) throws SQLException {
        String sql = "SELECT a.id, a.nome_quarto FROM acomodacoes a " +
                     "JOIN reservas r ON a.id = r.id_acomodacao " +
                     "WHERE r.id_usuario = ? AND r.checkin IS NOT NULL AND r.checkout IS NULL";

        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, idCliente);  // Define o ID do cliente para a consulta

            try (ResultSet rs = stmt.executeQuery()) {
                comboAcomodacoes.removeAllItems();  // Limpa o combo box antes de adicionar novos itens

                while (rs.next()) {
                    int idAcomodacao = rs.getInt("id");
                    String nomeAcomodacao = rs.getString("nome_quarto");
                    comboAcomodacoes.addItem(idAcomodacao + " - " + nomeAcomodacao);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao carregar acomodações: " + e.getMessage(), e);
        }
    }

    // Excluir consumo de cliente
    public static void excluirConsumo(int idReserva, int Produto) throws SQLException {
        String sql = "DELETE FROM consumo WHERE id_reserva = ? AND produto = ?";
        try (Connection conexao = getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idReserva);
            stmt.setInt(2, Produto);
            stmt.executeUpdate();
        }
    }
}

