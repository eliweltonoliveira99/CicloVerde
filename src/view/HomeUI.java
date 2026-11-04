package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomeUI extends JFrame {
    public HomeUI() {
        setTitle("CicloVerde - Início"); setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 780); setMinimumSize(new Dimension(560, 730)); setLocationRelativeTo(null);
        JPanel raiz = UI.fundo(new BorderLayout(0, 30), false);
        raiz.setBorder(new EmptyBorder(45, 40, 30, 40));
        JPanel marca = UI.painel(new GridLayout(0,1,0,5));
        marca.add(UI.texto("Salve alimentos, ajude a comunidade"));
        JLabel nome = UI.titulo("CicloVerde", 44); nome.setForeground(UI.VERDE); marca.add(nome);
        marca.add(UI.texto("Uma nova oportunidade para o que sobra.")); raiz.add(marca, BorderLayout.NORTH);
        JPanel opcoes = UI.cartao(new BorderLayout(0, 22), 24);
        opcoes.add(UI.titulo("O que você deseja fazer?", 22), BorderLayout.NORTH);
        JPanel escolhas = UI.painel(new GridLayout(0,1,0,16));
        JButton empresas = UI.acao("Encontrar alimentos", "Conheça as empresas e reserve um lote.", UI.VERDE);
        empresas.addActionListener(e -> abrir(false)); escolhas.add(empresas);
        JButton admin = UI.acao("Administrar doações", "Cadastre empresas e acompanhe os alimentos.", UI.AZUL);
        admin.addActionListener(e -> abrir(true)); escolhas.add(admin);
        JPanel centro = UI.painel(new BorderLayout()); centro.add(escolhas, BorderLayout.NORTH);
        opcoes.add(centro, BorderLayout.CENTER); raiz.add(opcoes, BorderLayout.CENTER);
        JPanel rodape = UI.painel(new GridLayout(0, 1, 0, 6));
        rodape.add(UI.texto("Retirada no estabelecimento · Conexões locais"));
        rodape.add(UI.texto("UPX2 · Facens · Administrador de demonstração sem login"));
        raiz.add(rodape, BorderLayout.SOUTH); setContentPane(raiz);
    }
    private void abrir(boolean admin) {
        new EmpresasUI(admin).setVisible(true); dispose();
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> { UI.iniciar(); new HomeUI().setVisible(true); }); }
}
