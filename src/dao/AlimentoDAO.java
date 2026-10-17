package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Alimento;

public class AlimentoDAO {
    
    // Método para inserir um alimento no banco de dados
    public boolean cadastrarDoacao(Alimento alimento) {
        String sql = "INSERT INTO alimento (comercio_id, nome_alimento, quantidade_kg, data_validade, status) VALUES (?, ?, ?, ?, ?)";
        
        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            
            stmt.setInt(1, alimento.getComercioId());
            stmt.setString(2, alimento.getNomeAlimento());
            stmt.setDouble(3, alimento.getQuantidadeKg());
            stmt.setString(4, alimento.getDataValidade());
            stmt.setString(5, alimento.getStatus());
            
            stmt.execute();
            stmt.close();
            conn.close();
            return true;
            
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar doação: " + e.getMessage());
            return false;
        }
    }

    // Método para listar alimentos disponíveis para retirada
    public List<Alimento> listarDisponiveis() {
        List<Alimento> lista = new ArrayList<>();
        String sql = "SELECT id, comercio_id, nome_alimento, quantidade_kg, data_validade, status FROM alimento WHERE status = 'Disponível' ORDER BY data_validade ASC";

        try {
            Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Alimento a = new Alimento(
                        rs.getInt("comercio_id"),
                        rs.getString("nome_alimento"),
                        rs.getDouble("quantidade_kg"),
                        rs.getString("data_validade")
                );
                a.setId(rs.getInt("id"));
                a.setStatus(rs.getString("status"));
                lista.add(a);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            System.out.println("Erro ao listar alimentos disponíveis: " + e.getMessage());
        }

        return lista;
    }
}
