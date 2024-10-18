package pousada;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaConsumoCliente extends JDialog {
    private JComboBox<String> comboClientes;
    private JTable tabelaConsumo;
    private DefaultTableModel modelConsumo;
    private JButton btnAdicionarConsumo;
    private JButton btnExcluirConsumo;

    public TelaConsumoCliente(JFrame parent) {
        super(parent, "Gerenciar Consumo de Cliente", true);
        setSize(600, 400);
        setLayout(new BorderLayout());

        configurarComponentes();
        carregarClientesComCheckin();
        setLocationRelativeTo(parent); // Centraliza em relação à janela pai
    }

    private void configurarComponentes() {
        JPanel painelFormulario = new JPanel(new BorderLayout());
        JPanel painelCliente = new JPanel(new GridLayout(2, 1, 10, 10));
        painelCliente.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        comboClientes = new JComboBox<>();
        JButton btnCarregarConsumo = new JButton("Carregar Consumo");
        btnCarregarConsumo.addActionListener(e -> carregarConsumoCliente());

        painelCliente.add(new JLabel("Selecione o Cliente:"));
        painelCliente.add(comboClientes);
        painelCliente.add(btnCarregarConsumo);

        // Adiciona tabela de consumo
        modelConsumo = new DefaultTableModel(new Object[]{"ID_Produto", "Quantidade", "Valor Unitário", "Valor Total"}, 0);
        tabelaConsumo = new JTable(modelConsumo);
        JScrollPane scrollPane = new JScrollPane(tabelaConsumo);

        // Botões para adicionar e excluir consumo
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdicionarConsumo = new JButton("Adicionar Consumo");
        btnExcluirConsumo = new JButton("Excluir Consumo");

        btnAdicionarConsumo.addActionListener(e -> adicionarConsumo());
        btnExcluirConsumo.addActionListener(e -> excluirConsumo());

        painelBotoes.add(btnAdicionarConsumo);
        painelBotoes.add(btnExcluirConsumo);

        painelFormulario.add(painelCliente, BorderLayout.NORTH);
        painelFormulario.add(scrollPane, BorderLayout.CENTER);
        painelFormulario.add(painelBotoes, BorderLayout.SOUTH);

        add(painelFormulario, BorderLayout.CENTER);
    }

    private void carregarClientesComCheckin() {
        try {
            // Carrega clientes que têm check-ins realizados
            BancoDadosUtil.carregarClientes(comboClientes, true); // Refatoração: carrega apenas clientes com check-in
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + ex.getMessage());
        }
    }

    private void carregarConsumoCliente() {
        if (comboClientes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        int idCliente;

        try {
            idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]); // Verifica se o ID do cliente é válido
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao identificar o cliente. ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Obtenha o id da reserva diretamente com base no idCliente
            int idReserva = BancoDadosUtil.obterIdReservaPeloCliente(idCliente); // Novo método para obter idReserva

            if (idReserva == -1) {
                JOptionPane.showMessageDialog(this, "Nenhuma reserva encontrada para este cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modelConsumo.setRowCount(0); // Limpa a tabela antes de carregar os dados

            // Carrega o consumo da reserva do cliente
            List<String[]> consumos = BancoDadosUtil.obterConsumoCliente(idReserva); // Carrega com base no idReserva

            for (String[] consumo : consumos) {
                modelConsumo.addRow(consumo);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar consumo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void adicionarConsumo() {
        if (comboClientes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para adicionar o consumo.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String clienteSelecionado = (String) comboClientes.getSelectedItem();
        int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

        TelaAdicionarConsumo telaAdicionarConsumo = new TelaAdicionarConsumo(idCliente, this);
        telaAdicionarConsumo.setVisible(true);
    }

    private void excluirConsumo() {
        int linhaSelecionada = tabelaConsumo.getSelectedRow();  // Obtém a linha selecionada na tabela

        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um produto de consumo para excluir.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja excluir o produto consumido?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                // Obtém os dados da linha selecionada (Produto, Quantidade, Valor Unitário)
                String nomeProduto = (String) modelConsumo.getValueAt(linhaSelecionada, 1);  // Nome do produto na coluna 1

                // A quantidade e o valor unitário podem vir com vírgulas, então vamos substituir vírgulas por pontos
                String quantidadeStr = ((String) modelConsumo.getValueAt(linhaSelecionada, 2)).replace(",", ".");  // Quantidade na coluna 2
                String valorUnitarioStr = ((String) modelConsumo.getValueAt(linhaSelecionada, 3)).replace(",", ".");  // Valor unitário na coluna 3

                // Debug para verificar os valores antes da conversão
                System.out.println("Excluindo consumo: Produto = " + nomeProduto + ", Quantidade = " + quantidadeStr + ", Valor Unitário = " + valorUnitarioStr);

                // Converte a quantidade e o valor unitário para double
                double quantidade = Double.parseDouble(quantidadeStr);  // Converte a quantidade para double
                double valorUnitario = Double.parseDouble(valorUnitarioStr);  // Converte o valor unitário para double

                // Verifica o cliente selecionado
                String clienteSelecionado = (String) comboClientes.getSelectedItem();
                int idCliente = Integer.parseInt(clienteSelecionado.split(" - ")[0]);

                // Obter o idReserva do cliente
                int idReserva = BancoDadosUtil.obterIdReservaPeloCliente(idCliente);  // Obtenha a reserva do cliente

                if (idReserva == -1) {
                    JOptionPane.showMessageDialog(this, "Nenhuma reserva encontrada para este cliente.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Excluir o consumo baseado em idReserva, nomeProduto, quantidade e valor unitário
                BancoDadosUtil.excluirConsumoPorDetalhes(idReserva, nomeProduto, quantidade, valorUnitario);

                // Remover a linha da tabela
                modelConsumo.removeRow(linhaSelecionada);
                JOptionPane.showMessageDialog(this, "Produto consumido excluído com sucesso!");

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Erro ao converter os valores. Verifique se os dados estão no formato correto.", "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir consumo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

}