package dao;
import java.sql.Connection;
import java.sql.SQLException;
@FunctionalInterface
public interface Conexoes { Connection abrir() throws SQLException; }
