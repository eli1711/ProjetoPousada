package pousada;

import java.awt.*;
import java.sql.Connection;
import javax.swing.*;

public class TelaInicial extends JFrame {

    private Connection conexao;

    public TelaInicial(Connection conexao) {
        this.conexao = conexao;  // Recebe a conexão do banco de dados
        setTitle("Tela Inicial - Gerenciamento de Pousada");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        configurarComponentes();
    }

    private void configurarComponentes() {
        // Criação do menu superior
        JMenuBar menuBar = new JMenuBar();

        // Menu "Cadastro"
        JMenu menuCadastro = new JMenu("CADASTRO");
        JMenuItem itemCadastroAcomodacoes = new JMenuItem("Cadastrar Acomodações");
        itemCadastroAcomodacoes.addActionListener(e -> abrirDialog(new TelaCadastroAcomodacoes(this)));
        JMenuItem itemCadastroClientes = new JMenuItem("Cadastrar Clientes");
        itemCadastroClientes.addActionListener(e -> abrirDialog(new TelaCadastroClientes(this)));

        JMenuItem itemCadastroProdutos = new JMenuItem("Cadastrar Produtos");
        itemCadastroProdutos.addActionListener(e -> abrirDialog(new CadastroDeProdutos(this)));

        JMenuItem itemCadastroUsuario = new JMenuItem("Cadastrar Usuário");
        itemCadastroUsuario.addActionListener(e -> abrirDialog(new TelaCadastroUsuario(this, conexao)));

        // Adiciona os itens ao menu "Cadastro"
        menuCadastro.add(itemCadastroAcomodacoes);
        menuCadastro.add(itemCadastroClientes);
        menuCadastro.add(itemCadastroProdutos);
        menuCadastro.add(itemCadastroUsuario);

        // Menu "Consultas"
        JMenu menuConsultas = new JMenu("CONSULTAS");
        JMenuItem itemConsultaAcomodacoes = new JMenuItem("Consultar Acomodações");
        itemConsultaAcomodacoes.addActionListener(e -> abrirDialog(new TelaConsultaAcomodacoes(this)));
        JMenuItem itemConsultaClientes = new JMenuItem("Consultar Clientes");
        itemConsultaClientes.addActionListener(e -> abrirDialog(new TelaClientesCadastrados(this)));
        JMenuItem itemConsultaCheckin = new JMenuItem("Consultar Check-In/Check-Out");
        itemConsultaCheckin.addActionListener(e -> abrirDialog(new TelaConsultaCheck(this)));
        JMenuItem itemConsultaReservas = new JMenuItem("Consultar Reservas");
        itemConsultaReservas.addActionListener(e -> abrirDialog(new ConsultaReservas(this)));

        JMenuItem itemListarProdutos = new JMenuItem("Listar Produtos");
        itemListarProdutos.addActionListener(e -> abrirDialog(new ListarProdutos(this)));

        menuConsultas.add(itemConsultaAcomodacoes);
        menuConsultas.add(itemConsultaClientes);
        menuConsultas.add(itemConsultaCheckin);
        menuConsultas.add(itemConsultaReservas);
        menuConsultas.add(itemListarProdutos);

        // Menu "Administração"
        JMenu menuAdministracao = new JMenu("ADMINISTRAÇÃO");
        JMenuItem itemCheckin = new JMenuItem("Realizar Check-In");
        itemCheckin.addActionListener(e -> abrirDialog(new TelaCheckin(this)));
        JMenuItem itemCheckout = new JMenuItem("Realizar Check-Out");
        itemCheckout.addActionListener(e -> abrirDialog(new TelaCheckOut(this)));
        JMenuItem itemAdicionarReservas = new JMenuItem("Adicionar Reservas");
        itemAdicionarReservas.addActionListener(e -> abrirDialog(new TelaAdicionarReservas(this)));
        JMenuItem itemConsumoCliente = new JMenuItem("Consumo Cliente");
        itemConsumoCliente.addActionListener(e -> abrirDialog(new TelaConsumoCliente(this)));
        JMenuItem itemFecharConta = new JMenuItem("Fechar Conta");
        itemFecharConta.addActionListener(e -> abrirDialog(new TelaFecharConta(this)));

        menuAdministracao.add(itemCheckin);
        menuAdministracao.add(itemCheckout);
        menuAdministracao.add(itemAdicionarReservas);
        menuAdministracao.add(itemConsumoCliente);
        menuAdministracao.add(itemFecharConta);

        menuBar.add(menuCadastro);
        menuBar.add(menuConsultas);
        menuBar.add(menuAdministracao);
        setJMenuBar(menuBar);

        JPanel painelFundo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                ImageIcon imagemFundo = new ImageIcon(getClass().getResource("/img/imagenInicial.jpg"));
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2d.drawImage(imagemFundo.getImage(), 0, 0, getWidth(), getHeight(), this);
                g2d.dispose();
            }
        };
        painelFundo.setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Bem-vindo ao Sistema de Gerenciamento da Pousada Quinta do Ipuã", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 30));
        lblTitulo.setForeground(Color.BLACK);
        lblTitulo.setOpaque(false);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 70, 0));
        painelFundo.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelTextoDireita = new JPanel(new BorderLayout());
        painelTextoDireita.setBackground(new Color(0, 0, 0, 0));
        painelTextoDireita.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTextoDireita = new JLabel("<html><div style='text-align: justify;'>"
                + "<br>A pousada Quinta do Ypuã oferece aos seus clientes<br> um recanto de aconchego e lazer,<br> em um ambiente rústico "
                + "e agradável,<br> ideal para quem deseja fugir da rotina<br> e busca um local de paz para descansar <br>e aproveitar a natureza.<br><br>"
                + "<i>\"O Ypuã tem tudo a ver com a natureza,<br> dá para sentir a energia do lugar.<br> Eu me preocupo se você vai comer bem,<br> "
                + "dormir bem e se vai se sentir em casa.<br> Vou te mostrar onde encontrar os melhores frutos do mar,<br> onde curtir a melhor praia "
                + "e as melhores ondas. <br> Mas se você não quiser fazer nada,<br> eu também conheço o melhor lugar.\"</i><br><br>"
                + "<b>HEITOR, Anfitrião da pousada</b>"
                + "</div></html>", JLabel.CENTER);

        lblTextoDireita.setFont(new Font("Arial", Font.PLAIN, 25));
        lblTextoDireita.setForeground(Color.BLACK);
        lblTextoDireita.setVerticalAlignment(JLabel.TOP);
        lblTextoDireita.setHorizontalAlignment(JLabel.CENTER);

        painelTextoDireita.add(lblTextoDireita, BorderLayout.CENTER);
        painelFundo.add(painelTextoDireita, BorderLayout.EAST);

        JLabel lblRodape = new JLabel("Sistema desenvolvido pelo grupo ... do Senai", JLabel.CENTER);
        lblRodape.setFont(new Font("Arial", Font.ITALIC, 14));
        lblRodape.setForeground(Color.BLACK);
        lblRodape.setOpaque(false);
        lblRodape.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        painelFundo.add(lblRodape, BorderLayout.SOUTH);

        setContentPane(painelFundo);
    }

    private void abrirDialog(JDialog dialog) {
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
