package view;

import dao.AlimentoDAO;
import dao.EmpresaDAO;
import model.Alimento;
import model.Empresa;
import model.Validacao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CadastroUI extends JDialog {
    private final JComboBox<Empresa> empresas = new JComboBox<>();
    private final JTextField nome = new JTextField(), quantidade = new JTextField(), validade = new JTextField();
    private final JLabel erro = new JLabel(" ");
    private final JButton salvar = UI.botao("Disponibilizar alimento", UI.VERDE);
    private final Runnable aoSalvar;
    private final int empresaInicial;
    public CadastroUI(Window pai, Empresa empresa, Runnable aoSalvar) {
        super(pai, "CicloVerde - Cadastro de alimento", ModalityType.APPLICATION_MODAL);
        this.aoSalvar = aoSalvar; empresaInicial = empresa == null ? -1 : empresa.getId();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setSize(640, 640); setMinimumSize(new Dimension(580, 600)); setLocationRelativeTo(pai);
        JPanel raiz = UI.fundo(new BorderLayout(0,16), false);
        raiz.add(UI.cabecalho("ADMINISTRADOR / ALIMENTOS", "Disponibilizar alimento", "Escolha a empresa e informe os dados do lote."), BorderLayout.NORTH);
        JPanel campos = UI.cartao(new GridBagLayout(), 22);
        GridBagConstraints g = new GridBagConstraints(); g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        adicionar(campos, g, "Empresa responsável *", empresas);
        adicionar(campos, g, "Nome do alimento *", nome);
        adicionar(campos, g, "Quantidade em kg * (ex.: 2,5)", quantidade);
        adicionar(campos, g, "Validade * (dd/MM/aaaa)", validade);
        erro.setForeground(new Color(163, 44, 38)); g.gridy++; campos.add(erro, g);
        JPanel corpo = UI.painel(new BorderLayout()); corpo.setBorder(new EmptyBorder(0,22,0,22)); corpo.add(campos); raiz.add(corpo, BorderLayout.CENTER);
        JPanel rodape = UI.painel(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        JButton cancelar = UI.secundario("Cancelar"); cancelar.addActionListener(e -> dispose()); rodape.add(cancelar); rodape.add(salvar);
        raiz.add(rodape, BorderLayout.SOUTH); setContentPane(raiz);
        salvar.setEnabled(false); salvar.addActionListener(e -> salvar());
        validade.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (!validade.getText().isBlank()) try { Validacao.data(validade.getText()); erro.setText(" "); }
                catch (IllegalArgumentException ex) { erro.setText(ex.getMessage()); }
            }
        });
        UI.tarefa(this, null, () -> new EmpresaDAO().listar(), this::carregar, mensagem -> { erro.setText("Falha ao carregar empresas. Reabra o cadastro para tentar novamente."); JOptionPane.showMessageDialog(this, mensagem); });
    }
    private void adicionar(JPanel p, GridBagConstraints g, String rotulo, JComponent campo) {
        g.gridy++; g.insets = new Insets(8, 0, 4, 0); p.add(UI.titulo(rotulo, 13), g);
        g.gridy++; g.insets = new Insets(0, 0, 6, 0); campo.setPreferredSize(new Dimension(400, 34)); p.add(campo, g);
    }
    private void carregar(List<Empresa> lista) {
        for (Empresa empresa : lista) { empresas.addItem(empresa); if (empresa.getId() == empresaInicial) empresas.setSelectedItem(empresa); }
        salvar.setEnabled(!lista.isEmpty());
        if (lista.isEmpty()) erro.setText("Cadastre uma empresa antes de disponibilizar alimentos.");
    }
    private void salvar() {
        try {
            Empresa empresa = (Empresa) empresas.getSelectedItem();
            if (empresa == null) throw new IllegalArgumentException("Selecione uma empresa cadastrada.");
            String produto = Validacao.texto(nome.getText(), "o nome do alimento", 100, true);
            double kg = Validacao.quantidade(quantidade.getText()); LocalDate data = Validacao.data(validade.getText());
            Alimento alimento = new Alimento(empresa.getId(), produto, kg, data.toString());
            erro.setText(" "); empresas.setEnabled(false);
            UI.tarefa(this, salvar, () -> new AlimentoDAO().cadastrarDoacao(alimento), sucesso -> {
                empresas.setEnabled(true);
                if (!sucesso) { erro.setText("O cadastro não foi confirmado. Tente novamente."); return; }
                JOptionPane.showMessageDialog(this, "Alimento cadastrado para " + empresa.getNome() + ".\nO lote está disponível para consulta e reserva.", "Cadastro concluído", JOptionPane.INFORMATION_MESSAGE);
                aoSalvar.run(); dispose();
            }, mensagem -> { empresas.setEnabled(true); JOptionPane.showMessageDialog(this, mensagem, "Cadastro não concluído", JOptionPane.ERROR_MESSAGE); });
        } catch (IllegalArgumentException e) { erro.setText(e.getMessage()); }
    }
}
