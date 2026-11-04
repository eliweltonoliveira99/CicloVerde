package view;

import dao.AlimentoDAO;
import model.Alimento;
import model.Empresa;
import model.Validacao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
public class RetiradaUI extends JDialog {
    private final Empresa empresa;
    private final Runnable aoAlterar;
    private final JPanel disponiveis = UI.painel(new GridBagLayout()), historico = UI.painel(new GridBagLayout());
    private final JLabel resumo = UI.texto("Carregando alimentos…");
    private final JButton atualizar = UI.botao("Atualizar", UI.AZUL);
    private boolean carregando;
    public RetiradaUI(Window pai, Empresa empresa, boolean admin, Runnable aoAlterar) {
        super(pai, "CicloVerde - Perfil da empresa", ModalityType.APPLICATION_MODAL);
        this.empresa = empresa; this.aoAlterar = aoAlterar;
        setSize(850, 800); setMinimumSize(new Dimension(750, 690)); setLocationRelativeTo(pai); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel raiz = UI.fundo(new BorderLayout(0, 10), true);
        JPanel topo = UI.painel(new BorderLayout());
        topo.add(UI.cabecalho("EMPRESAS / PERFIL" + (empresa.isDemonstracao() ? " / DEMONSTRAÇÃO" : ""), empresa.getNome(),
                empresa.getCidade().isBlank() ? "Cidade não informada" : empresa.getCidade(), true), BorderLayout.NORTH);
        JPanel info = UI.cartao(null, 18); info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.add(UI.texto("Responsável: " + informado(empresa.getResponsavel())));
        info.add(new JLabel("<html><div style='width:520px'>Local de retirada: " + UI.html(informado(empresa.getEndereco())) + "</div></html>"));
        if (!empresa.getDescricao().isBlank()) info.add(new JLabel("<html><div style='width:520px'>" + UI.html(empresa.getDescricao()) + "</div></html>"));
        if (empresa.isDemonstracao()) info.add(UI.texto("Alimentos, quantidades e validades deste perfil são dados de demonstração."));
        if (admin) {
            JButton novo = UI.botao("Cadastrar alimento nesta empresa", UI.VERDE);
            novo.addActionListener(e -> new CadastroUI(this, empresa, () -> { carregar(); aoAlterar.run(); }).setVisible(true)); info.add(novo);
        }
        for (Component c : info.getComponents()) if (c instanceof JComponent) ((JComponent)c).setAlignmentX(Component.LEFT_ALIGNMENT);
        for (int i=info.getComponentCount()-1;i>0;i--) info.add(Box.createVerticalStrut(8),i);
        JPanel blocoInfo = UI.painel(new BorderLayout()); blocoInfo.setBorder(new EmptyBorder(16,22,4,22)); blocoInfo.add(info);
        topo.add(blocoInfo, BorderLayout.CENTER); raiz.add(topo, BorderLayout.NORTH);
        JTabbedPane abas = new JTabbedPane(); abas.addTab("Disponíveis para reserva", scroll(disponiveis));
        if (admin) abas.addTab("Histórico de lotes", scroll(historico));
        abas.setBorder(new EmptyBorder(0, 22, 0, 22)); raiz.add(abas, BorderLayout.CENTER);
        JPanel rodape = UI.painel(new BorderLayout(14, 0)); rodape.setBorder(new EmptyBorder(8, 26, 18, 26));
        JButton voltar = UI.secundario("Voltar às empresas"); voltar.addActionListener(e -> dispose());
        rodape.add(voltar, BorderLayout.WEST); rodape.add(resumo, BorderLayout.CENTER); rodape.add(atualizar, BorderLayout.EAST);
        raiz.add(rodape, BorderLayout.SOUTH); setContentPane(raiz); atualizar.addActionListener(e -> carregar()); carregar();
    }
    private static String informado(String valor) { return valor == null || valor.isBlank() ? "Não informado" : valor; }
    private static JScrollPane scroll(JPanel painel) {
        JScrollPane scroll = new JScrollPane(painel); scroll.setBorder(null); scroll.getViewport().setBackground(UI.FUNDO);
        scroll.getViewport().setOpaque(false); scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(18); return scroll;
    }
    private void carregar() {
        if (carregando) return;
        carregando = true; disponiveis.removeAll(); historico.removeAll(); resumo.setText("Carregando alimentos…");
        disponiveis.revalidate(); disponiveis.repaint(); historico.revalidate(); historico.repaint();
        UI.tarefa(this, atualizar, () -> new AlimentoDAO().listarHistoricoEmpresa(empresa.getId()), this::renderizar, mensagem -> {
            carregando = false; resumo.setText("Falha ao carregar. Tente Atualizar.");
            adicionar(disponiveis, UI.texto("Não foi possível consultar os alimentos."), 0); finalizar(disponiveis, 1);
            JOptionPane.showMessageDialog(this, mensagem, "Falha na consulta", JOptionPane.ERROR_MESSAGE);
        });
    }
    private void renderizar(List<Alimento> itens) {
        carregando = false; int qtd = 0, h = 0;
        for (Alimento a : itens) {
            boolean disponivel = Alimento.DISPONIVEL.equals(a.getStatus()) && !LocalDate.parse(a.getDataValidade()).isBefore(LocalDate.now());
            if (disponivel) adicionar(disponiveis, card(a, true), qtd++);
            adicionar(historico, card(a, false), h++);
        }
        if (qtd == 0) adicionar(disponiveis, UI.texto("Esta empresa não tem lotes disponíveis dentro da validade."), 0);
        if (h == 0) adicionar(historico, UI.texto("Ainda não há alimentos cadastrados para esta empresa."), 0);
        finalizar(disponiveis, Math.max(1, qtd)); finalizar(historico, Math.max(1, h));
        resumo.setText(qtd + " lotes disponíveis para reserva");
    }
    private static void adicionar(JPanel painel, JComponent item, int linha) {
        GridBagConstraints g = new GridBagConstraints(); g.gridx = 0; g.gridy = linha; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTH; g.insets = new Insets(6, 6, 6, 6); painel.add(item, g);
    }
    private static void finalizar(JPanel painel, int linha) {
        GridBagConstraints g = new GridBagConstraints(); g.gridy = linha; g.weighty = 1; painel.add(Box.createVerticalGlue(), g);
        painel.revalidate(); painel.repaint();
    }
    private JPanel card(Alimento alimento, boolean permitirReserva) {
        JPanel card = UI.cartao(new BorderLayout(18, 0), 18);
        JPanel info = UI.painel(new GridLayout(0, 1, 0, 6));
        info.add(UI.titulo(alimento.getNomeAlimento(), 17));
        info.add(UI.texto(String.format(Locale.forLanguageTag("pt-BR"), "%.2f kg · Validade: %s", alimento.getQuantidadeKg(), LocalDate.parse(alimento.getDataValidade()).format(Validacao.DATA_BR))));
        boolean vencido = LocalDate.parse(alimento.getDataValidade()).isBefore(LocalDate.now());
        info.add(UI.texto(alimento.getStatus() + (vencido ? " · Vencido" : ""))); card.add(info, BorderLayout.CENTER);
        if (permitirReserva) {
            JButton reservar = UI.botao("Reservar lote", UI.AZUL);
            reservar.addActionListener(e -> reservar(alimento, reservar));
            JPanel acao = UI.painel(new GridBagLayout()); acao.add(reservar); card.add(acao, BorderLayout.EAST);
        }
        return card;
    }
    private void reservar(Alimento alimento, JButton botao) {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Reservar o lote completo de " + alimento.getNomeAlimento() + "?\nEmpresa: " + empresa.getNome()
                        + "\nA reserva não confirma a entrega física dos alimentos.", "Confirmar reserva", JOptionPane.YES_NO_OPTION);
        if (resposta != JOptionPane.YES_OPTION) return;
        UI.tarefa(this, botao, () -> new AlimentoDAO().reservar(alimento.getId(), empresa.getId()), sucesso -> {
            JOptionPane.showMessageDialog(this, sucesso ? "Reserva registrada. Combine a coleta com a empresa.\nEste lote não está mais disponível para novas reservas."
                    : "O lote já foi reservado, venceu ou não está mais disponível. A lista será atualizada.",
                    sucesso ? "Reserva concluída" : "Lote indisponível", sucesso ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            carregar(); aoAlterar.run();
        });
    }
}
