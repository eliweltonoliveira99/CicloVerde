package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

final class UI {
    static final Color VERDE = new Color(34, 139, 34), AZUL = new Color(22, 90, 170);
    static final Color FUNDO = new Color(240, 252, 240), TEXTO = new Color(40, 75, 48);
    static final Color SUAVE = new Color(106, 128, 111);
    private UI() {}
    static void iniciar() {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("ComboBox.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("List.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TextField.background", new Color(250, 253, 250));
        UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(211, 226, 213)), new EmptyBorder(6, 10, 6, 10)));
        UIManager.put("Button.disabledText", new Color(119, 140, 123));
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 13));
    }
    static JPanel painel(LayoutManager layout) { JPanel p = new JPanel(layout); p.setOpaque(false); return p; }
    static JPanel fundo(LayoutManager layout, boolean azul) {
        return gradiente(layout, azul ? new Color(235, 248, 250) : FUNDO,
                azul ? new Color(215, 238, 248) : new Color(218, 244, 218), false);
    }
    private static JPanel gradiente(LayoutManager layout, Color inicio, Color fim, boolean horizontal) {
        JPanel painel = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, inicio, horizontal ? getWidth() : 0, horizontal ? 0 : getHeight(), fim));
                g2.fillRect(0, 0, getWidth(), getHeight()); g2.dispose();
            }
        };
        painel.setOpaque(false); return painel;
    }
    static JPanel cartao(LayoutManager layout, int margem) {
        JPanel painel = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(25, 59, 39, 12)); g2.fillRoundRect(2, 5, getWidth()-4, getHeight()-6, 22, 22);
                g2.setColor(Color.WHITE); g2.fillRoundRect(1, 1, getWidth()-3, getHeight()-6, 22, 22);
                g2.setColor(new Color(225, 236, 227)); g2.drawRoundRect(1, 1, getWidth()-4, getHeight()-7, 22, 22);
                g2.dispose(); super.paintComponent(g);
            }
        };
        painel.setOpaque(false); painel.setBorder(new EmptyBorder(margem, margem, margem+5, margem)); return painel;
    }
    static JLabel titulo(String texto, int tamanho) {
        JLabel l = new JLabel(texto); l.setFont(new Font("Segoe UI", Font.BOLD, tamanho)); l.setForeground(TEXTO); return l;
    }
    static JLabel texto(String texto) { JLabel l = new JLabel(texto); l.setForeground(SUAVE); return l; }
    static JLabel selo(String texto, boolean azul) {
        JLabel l = new JLabel(texto); l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(azul ? AZUL : VERDE); l.setOpaque(true);
        l.setBackground(azul ? new Color(234,242,252) : new Color(234,247,234));
        l.setBorder(new EmptyBorder(5,10,5,10)); return l;
    }
    static JButton botao(String texto, Color cor) {
        return botao(texto, cor, false);
    }
    static JButton secundario(String texto) { return botao(texto, VERDE, true); }
    private static JButton botao(String texto, Color cor, boolean secundario) {
        JButton b = new JButton(texto) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fundo = !isEnabled() ? new Color(226, 234, 228) : secundario ? Color.WHITE
                        : getModel().isPressed() ? cor.darker() : getModel().isRollover() ? cor.darker() : cor;
                g2.setColor(fundo); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                if (secundario || hasFocus()) {
                    g2.setColor(hasFocus() ? cor.darker() : new Color(203, 222, 206));
                    g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 16, 16);
                }
                g2.dispose(); super.paintComponent(g);
            }
        };
        b.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        b.setOpaque(false); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setRolloverEnabled(true);
        b.setBackground(cor); b.setForeground(secundario ? cor : Color.WHITE); b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(12, 18, 12, 18)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
    static JButton acao(String titulo, String descricao, Color cor) {
        JButton b = botao("", cor); b.setLayout(new BorderLayout());
        JLabel l = new JLabel("<html><b style='font-size:17px'>" + html(titulo)
                + "</b><br><span style='font-size:11px'>" + html(descricao) + "</span></html>");
        l.setForeground(Color.WHITE); b.add(l, BorderLayout.CENTER); b.getAccessibleContext().setAccessibleName(titulo);
        b.setPreferredSize(new Dimension(380, 100)); return b;
    }
    static JPanel cabecalho(String trilha, String titulo, String subtitulo) {
        return cabecalho(trilha, titulo, subtitulo, false);
    }
    static JPanel cabecalho(String trilha, String titulo, String subtitulo, boolean azul) {
        JPanel p = gradiente(new GridLayout(0, 1, 0, 6), azul ? AZUL : new Color(28,120,28),
                azul ? new Color(32,141,208) : new Color(56,180,56), true);
        p.setBorder(new EmptyBorder(20, 28, 22, 28));
        JLabel caminho = texto(trilha); caminho.setForeground(new Color(222, 243, 225)); caminho.setFont(new Font("Segoe UI", Font.BOLD, 11));
        JLabel nome = titulo(titulo, 28); nome.setForeground(Color.WHITE);
        JLabel sub = texto(subtitulo); sub.setForeground(new Color(232, 247, 235));
        p.add(caminho); p.add(nome); p.add(sub); return p;
    }
    static String html(String texto) {
        return texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
    static <T> void tarefa(Component pai, JButton botao, Callable<T> trabalho, Consumer<T> sucesso) {
        tarefa(pai, botao, trabalho, sucesso, mensagem -> JOptionPane.showMessageDialog(pai, mensagem, "Não foi possível concluir", JOptionPane.ERROR_MESSAGE));
    }
    static <T> void tarefa(Component pai, JButton botao, Callable<T> trabalho, Consumer<T> sucesso, Consumer<String> falha) {
        if (botao != null) botao.setEnabled(false);
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception { return trabalho.call(); }
            @Override protected void done() {
                if (botao != null) botao.setEnabled(true);
                try { sucesso.accept(get()); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); falha.accept("Operação interrompida."); }
                catch (ExecutionException e) {
                    Throwable causa = e.getCause();
                    falha.accept(causa.getMessage() == null ? "Falha ao acessar os dados." : causa.getMessage());
                }
            }
        }.execute();
    }
}
