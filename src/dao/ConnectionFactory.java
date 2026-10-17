package dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static String url = "jdbc:mysql://localhost:3306/cicloverde";
    private static String user = "root";
    private static String pass = "";

    static {
        Properties props = new Properties();
        boolean loaded = false;

        // 1. Tenta carregar do classpath (ex: src/ ou bin/)
        try (InputStream in = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                loaded = true;
            }
        } catch (Exception ignored) {
        }

        // 2. Se não estiver no classpath, tenta carregar direto do sistema de arquivos
        if (!loaded) {
            File[] possibleFiles = new File[] {
                new File("db.properties"),
                new File("CicloVerde/db.properties"),
                new File("CicloVerde/src/db.properties"),
                new File("src/db.properties")
            };

            for (File file : possibleFiles) {
                if (file.exists() && file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        props.load(fis);
                        loaded = true;
                        break;
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        // Aplica os valores do arquivo db.properties se encontrado
        if (loaded) {
            if (props.getProperty("db.url") != null && !props.getProperty("db.url").trim().isEmpty()) {
                url = props.getProperty("db.url").trim();
            }
            if (props.getProperty("db.user") != null && !props.getProperty("db.user").trim().isEmpty()) {
                user = props.getProperty("db.user").trim();
            }
            if (props.getProperty("db.password") != null) {
                pass = props.getProperty("db.password").trim();
            }
        } else {
            // Fallback para variáveis de ambiente, se existirem
            if (System.getenv("DB_URL") != null) {
                url = System.getenv("DB_URL");
            }
            if (System.getenv("DB_USER") != null) {
                user = System.getenv("DB_USER");
            }
            if (System.getenv("DB_PASS") != null) {
                pass = System.getenv("DB_PASS");
            }
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados. Verifique se o MySQL está rodando: " + e);
        }
    }
}
