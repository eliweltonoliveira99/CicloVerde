package view;

import dao.AlimentoDAO;
import dao.EmpresaDAO;
import model.Alimento;
import model.Empresa;
import model.Validacao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class EmpresasUI extends JFrame {
    private final boolean admin;
    private final DefaultListModel<Empresa> modeloEmpresas = new DefaultListModel<>();
    private final JList<Empresa> lista = new JList<>(modeloEmpresas);
    private final JLabel resumo = UI.texto("Carregando empresas e alimentos…");
    private final JLabel selecao = UI.titulo("Todas as empresas", 20);
    private final JLabel detalhes = UI.texto("Selecione uma empresa para conhecer seu perfil.");
    private final JButton atualizar = UI.botao("Atualizar", UI.AZUL);
    private final JButton perfil = UI.botao("Abrir perfil e alimentos", UI.VERDE);
    private final JButton cadastrar = UI.botao("Cadastrar alimento", UI.VERDE);
    private final JTabbedPane abas = new JTabbedPane();
    private final DefaultTableModel disponiveis = modeloTabela(), todos = modeloTabela();
    private List<Alimento> alimentos = List.of();
    private boolean carregando;
    private final JPanel catalogo = UI.painel(new GridBagLayout());
    private final JLabel totalEmpresas = UI.titulo("—", 28), totalDisponiveis = UI.titulo("—", 28), totalReservados = UI.titulo("—", 28);
    private record Dados(List<Empresa> empresas, List<Alimento> alimentos) {}

    public EmpresasUI(boolean admin) {
        this.admin = admin;
        setTitle(admin ? "CicloVerde - Administrador" : "CicloVerde - Empresas e alimentos");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1150, 780);
        setMinimumSize(new Dimension(1000, 680)); setLocationRelativeTo(null);
        JPanel raiz = UI.fundo(new BorderLayout(0, 16), !admin);
        JPanel topoGeral = UI.painel(new BorderLayout());
        topoGeral.add(UI.cabecalho(admin ? "CICLOVERDE / ADMINISTRADOR" : "CICLOVERDE / ENCONTRE UMA DOAÇÃO",
                admin ? "Empresas e alimentos" : "Conheça as empresas parceiras",
                admin ? "Consulte empresas cadastradas e acompanhe os lotes em um só lugar." : "Abra o perfil da empresa para consultar os alimentos e reservar um lote.", !admin), BorderLayout.NORTH);
        if (admin) {
            JPanel indicadores = UI.painel(new GridLayout(1,3,16,0)); indicadores.setBorder(new EmptyBorder(16,24,0,24));
            String[] rotulos = {"Empresas cadastradas", "Lotes disponíveis", "Lotes reservados"};
            JLabel[] valores = {totalEmpresas,totalDisponiveis,totalReservados};
            for (int i=0;i<valores.length;i++) {
                JPanel cartao = UI.cartao(new BorderLayout(16,0),16); valores[i].setForeground(UI.VERDE);
                cartao.add(valores[i],BorderLayout.WEST); cartao.add(UI.texto(rotulos[i]),BorderLayout.CENTER); indicadores.add(cartao);
            }
            topoGeral.add(indicadores,BorderLayout.CENTER);
        }
        raiz.add(topoGeral,BorderLayout.NORTH);
        JPanel esquerda = UI.cartao(new BorderLayout(0, 10),18);
        esquerda.add(UI.titulo("Empresas cadastradas", 17), BorderLayout.NORTH);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFixedCellHeight(84);
        lista.setCellRenderer((list, empresa, index, selecionada, foco) -> {
            JLabel l = new JLabel("<html><b>" + UI.html(empresa.getNome()) + "</b><br>" + UI.html(empresa.getCidade().isBlank() ? "Cidade não informada" : empresa.getCidade())
                    + (empresa.isDemonstracao() ? " · Demonstração" : "") + "</html>");
            l.setOpaque(true); l.setBackground(selecionada ? new Color(234, 247, 234) : Color.WHITE);
            l.setForeground(UI.TEXTO); l.setBorder(new EmptyBorder(10, 12, 10, 12)); return l;
        });
        esquerda.add(new JScrollPane(lista), BorderLayout.CENTER);
        JPanel acoesEmpresa = UI.painel(new GridLayout(0, 1, 0, 8));
        if (admin) {
            JButton nova = UI.botao("Cadastrar empresa", UI.AZUL);
            nova.addActionListener(e -> new CadastroEmpresaUI(this, this::carregar).setVisible(true)); acoesEmpresa.add(nova);
            JButton todas = UI.secundario("Ver todas as empresas"); todas.addActionListener(e -> lista.clearSelection()); acoesEmpresa.add(todas);
        }
        esquerda.add(acoesEmpresa, BorderLayout.SOUTH);

        JPanel direita = UI.cartao(new BorderLayout(0, 14),18);
        JPanel topo = UI.painel(new BorderLayout(0, 10));
        JPanel info = UI.painel(new GridLayout(0, 1, 0, 8)); info.add(selecao); info.add(detalhes); topo.add(info, BorderLayout.CENTER);
        JPanel acoes = UI.painel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acoes.add(perfil); if (admin) { acoes.add(Box.createHorizontalStrut(10)); acoes.add(cadastrar); }
        topo.add(acoes, BorderLayout.SOUTH); direita.add(topo, BorderLayout.NORTH);
        abas.addTab("Disponíveis para reserva", new JScrollPane(tabela(disponiveis)));
        if (admin) abas.addTab("Todos os lotes", new JScrollPane(tabela(todos)));
        direita.add(abas, BorderLayout.CENTER);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, esquerda, direita);
        split.setBorder(new EmptyBorder(0,24,0,24)); split.setOpaque(false); split.setDividerSize(16);
        split.setDividerLocation(340); split.setResizeWeight(0.28);
        if (admin) raiz.add(split, BorderLayout.CENTER);
        else {
            JScrollPane scroll = new JScrollPane(catalogo); scroll.setBorder(new EmptyBorder(4,24,0,24));
            scroll.setOpaque(false); scroll.getViewport().setOpaque(false); scroll.getVerticalScrollBar().setUnitIncrement(20);
            raiz.add(scroll,BorderLayout.CENTER);
        }
        JPanel rodape = UI.painel(new BorderLayout(16, 0)); rodape.setBorder(new EmptyBorder(8, 24, 18, 24));
        JButton inicio = UI.secundario("Voltar ao início"); inicio.addActionListener(e -> { new HomeUI().setVisible(true); dispose(); });
        rodape.add(inicio, BorderLayout.WEST); rodape.add(resumo, BorderLayout.CENTER); rodape.add(atualizar, BorderLayout.EAST);
        raiz.add(rodape, BorderLayout.SOUTH); setContentPane(raiz);
        lista.addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && !carregando) renderizar(); });
        perfil.addActionListener(e -> {
            Empresa empresa = lista.getSelectedValue();
            if (empresa != null) new RetiradaUI(this, empresa, admin, this::carregar).setVisible(true);
        });
        cadastrar.addActionListener(e -> new CadastroUI(this, lista.getSelectedValue(), this::carregar).setVisible(true));
        atualizar.addActionListener(e -> carregar()); carregar();
    }
    private static DefaultTableModel modeloTabela() {
        return new DefaultTableModel(new String[]{"Empresa", "Alimento", "kg", "Validade", "Situação"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }
    private static JTable tabela(DefaultTableModel modelo) {
        JTable t = new JTable(modelo); t.setRowHeight(36); t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0, 0));
        t.setAutoCreateRowSorter(true); t.getTableHeader().setReorderingAllowed(false);
        t.setSelectionBackground(new Color(234,247,234)); t.setSelectionForeground(UI.TEXTO);
        t.getTableHeader().setPreferredSize(new Dimension(0,36));
        int[] larguras = {170,230,60,110,120};
        for (int i=0;i<larguras.length;i++) t.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        return t;
    }
    private void carregar() {
        if (carregando) return;
        Empresa selecionada = lista.getSelectedValue(); int id = selecionada == null ? -1 : selecionada.getId();
        carregando = true; lista.setEnabled(false); perfil.setEnabled(false); cadastrar.setEnabled(false);
        disponiveis.setRowCount(0); todos.setRowCount(0); resumo.setText("Carregando empresas e alimentos…");
        totalEmpresas.setText("—"); totalDisponiveis.setText("—"); totalReservados.setText("—");
        catalogo.removeAll(); catalogo.revalidate(); catalogo.repaint();
        UI.tarefa(this, atualizar, () -> new Dados(new EmpresaDAO().listar(), new AlimentoDAO().listarTodos()), dados -> {
            modeloEmpresas.clear();
            for (Empresa empresa : dados.empresas()) { modeloEmpresas.addElement(empresa); if (empresa.getId() == id) lista.setSelectedValue(empresa, true); }
            if (!admin && lista.isSelectionEmpty() && !modeloEmpresas.isEmpty()) lista.setSelectedIndex(0);
            alimentos = dados.alimentos(); carregando = false; lista.setEnabled(true); renderizar();
        }, mensagem -> {
            modeloEmpresas.clear(); alimentos = List.of(); carregando = false; lista.setEnabled(true);
            selecao.setText("Não foi possível carregar"); detalhes.setText("Verifique o banco e clique em Atualizar.");
            resumo.setText("Falha de conexão. Os dados não foram carregados.");
            catalogo.add(UI.texto("Não foi possível carregar. Verifique o banco e clique em Atualizar.")); catalogo.revalidate(); catalogo.repaint();
            JOptionPane.showMessageDialog(this, mensagem, "Falha ao carregar", JOptionPane.ERROR_MESSAGE);
        });
    }
    private void renderizar() {
        totalEmpresas.setText(Integer.toString(modeloEmpresas.size()));
        totalDisponiveis.setText(Long.toString(alimentos.stream().filter(this::disponivel).count()));
        totalReservados.setText(Long.toString(alimentos.stream().filter(a -> Alimento.RESERVADO.equals(a.getStatus())).count()));
        if (!admin) { renderizarCatalogo(); return; }
        Empresa empresa = lista.getSelectedValue();
        selecao.setText(empresa == null ? "Todas as empresas" : empresa.getNome());
        detalhes.setText(empresa == null ? "Selecione uma empresa para consultar seu perfil e filtrar os lotes."
                : (empresa.getCidade().isBlank() ? "Cidade não informada" : empresa.getCidade()) + (empresa.isDemonstracao() ? " · Perfil de demonstração" : ""));
        perfil.setEnabled(empresa != null); cadastrar.setEnabled(admin && !modeloEmpresas.isEmpty());
        disponiveis.setRowCount(0); todos.setRowCount(0); int qtd = 0;
        for (Alimento a : alimentos) {
            if (empresa != null && a.getComercioId() != empresa.getId()) continue;
            LocalDate data = LocalDate.parse(a.getDataValidade()); boolean vencido = data.isBefore(LocalDate.now());
            String status = a.getStatus() + (vencido ? " · Vencido" : "");
            Object[] linha = {a.getEmpresaNome(), a.getNomeAlimento(), String.format(Locale.forLanguageTag("pt-BR"), "%.2f", a.getQuantidadeKg()), data.format(Validacao.DATA_BR), status};
            todos.addRow(linha);
            if (!vencido && Alimento.DISPONIVEL.equals(a.getStatus())) { disponiveis.addRow(linha); qtd++; }
        }
        resumo.setText(modeloEmpresas.size() + " empresas · " + qtd + " lotes disponíveis" + (empresa != null ? " nesta empresa" : " no total"));
    }
    private boolean disponivel(Alimento a) {
        return Alimento.DISPONIVEL.equals(a.getStatus()) && !LocalDate.parse(a.getDataValidade()).isBefore(LocalDate.now());
    }
    private void renderizarCatalogo() {
        catalogo.removeAll();
        for (int i=0;i<modeloEmpresas.size();i++) {
            Empresa empresa = modeloEmpresas.get(i);
            long qtd = alimentos.stream().filter(a -> a.getComercioId()==empresa.getId() && disponivel(a)).count();
            JPanel card = UI.cartao(new BorderLayout(0,20),24);
            JPanel info = UI.painel(null); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
            info.add(UI.selo(empresa.isDemonstracao() ? "Demonstração" : "Empresa cadastrada", false)); info.add(Box.createVerticalStrut(18));
            info.add(UI.titulo(empresa.getNome(),24)); info.add(Box.createVerticalStrut(8));
            info.add(UI.texto(empresa.getCidade().isBlank() ? "Cidade não informada" : empresa.getCidade())); info.add(Box.createVerticalStrut(16));
            info.add(UI.selo(qtd==0 ? "Sem lotes disponíveis" : qtd+" lotes disponíveis",true));
            for (Component c:info.getComponents()) if (c instanceof JComponent) ((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(info,BorderLayout.CENTER);
            JButton abrir = UI.botao("Ver perfil e alimentos",UI.AZUL);
            abrir.addActionListener(e -> new RetiradaUI(this,empresa,false,this::carregar).setVisible(true)); card.add(abrir,BorderLayout.SOUTH);
            GridBagConstraints g = new GridBagConstraints(); g.gridx=i%2; g.gridy=i/2; g.weightx=0.5; g.fill=GridBagConstraints.HORIZONTAL;
            g.anchor=GridBagConstraints.NORTH; g.insets=new Insets(4,6,16,6); catalogo.add(card,g);
        }
        if (modeloEmpresas.isEmpty()) catalogo.add(UI.texto("Nenhuma empresa cadastrada no momento."));
        GridBagConstraints espaco = new GridBagConstraints(); espaco.gridy=(modeloEmpresas.size()+1)/2+1; espaco.weighty=1;
        catalogo.add(Box.createVerticalGlue(),espaco); catalogo.revalidate(); catalogo.repaint();
        resumo.setText(modeloEmpresas.size()+" empresas · "+alimentos.stream().filter(this::disponivel).count()+" lotes disponíveis");
    }
}
