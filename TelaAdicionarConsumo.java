package pousada;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TelaAdicionarConsumo extends JDialog {
    private int idCliente;
    private JComboBox<String> comboAcomodacoes;
    private JComboBox<String> comboProdutos;
    private JTextField txtQuantidade;
    private JTextField txtValorUnitario;
    private JButton btnRegistrarConsumo;

    public TelaAdicionarConsumo(int idCliente, TelaConsumoCliente telaConsumoCliente) {
        super(telaConsumoCliente, "Registrar Consumo para Cliente", true);  // JDialog modal
        this.idCliente = idCliente;
        configurarJanela(telaConsumoCliente);
        configurarComponentes();
        carregarAcomodacoes();
        carregarProdutos();
    }

    private void configurarJanela(TelaConsumoCliente telaConsumoCliente) {
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(telaConsumoCliente);  // Centraliza a janela
    }

    private void configurarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(6, 2, 10, 10));
        painelFormulario.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        comboAcomodacoes = new JComboBox<>();
        comboProdutos = new JComboBox<>();
        txtQuantidade = new JTextField();
        txtValorUnitario = new JTextField();
        txtValorUnitario.setEditable(false);  // Valor unitário é definido automaticamente
        btnRegistrarConsumo = new JButton("Registrar Consumo");
        btnRegistrarConsumo.addActionListener(e -> registrarConsumo());

        // Adiciona os componentes ao painel
        painelFormulario.add(new JLabel("Acomodação:"));
        painelFormulario.add(comboAcomodacoes);
        painelFormulario.add(new JLabel("Produto:"));
        painelFormulario.add(comboProdutos);
        painelFormulario.add(new JLabel("Quantidade:"));
        painelFormulario.add(txtQuantidade);
        painelFormulario.add(new JLabel("Valor Unitário:"));
        painelFormulario.add(txtValorUnitario);
        painelFormulario.add(new JLabel());  // Espaço em branco
        painelFormulario.add(btnRegistrarConsumo);

        // Adiciona o painel ao centro da janela
        add(painelFormulario, BorderLayout.CENTER);

        // Adiciona evento para atualizar o valor unitário quando o produto for selecionado
        comboProdutos.addActionListener(e -> atualizarValorUnitario());
    }

    private void carregarAcomodacoes() {
        comboAcomodacoes.removeAllItems();  // Limpa itens anteriores

        try {
            // Carrega acomodações para o cliente que fez check-in
            BancoDadosUtil.carregarAcomodacoesPorCliente(comboAcomodacoes, idCliente);
        } catch (SQLException ex) {
            exibirMensagemErro("Erro ao carregar acomodações: " + ex.getMessage());
            ex.printStackTrace();  // Exibe o stack trace completo para facilitar o diagnóstico
        }
    }

    private void carregarProdutos() {
        comboProdutos.removeAllItems();  // Limpa os produtos anteriores

        try {
            // Carrega a lista de produtos disponíveis
            List<String[]> produtos = BancoDadosUtil.obterListaProdutos();
            for (String[] produto : produtos) {
                comboProdutos.addItem(produto[0] + " - " + produto[1]);  // Formato: id - nome
            }
        } catch (SQLException ex) {
            exibirMensagemErro("Erro ao carregar produtos: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void atualizarValorUnitario() {
        if (comboProdutos.getSelectedItem() != null) {
            String produtoSelecionado = comboProdutos.getSelectedItem().toString();
            int idProduto = Integer.parseInt(produtoSelecionado.split(" - ")[0]);  // Extrai o ID do produto

            try {
                List<String[]> produtos = BancoDadosUtil.obterListaProdutos();
                for (String[] produto : produtos) {
                    if (Integer.parseInt(produto[0]) == idProduto) {
                        txtValorUnitario.setText(produto[2]);  // Define o valor unitário no campo
                        break;
                    }
                }
            } catch (SQLException ex) {
                exibirMensagemErro("Erro ao atualizar valor unitário: " + ex.getMessage());
            }
        }
    }

    private void registrarConsumo() {
        if (comboAcomodacoes.getSelectedItem() == null) {
            exibirMensagemErro("Selecione uma acomodação.");
            return;
        }

        if (comboProdutos.getSelectedItem() == null) {
            exibirMensagemErro("Selecione um produto.");
            return;
        }

        String acomodacaoSelecionada = comboAcomodacoes.getSelectedItem().toString();
        int idAcomodacao = obterIdAcomodacao(acomodacaoSelecionada);

        String produtoSelecionado = comboProdutos.getSelectedItem().toString();
        int idProduto = Integer.parseInt(produtoSelecionado.split(" - ")[0]);  // Extrai o ID do produto

        int quantidade;
        double valorUnitario;
        try {
            quantidade = Integer.parseInt(txtQuantidade.getText().trim());
            valorUnitario = Double.parseDouble(txtValorUnitario.getText().trim());
        } catch (NumberFormatException e) {
            exibirMensagemErro("Quantidade e valor unitário devem ser numéricos.");
            return;
        }

        double valorTotal = quantidade * valorUnitario;

        try {
            int idReserva = BancoDadosUtil.obterIdReserva(idCliente, idAcomodacao);
            BancoDadosUtil.registrarConsumo(idReserva, produtoSelecionado, quantidade, valorUnitario, valorTotal);
            exibirMensagemSucesso("Consumo registrado com sucesso!");
            dispose();  // Fecha a janela após o registro bem-sucedido
        } catch (SQLException ex) {
            exibirMensagemErro("Erro ao registrar consumo: " + ex.getMessage());
        }
    }

    private int obterIdAcomodacao(String acomodacaoSelecionada) {
        // Extrai o ID da acomodação da string formatada como "id - nome"
        return Integer.parseInt(acomodacaoSelecionada.split(" - ")[0]);
    }

    private void exibirMensagemErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void exibirMensagemSucesso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }
}

