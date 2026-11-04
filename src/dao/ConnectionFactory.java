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
        try (InputStream in = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
                loaded = true;
            }
        } catch (Exception ignored) {
        }
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
            Properties connectionProps = new Properties();
            connectionProps.setProperty("user", user);
            connectionProps.setProperty("password", pass);
            connectionProps.setProperty("connectTimeout", "5000");
            connectionProps.setProperty("socketTimeout", "10000");
            Connection connection = DriverManager.getConnection(url, connectionProps);
            String schemaDemonstracao = System.getProperty("cicloverde.demo.schema");
            if (schemaDemonstracao != null) {
                try {
                    if (!schemaDemonstracao.matches("cicloverde_demo_[a-f0-9]{32}")) {
                        throw new SQLException("Banco de demonstração inválido.");
                    }
                    connection.setCatalog(schemaDemonstracao);
                } catch (SQLException e) {
                    connection.close();
                    throw e;
                }
            }
            return connection;
        } catch (SQLException e) {
            throw new DadosException("Não foi possível conectar ao MySQL. Verifique o serviço e a configuração do banco.", e);
        }
    }
}
