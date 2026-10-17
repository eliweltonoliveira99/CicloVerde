package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HomeUI extends JFrame {

    public HomeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("CicloVerde - Início");
        setSize(480, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Painel principal com gradiente
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 255, 240),
                        0, getHeight(), new Color(220, 245, 220));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 20));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // ─── Header ───
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("CicloVerde");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(new Color(30, 120, 30));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Salve Alimentos, Ajude a Comunidade");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubtitulo.setForeground(new Color(100, 130, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSubtitulo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        // ─── Painel Central com Botões ───
        JPanel botoesPanel = new JPanel();
        botoesPanel.setLayout(new BoxLayout(botoesPanel, BoxLayout.Y_AXIS));
        botoesPanel.setOpaque(false);

        JLabel lblEscolha = new JLabel("O que você deseja fazer?");
        lblEscolha.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblEscolha.setForeground(new Color(50, 90, 50));
        lblEscolha.setAlignmentX(Component.CENTER_ALIGNMENT);

        botoesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        botoesPanel.add(lblEscolha);
        botoesPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Botão Cadastrar
        JButton btnCadastrar = criarBotao(
                "Cadastrar Produto",
                "Disponibilize alimentos para doação",
                new Color(34, 139, 34),
                new Color(28, 115, 28));
        btnCadastrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCadastrar.addActionListener(e -> {
            new CadastroUI().setVisible(true);
            dispose();
        });

        botoesPanel.add(btnCadastrar);
        botoesPanel.add(Box.createRigidArea(new Dimension(0, 18)));

        // Botão Retirada
        JButton btnRetirada = criarBotao(
                "Retirar Produto",
                "Veja itens disponíveis para retirada",
                new Color(22, 90, 170),
                new Color(16, 72, 140));
        btnRetirada.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRetirada.addActionListener(e -> {
            new RetiradaUI().setVisible(true);
            dispose();
        });

        botoesPanel.add(btnRetirada);

        // ─── Rodapé ───
        JPanel rodapePanel = new JPanel();
        rodapePanel.setOpaque(false);
        JLabel lblRodape = new JLabel("Transforme desperdício em impacto social");
        lblRodape.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblRodape.setForeground(new Color(120, 150, 120));
        rodapePanel.add(lblRodape);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(botoesPanel, BorderLayout.CENTER);
        mainPanel.add(rodapePanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JButton criarBotao(String titulo, String descricao, Color corFundo, Color corHover) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // sem borda padrão
            }
        };

        // Layout interno do botão
        btn.setLayout(new BorderLayout(8, 0));
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(corFundo);
        btn.setPreferredSize(new Dimension(360, 70));
        btn.setMaximumSize(new Dimension(360, 70));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(new EmptyBorder(0, 20, 0, 0));

        JLabel lblDesc = new JLabel(descricao);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(255, 255, 255, 180));
        lblDesc.setBorder(new EmptyBorder(0, 20, 0, 0));

        JPanel textoPanel = new JPanel();
        textoPanel.setLayout(new BoxLayout(textoPanel, BoxLayout.Y_AXIS));
        textoPanel.setOpaque(false);
        textoPanel.add(Box.createVerticalGlue());
        textoPanel.add(lblTitulo);
        textoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        textoPanel.add(lblDesc);
        textoPanel.add(Box.createVerticalGlue());

        btn.add(textoPanel, BorderLayout.CENTER);

        // Hover
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(corHover);
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(corFundo);
                btn.repaint();
            }
        });

        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeUI().setVisible(true));
    }
}
