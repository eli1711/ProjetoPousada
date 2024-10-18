package pousada;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoBanco {
    private static final String URL = "jdbc:mysql://localhost:3306/pousada_gerenciamento"; // URL do banco de dados
    private static final String USER = "root"; // Usuário do banco de dados
    private static final String PASSWORD = "17111988"; // Senha do banco de dados

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try {
            Connection conexao = getConnection();
            System.out.println("Conexão bem-sucedida!");
            conexao.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
