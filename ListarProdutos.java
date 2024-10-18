package pousada;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ListarProdutos extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    // Construtor
    public ListarProdutos(Frame parent) {
        super(parent, "Listar Produtos", true);
        setBounds(100, 100, 600, 400);
        initUI();
    }

    // Método para inicializar a interface gráfica
    private void initUI() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane();
        table = new JTable();
        scrollPane.setViewportView(table);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Botão OK para fechar a janela
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dispose());
        buttonPane.add(okButton);

        // Botão Excluir para remover um produto
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirProduto());
        buttonPane.add(btnExcluir);

        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        carregarProdutos();
    }

    // Método para carregar os produtos na tabela
    private void carregarProdutos() {
        try {
            List<String[]> produtos = BancoDadosUtil.obterListaProdutos();

            String[] colunas = {"ID", "Nome do Produto", "Preço"};
            model = new DefaultTableModel(colunas, 0);

            if (produtos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum produto encontrado.", "Informação", JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (String[] produto : produtos) {
                    produto[2] = String.format("R$ %.2f", Double.parseDouble(produto[2])); // Formatar o preço
                    model.addRow(produto);
                }
            }

            table.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para excluir o produto selecionado
    private void excluirProduto() {
        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, "Tem certeza de que deseja excluir o produto selecionado?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                // Obtém o ID do produto selecionado
                int idProduto = Integer.parseInt((String) model.getValueAt(selectedRow, 0));

                // Exclui o produto do banco de dados
                BancoDadosUtil.excluirProduto(idProduto);

                // Remove a linha da tabela
                model.removeRow(selectedRow);

                JOptionPane.showMessageDialog(this, "Produto excluído com sucesso!");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir o produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

