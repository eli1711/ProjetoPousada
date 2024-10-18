package pousada;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TelaClientesCadastrados extends JDialog {  // Mudança para JDialog
    private DefaultTableModel modeloTabelaClientes;

    public TelaClientesCadastrados(JFrame parent) {
        super(parent, "Clientes Cadastrados - Gerenciamento de Pousada", true); // Modo modal
        setSize(1000, 600);  // Define um tamanho padrão para a janela
        setLayout(new BorderLayout());

        configurarComponentes();
        carregarClientesDoBanco();  // Carrega os clientes do banco de dados
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        // Título da tela
        JLabel lblTitulo = new JLabel("Clientes Cadastrados", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBackground(Color.GRAY);
        lblTitulo.setOpaque(true);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Configuração da tabela de clientes
        modeloTabelaClientes = new DefaultTableModel();
        modeloTabelaClientes.addColumn("Nome");
        modeloTabelaClientes.addColumn("Endereço");
        modeloTabelaClientes.addColumn("CEP");
        modeloTabelaClientes.addColumn("Estado");
        modeloTabelaClientes.addColumn("País");
        modeloTabelaClientes.addColumn("Telefone");
        modeloTabelaClientes.addColumn("E-mail");
        modeloTabelaClientes.addColumn("Data de Nascimento");

        JTable tabelaClientes = new JTable(modeloTabelaClientes);
        tabelaClientes.setBackground(Color.BLACK);
        tabelaClientes.setForeground(Color.WHITE);
        tabelaClientes.setFont(new Font("Arial", Font.PLAIN, 16));
        tabelaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        tabelaClientes.getTableHeader().setBackground(Color.GRAY);
        tabelaClientes.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tabelaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new GridLayout(1, 4, 10, 10));
        painelBotoes.setBackground(Color.DARK_GRAY);
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JButton btnAdicionarCliente = criarBotao("Adicionar Cliente", e -> adicionarCliente());
        JButton btnEditarCliente = criarBotao("Editar Cliente", e -> editarCliente(tabelaClientes));
        JButton btnRemoverCliente = criarBotao("Remover Cliente", e -> removerCliente(tabelaClientes));
        JButton btnVoltar = criarBotao("Voltar", e -> dispose()); // Fecha o diálogo

        painelBotoes.add(btnAdicionarCliente);
        painelBotoes.add(btnEditarCliente);
        painelBotoes.add(btnRemoverCliente);
        painelBotoes.add(btnVoltar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private JButton criarBotao(String texto, ActionListener acao) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Tahoma", Font.PLAIN, 28));
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(70, 130, 180));  // Cor azul para consistência com a tela inicial
        botao.setFocusPainted(false);
        botao.addActionListener(acao);
        return botao;
    }

    private void carregarClientesDoBanco() {
        try (Connection conexao = ConexaoBanco.getConnection()) {
            String sql = "SELECT nome, endereco, cep, estado, pais, telefone, email, data_nascimento FROM usuarios";
            Statement stmt = conexao.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String nome = rs.getString("nome");
                String endereco = rs.getString("endereco");
                String cep = rs.getString("cep");
                String estado = rs.getString("estado");
                String pais = rs.getString("pais");
                String telefone = rs.getString("telefone");
                String email = rs.getString("email");
                String dataNascimento = rs.getString("data_nascimento");

                modeloTabelaClientes.addRow(new Object[]{nome, endereco, cep, estado, pais, telefone, email, dataNascimento});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar os clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adicionarCliente() {
        // Implementação do método de adicionar cliente
        String nome = JOptionPane.showInputDialog("Digite o nome do cliente:");
        // (Continua para os outros campos...)
        // Adicionar lógica para validar e inserir os dados na tabela
    }

    private void editarCliente(JTable tabelaClientes) {
        // Implementação do método de editar cliente
        // Lógica para editar a linha selecionada na tabela
    }

    private void removerCliente(JTable tabelaClientes) {
        // Implementação do método de remover cliente
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada != -1) {
            int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover este cliente?", "Confirmar remoção", JOptionPane.YES_NO_OPTION);
            if (confirmacao == JOptionPane.YES_OPTION) {
                modeloTabelaClientes.removeRow(linhaSelecionada);
                JOptionPane.showMessageDialog(this, "Cliente removido com sucesso!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para remover.");
        }
    }

    public static void main(String[] args) {
        // Para testar, abrir a janela dentro de um JFrame
        JFrame frame = new JFrame("Teste - Clientes Cadastrados");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);

        TelaClientesCadastrados telaClientesCadastrados = new TelaClientesCadastrados(frame);
        telaClientesCadastrados.setVisible(true); // Mostra o diálogo
    }
}
