package view;

import dao.AlimentoDAO;
import model.Alimento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class RetiradaUI extends JFrame {

    private JPanel painelItens;

    public RetiradaUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("CicloVerde - Retirada de Produtos");
        setSize(560, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ─── Painel principal com gradiente ───
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(235, 248, 250),
                        0, getHeight(), new Color(215, 238, 248));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // ─── Header ───
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(22, 90, 170),
                        getWidth(), 0, new Color(14, 130, 200));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(22, 30, 22, 30));
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Retirada de Produtos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Itens disponíveis para retirada pela sua ONG");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubtitulo.setForeground(new Color(200, 230, 255));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSubtitulo);

        // ─── Área de Itens com scroll ───
        painelItens = new JPanel();
        painelItens.setLayout(new BoxLayout(painelItens, BoxLayout.Y_AXIS));
        painelItens.setOpaque(false);
        painelItens.setBorder(new EmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(painelItens);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // ─── Rodapé com botão Voltar ───
        JPanel rodapePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 12));
        rodapePanel.setOpaque(false);

        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnVoltar.setBackground(new Color(22, 90, 170));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.setPreferredSize(new Dimension(110, 38));
        btnVoltar.addActionListener(e -> {
            new HomeUI().setVisible(true);
            dispose();
        });

        rodapePanel.add(btnVoltar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(rodapePanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Carrega os itens do banco
        carregarItens();
    }

    private void carregarItens() {
        painelItens.removeAll();

        AlimentoDAO dao = new AlimentoDAO();
        List<Alimento> itens = dao.listarDisponiveis();

        // Ordena por data de validade (vencimento mais próximo primeiro)
        if (itens != null && !itens.isEmpty()) {
            SimpleDateFormat sdfParse = new SimpleDateFormat("yyyy-MM-dd");
            itens.sort((a, b) -> {
                try {
                    Date da = sdfParse.parse(a.getDataValidade());
                    Date db = sdfParse.parse(b.getDataValidade());
                    return da.compareTo(db);
                } catch (ParseException ex) {
                    return 0;
                }
            });
        }

        if (itens == null || itens.isEmpty()) {
            JPanel semItens = new JPanel();
            semItens.setOpaque(false);
            semItens.setLayout(new BoxLayout(semItens, BoxLayout.Y_AXIS));

            JLabel lblVazio = new JLabel("Nenhum item disponível no momento.");
            lblVazio.setFont(new Font("Segoe UI", Font.ITALIC, 15));
            lblVazio.setForeground(new Color(100, 130, 150));
            lblVazio.setAlignmentX(Component.CENTER_ALIGNMENT);

            semItens.add(Box.createRigidArea(new Dimension(0, 80)));
            semItens.add(lblVazio);

            painelItens.add(semItens);
        } else {
            for (Alimento item : itens) {
                painelItens.add(criarCardItem(item));
                painelItens.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }

        painelItens.revalidate();
        painelItens.repaint();
    }

    private JPanel criarCardItem(Alimento alimento) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Sombra suave
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(3, 5, getWidth() - 3, getHeight() - 3, 16, 16);
                // Fundo branco
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BorderLayout(12, 0));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14, 18, 14, 18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // ─── Informações do item ───
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblNome = new JLabel(alimento.getNomeAlimento());
        lblNome.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNome.setForeground(new Color(30, 60, 30));

        JLabel lblQtd = new JLabel(String.format("%.1f kg disponíveis", alimento.getQuantidadeKg()));
        lblQtd.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblQtd.setForeground(new Color(80, 100, 80));

        // Formata a data para o padrão brasileiro (dd/MM/yyyy)
        String dataFormatada = alimento.getDataValidade();
        try {
            SimpleDateFormat sdfOriginal = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdfBrasileiro = new SimpleDateFormat("dd/MM/yyyy");
            Date dataVal = sdfOriginal.parse(alimento.getDataValidade());
            dataFormatada = sdfBrasileiro.format(dataVal);
        } catch (ParseException ex) {
            // Se falhar o parse, mantém o formato original
        }

        JLabel lblValidade = new JLabel("Validade: " + dataFormatada);
        lblValidade.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblValidade.setForeground(new Color(150, 100, 50));

        // Badge de status
        JLabel lblStatus = new JLabel("● " + alimento.getStatus());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(new Color(34, 139, 34));

        infoPanel.add(lblNome);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(lblQtd);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        infoPanel.add(lblValidade);

        // ─── Coluna Direita: botão Reservar ───
        JButton btnReservar = new JButton("Reservar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // sem borda
            }
        };
        btnReservar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReservar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnReservar.setBackground(new Color(22, 90, 170));
        btnReservar.setForeground(Color.WHITE);
        btnReservar.setFocusPainted(false);
        btnReservar.setOpaque(false);
        btnReservar.setContentAreaFilled(false);
        btnReservar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReservar.setPreferredSize(new Dimension(100, 38));
        btnReservar.setMaximumSize(new Dimension(100, 38));
        // Botão ainda não funcional - apenas visual
        btnReservar.setEnabled(true);
        btnReservar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnReservar.setBackground(new Color(16, 72, 140));
                btnReservar.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnReservar.setBackground(new Color(22, 90, 170));
                btnReservar.repaint();
            }
        });

        lblStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReservar.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel direitaPanel = new JPanel();
        direitaPanel.setLayout(new BoxLayout(direitaPanel, BoxLayout.Y_AXIS));
        direitaPanel.setOpaque(false);
        direitaPanel.add(Box.createVerticalGlue());
        direitaPanel.add(lblStatus);
        direitaPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        direitaPanel.add(btnReservar);
        direitaPanel.add(Box.createVerticalGlue());

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(direitaPanel, BorderLayout.EAST);

        return card;
    }
}
