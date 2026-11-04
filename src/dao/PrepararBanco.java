package dao;

import java.sql.*;
import java.time.LocalDate;
public final class PrepararBanco {
    private PrepararBanco() {}
    public static void preparar(Conexoes conexoes, boolean demonstracao) throws SQLException {
        try (Connection c = conexoes.abrir(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS comercio (id INT AUTO_INCREMENT PRIMARY KEY,nome VARCHAR(100) NOT NULL,cnpj VARCHAR(20) UNIQUE)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS alimento (id INT AUTO_INCREMENT PRIMARY KEY,comercio_id INT,nome_alimento VARCHAR(100) NOT NULL,quantidade_kg DOUBLE NOT NULL,data_validade DATE NOT NULL,status VARCHAR(50) DEFAULT 'Disponível',FOREIGN KEY (comercio_id) REFERENCES comercio(id))");
            adicionar(c, "cidade", "VARCHAR(100) NOT NULL DEFAULT ''");
            adicionar(c, "responsavel", "VARCHAR(100) NOT NULL DEFAULT ''");
            adicionar(c, "endereco", "VARCHAR(255) NOT NULL DEFAULT ''");
            adicionar(c, "descricao", "VARCHAR(500) NOT NULL DEFAULT ''");
            adicionar(c, "demonstracao", "BOOLEAN NOT NULL DEFAULT FALSE");
            try (ResultSet col = c.getMetaData().getColumns(c.getCatalog(), null, "comercio", "cnpj")) {
                if (col.next() && col.getInt("NULLABLE") == DatabaseMetaData.columnNoNulls)
                    s.executeUpdate("ALTER TABLE comercio MODIFY COLUMN cnpj VARCHAR(20) NULL");
            }
            if (demonstracao) demonstracao(c);
        }
    }
    private static void adicionar(Connection c, String coluna, String tipo) throws SQLException {
        try (ResultSet r = c.getMetaData().getColumns(c.getCatalog(), null, "comercio", coluna)) {
            if (r.next()) return;
        }
        try (Statement s = c.createStatement()) { s.executeUpdate("ALTER TABLE comercio ADD COLUMN " + coluna + " " + tipo); }
    }
    private static void demonstracao(Connection c) throws SQLException {
        try (PreparedStatement existe = c.prepareStatement("SELECT id FROM comercio WHERE nome=? AND demonstracao=TRUE")) {
            existe.setString(1, "Hortifrúti da Taty");
            try (ResultSet r = existe.executeQuery()) { if (r.next()) return; }
        }
        boolean autoCommit = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            int id;
            try (PreparedStatement p = c.prepareStatement("INSERT INTO comercio(nome,cidade,responsavel,endereco,descricao,demonstracao) VALUES(?,?,?,?,?,TRUE)", Statement.RETURN_GENERATED_KEYS)) {
                p.setString(1, "Hortifrúti da Taty"); p.setString(2, "Ibiúna/SP"); p.setString(3, "Tatiane (Taty)");
                p.setString(4, "Retirada no estabelecimento. Endereço a combinar.");
                p.setString(5, "Perfil de demonstração inspirado na persona do projeto. Os alimentos e quantidades são fictícios para apresentação.");
                p.executeUpdate();
                try (ResultSet r = p.getGeneratedKeys()) { if (!r.next()) throw new SQLException("Empresa de demonstração sem ID."); id = r.getInt(1); }
            }
            try (PreparedStatement p = c.prepareStatement("INSERT INTO alimento(comercio_id,nome_alimento,quantidade_kg,data_validade,status) VALUES(?,?,?,?,?)")) {
                String[] nomes = {"Bananas (demonstração)", "Tomates (demonstração)", "Cenouras (demonstração)"};
                double[] pesos = {4.5, 3, 2};
                for (int i = 0; i < nomes.length; i++) {
                    p.setInt(1, id); p.setString(2, nomes[i]); p.setDouble(3, pesos[i]);
                    p.setDate(4, Date.valueOf(LocalDate.now().plusDays(i + 2))); p.setString(5, "Disponível"); p.addBatch();
                }
                p.executeBatch();
            }
            c.commit();
        } catch (SQLException e) { c.rollback(); throw e; }
        finally { c.setAutoCommit(autoCommit); }
    }
    public static void main(String[] args) throws Exception {
        preparar(ConnectionFactory::getConnection, true);
        System.out.println("Banco atualizado. Dados existentes preservados; perfil de demonstração disponível.");
    }
}
