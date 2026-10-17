package view;

import dao.AlimentoDAO;
import model.Alimento;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CadastroUI extends JFrame {

    private JTextField txtNomeAlimento;
    private JTextField txtQuantidade;
    private JTextField txtValidade;
    private JButton btnDoar;

    // constantes
    private final String EX_NOME = "Ex: Pães, Frutas, Frios";
    private final String EX_QTD = "Ex: 2.5";
    private final String EX_DATA = "dd/MM/aaaa";

    private static final DateTimeFormatter FMT_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public CadastroUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("CicloVerde - Cadastro de Produto");
        setSize(560, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ─── Painel principal com gradiente verde ───
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 252, 240),
                        0, getHeight(), new Color(218, 244, 218));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // ─── Header com gradiente verde ───
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(28, 120, 28),
                        getWidth(), 0, new Color(56, 180, 56));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(22, 30, 22, 30));
        headerPanel.setOpaque(false);

        JLabel lblTitulo = new JLabel("Cadastro de Produto");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitulo = new JLabel("Disponibilize alimentos excedentes para ONGs");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblSubtitulo.setForeground(new Color(200, 240, 200));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(lblSubtitulo);

        // ─── Painel central: formulário com cards ───
        JPanel conteudoPanel = new JPanel();
        conteudoPanel.setLayout(new BoxLayout(conteudoPanel, BoxLayout.Y_AXIS));
        conteudoPanel.setOpaque(false);
        conteudoPanel.setBorder(new EmptyBorder(16, 20, 8, 20));

        Font labelFont = new Font("Segoe UI", Font.BOLD, 13);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // card: nome
        JPanel cardNome = criarCardCampo();
        JLabel lblNome = new JLabel("Produto Excedente");
        lblNome.setFont(labelFont);
        lblNome.setForeground(new Color(40, 90, 40));
        lblNome.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNomeAlimento = new JTextField();
        txtNomeAlimento.setFont(fieldFont);
        txtNomeAlimento.setOpaque(false);
        txtNomeAlimento.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(34, 139, 34, 100)));
        txtNomeAlimento.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtNomeAlimento.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        configurarPlaceholder(txtNomeAlimento, EX_NOME);
        cardNome.add(lblNome);
        cardNome.add(Box.createRigidArea(new Dimension(0, 6)));
        cardNome.add(txtNomeAlimento);

        // card: quantidade
        JPanel cardQtd = criarCardCampo();
        JLabel lblQuantidade = new JLabel("Quantidade (em Kg)");
        lblQuantidade.setFont(labelFont);
        lblQuantidade.setForeground(new Color(40, 90, 40));
        lblQuantidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtQuantidade = new JTextField();
        txtQuantidade.setFont(fieldFont);
        txtQuantidade.setOpaque(false);
        txtQuantidade.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(34, 139, 34, 100)));
        txtQuantidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtQuantidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        configurarPlaceholder(txtQuantidade, EX_QTD);
        cardQtd.add(lblQuantidade);
        cardQtd.add(Box.createRigidArea(new Dimension(0, 6)));
        cardQtd.add(txtQuantidade);

        // card: validade (JTextField com validação)
        JPanel cardValidade = criarCardCampo();
        cardValidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        JLabel lblValidade = new JLabel("Data de Validade");
        lblValidade.setFont(labelFont);
        lblValidade.setForeground(new Color(40, 90, 40));
        lblValidade.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHint = new JLabel("Formato: dd/MM/aaaa");
        lblHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHint.setForeground(new Color(140, 160, 140));

        // linha com label + hint alinhados
        JPanel linhaLabel = new JPanel(new BorderLayout());
        linhaLabel.setOpaque(false);
        linhaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        linhaLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        linhaLabel.add(lblValidade, BorderLayout.WEST);
        linhaLabel.add(lblHint, BorderLayout.EAST);

        // label de erro inline (inicialmente invisível)
        JLabel lblErroData = new JLabel(" ");
        lblErroData.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblErroData.setForeground(new Color(180, 40, 40));
        lblErroData.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtValidade = new JTextField();
        txtValidade.setFont(fieldFont);
        txtValidade.setOpaque(false);
        txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(34, 139, 34, 100)));
        txtValidade.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtValidade.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        configurarPlaceholder(txtValidade, EX_DATA);

        // feedback visual ao perder foco: valida a data
        txtValidade.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String texto = txtValidade.getText().trim();
                if (texto.isEmpty() || texto.equals(EX_DATA)) {
                    txtValidade.setForeground(Color.GRAY);
                    txtValidade.setText(EX_DATA);
                    txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(34, 139, 34, 100)));
                    lblErroData.setText(" ");
                    return;
                }
                String erro = obterErroData(texto);
                if (erro != null) {
                    txtValidade.setForeground(new Color(180, 40, 40));
                    txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180, 40, 40)));
                    lblErroData.setText(erro);
                } else {
                    txtValidade.setForeground(new Color(30, 120, 30));
                    txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(30, 120, 30)));
                    lblErroData.setText(" ");
                }
            }

            @Override
            public void focusGained(FocusEvent e) {
                if (txtValidade.getText().equals(EX_DATA)) {
                    txtValidade.setText("");
                    txtValidade.setForeground(Color.BLACK);
                } else {
                    txtValidade.setForeground(Color.BLACK);
                }
                txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(34, 139, 34)));
                lblErroData.setText(" ");
            }
        });

        cardValidade.add(linhaLabel);
        cardValidade.add(Box.createRigidArea(new Dimension(0, 6)));
        cardValidade.add(txtValidade);
        cardValidade.add(Box.createRigidArea(new Dimension(0, 4)));
        cardValidade.add(lblErroData);

        conteudoPanel.add(cardNome);
        conteudoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        conteudoPanel.add(cardQtd);
        conteudoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        conteudoPanel.add(cardValidade);

        JScrollPane scrollPane = new JScrollPane(conteudoPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // ─── Rodapé: botão Disponibilizar + Voltar ───
        JPanel rodapePanel = new JPanel();
        rodapePanel.setLayout(new BoxLayout(rodapePanel, BoxLayout.Y_AXIS));
        rodapePanel.setOpaque(false);
        rodapePanel.setBorder(new EmptyBorder(4, 20, 14, 20));

        // botão principal
        btnDoar = new JButton("Disponibilizar para ONGs") {
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
        btnDoar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnDoar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnDoar.setBackground(new Color(34, 139, 34));
        btnDoar.setForeground(Color.WHITE);
        btnDoar.setFocusPainted(false);
        btnDoar.setOpaque(false);
        btnDoar.setContentAreaFilled(false);
        btnDoar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDoar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnDoar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDoar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnDoar.setBackground(new Color(24, 110, 24));
                btnDoar.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnDoar.setBackground(new Color(34, 139, 34));
                btnDoar.repaint();
            }
        });

        // acao do botao
        btnDoar.addActionListener(e -> {
            String nome = txtNomeAlimento.getText().trim();
            String qtdStr = txtQuantidade.getText().replace(",", ".").trim();
            String dataStr = txtValidade.getText().trim();

            // valida campos vazios / placeholder
            if (nome.isEmpty() || nome.equals(EX_NOME)) {
                mostrarErro("Por favor, informe o nome do produto.");
                txtNomeAlimento.requestFocus();
                return;
            }
            if (qtdStr.isEmpty() || qtdStr.equals(EX_QTD)) {
                mostrarErro("Por favor, informe a quantidade em Kg.");
                txtQuantidade.requestFocus();
                return;
            }
            if (dataStr.isEmpty() || dataStr.equals(EX_DATA)) {
                mostrarErro("Por favor, informe a data de validade no formato dd/MM/aaaa.");
                txtValidade.requestFocus();
                return;
            }

            // valida número
            double kg;
            try {
                kg = Double.parseDouble(qtdStr);
                if (kg <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                mostrarErro("A quantidade deve ser um número positivo (ex: 2 ou 2.5).");
                txtQuantidade.requestFocus();
                return;
            }

            // valida data com mensagem específica
            String erroData = obterErroData(dataStr);
            if (erroData != null) {
                mostrarErro(erroData);
                txtValidade.setForeground(new Color(180, 40, 40));
                txtValidade.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(180, 40, 40)));
                txtValidade.requestFocus();
                return;
            }

            // converte data para ISO (yyyy-MM-dd) para salvar no banco
            LocalDate data = LocalDate.parse(dataStr, FMT_BR);
            String validade = data.toString();

            // cadastra
            Alimento alimento = new Alimento(1, nome, kg, validade);
            AlimentoDAO dao = new AlimentoDAO();
            boolean sucesso = dao.cadastrarDoacao(alimento);

            if (sucesso) {
                JOptionPane.showMessageDialog(this,
                        "Doação cadastrada com sucesso!\nAs instituições da região foram notificadas.",
                        "Impacto Positivo", JOptionPane.INFORMATION_MESSAGE);
                // limpa
                configurarPlaceholder(txtNomeAlimento, EX_NOME);
                configurarPlaceholder(txtQuantidade, EX_QTD);
                configurarPlaceholder(txtValidade, EX_DATA);
                btnDoar.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Erro de comunicação com o Banco de Dados.\nO MySQL está rodando?",
                        "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            }
        });

        // botão voltar
        JButton btnVoltar = new JButton("← Voltar");
        btnVoltar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVoltar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btnVoltar.setBackground(new Color(34, 139, 34));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnVoltar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnVoltar.addActionListener(e -> {
            new HomeUI().setVisible(true);
            dispose();
        });

        rodapePanel.add(btnDoar);
        rodapePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        rodapePanel.add(btnVoltar);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(rodapePanel, BorderLayout.SOUTH);

        add(mainPanel);

        SwingUtilities.invokeLater(() -> mainPanel.requestFocus());
    }

    // Retorna a mensagem de erro específica, ou null se a data for válida
    private String obterErroData(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return "Informe a data de validade.";
        }
        if (!texto.matches("\\d{2}/\\d{2}/\\d{4}")) {
            return "Formato inválido. Use dd/MM/aaaa (ex: 25/12/2025).";
        }
        // extrai dia, mês e ano para validações específicas
        String[] partes = texto.split("/");
        int dia = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int ano = Integer.parseInt(partes[2]);
        if (mes < 1 || mes > 12) {
            return "Mês inválido (" + partes[1] + "). Deve ser entre 01 e 12.";
        }
        if (dia < 1 || dia > 31) {
            return "Dia inválido (" + partes[0] + "). Deve ser entre 01 e 31.";
        }
        if (ano < 2024) {
            return "Ano inválido (" + ano + "). Informe um ano válido.";
        }
        LocalDate data;
        try {
            data = LocalDate.parse(texto, FMT_BR);
        } catch (DateTimeParseException ex) {
            return "Data inexistente no calendário (ex: 30/02 não existe).";
        }
        if (data.isBefore(LocalDate.now())) {
            return "A data de validade não pode estar no passado.";
        }
        return null;
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // Cria um card branco com sombra
    private JPanel criarCardCampo() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillRoundRect(3, 5, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 12, 16));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        return card;
    }

    private void configurarPlaceholder(JTextField campo, String textoExemplo) {
        campo.setText(textoExemplo);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(textoExemplo)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setForeground(Color.GRAY);
                    campo.setText(textoExemplo);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomeUI().setVisible(true));
    }
}
