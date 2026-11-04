package view;

import dao.EmpresaDAO;
import model.Empresa;
import model.Validacao;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CadastroEmpresaUI extends JDialog {
    private final JTextField nome = new JTextField(), cidade = new JTextField(), responsavel = new JTextField(),
            endereco = new JTextField(), descricao = new JTextField(), cnpj = new JTextField();
    public CadastroEmpresaUI(Window pai, Runnable aoSalvar) {
        super(pai, "CicloVerde - Cadastrar empresa", ModalityType.APPLICATION_MODAL);
        setSize(680, 720); setMinimumSize(new Dimension(600, 660)); setLocationRelativeTo(pai); setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        JPanel raiz = UI.fundo(new BorderLayout(0,16), false);
        raiz.add(UI.cabecalho("ADMINISTRADOR / EMPRESAS", "Cadastrar empresa", "Nome e cidade são obrigatórios. Complete os demais dados se disponíveis."), BorderLayout.NORTH);
        JPanel form = UI.cartao(new GridBagLayout(), 20);
        GridBagConstraints g = new GridBagConstraints(); g.gridx = 0; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        String[] rotulos = {"Nome da empresa *", "Cidade/UF *", "Responsável", "Local de retirada", "Descrição da empresa", "CNPJ (opcional)"};
        JTextField[] campos = {nome, cidade, responsavel, endereco, descricao, cnpj};
        for (int i = 0; i < campos.length; i++) {
            g.gridy = i * 2; g.insets = new Insets(5, 0, 4, 0); form.add(UI.titulo(rotulos[i], 13), g);
            g.gridy++; g.insets = new Insets(0, 0, 5, 0); campos[i].setPreferredSize(new Dimension(400, 32)); form.add(campos[i], g);
        }
        JPanel corpo = UI.painel(new BorderLayout()); corpo.setBorder(new EmptyBorder(0,22,0,22)); corpo.add(form); raiz.add(corpo, BorderLayout.CENTER);
        JButton salvar = UI.botao("Cadastrar empresa", UI.VERDE); JButton cancelar = UI.secundario("Cancelar"); cancelar.addActionListener(e -> dispose());
        JPanel botoes = UI.painel(new FlowLayout(FlowLayout.RIGHT, 14, 18)); botoes.add(cancelar); botoes.add(salvar); raiz.add(botoes, BorderLayout.SOUTH);
        salvar.addActionListener(e -> {
            try {
                String n = Validacao.texto(nome.getText(), "o nome da empresa", 100, true);
                String c = Validacao.texto(cidade.getText(), "a cidade/UF", 100, true);
                Empresa empresa = new Empresa(0, n, Validacao.texto(cnpj.getText(), "CNPJ", 20, false), c,
                        Validacao.texto(responsavel.getText(), "o responsável", 100, false),
                        Validacao.texto(endereco.getText(), "o local de retirada", 255, false),
                        Validacao.texto(descricao.getText(), "a descrição", 500, false), false);
                UI.tarefa(this, salvar, () -> new EmpresaDAO().cadastrar(empresa), id -> {
                    JOptionPane.showMessageDialog(this, "Empresa cadastrada. Agora você pode disponibilizar alimentos para ela."); aoSalvar.run(); dispose();
                });
            } catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Confira os dados", JOptionPane.WARNING_MESSAGE); }
        }); setContentPane(raiz);
    }
}
