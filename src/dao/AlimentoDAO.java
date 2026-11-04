package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.Alimento;
import model.Validacao;

public class AlimentoDAO {
    private final Conexoes conexoes;
    public AlimentoDAO() { this(ConnectionFactory::getConnection); }
    public AlimentoDAO(Conexoes conexoes) { this.conexoes = conexoes; }

    public boolean cadastrarDoacao(Alimento alimento) {
        Validacao.alimento(alimento);
        String sql = "INSERT INTO alimento (comercio_id,nome_alimento,quantidade_kg,data_validade,status) VALUES (?,?,?,?,?)";
        try (Connection conn = conexoes.abrir(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, alimento.getComercioId());
            stmt.setString(2, alimento.getNomeAlimento().trim());
            stmt.setDouble(3, alimento.getQuantidadeKg());
            stmt.setDate(4, Date.valueOf(alimento.getDataValidade()));
            stmt.setString(5, Alimento.DISPONIVEL);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DadosException("Não foi possível cadastrar o alimento. Verifique a conexão e a empresa selecionada.", e);
        }
    }
    public List<Alimento> listarDisponiveis() { return listar(null, true); }
    public List<Alimento> listarPorEmpresa(int empresaId) { return listar(empresaId, true); }
    public List<Alimento> listarTodos() { return listar(null, false); }
    public List<Alimento> listarHistoricoEmpresa(int empresaId) { return listar(empresaId, false); }

    private List<Alimento> listar(Integer empresaId, boolean disponiveis) {
        String sql = "SELECT a.*,c.nome AS empresa_nome FROM alimento a JOIN comercio c ON c.id=a.comercio_id WHERE 1=1"
                + (disponiveis ? " AND a.status=? AND a.data_validade>=?" : "")
                + (empresaId != null ? " AND a.comercio_id=?" : "") + " ORDER BY a.data_validade,a.id";
        try (Connection conn = conexoes.abrir(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            int param = 1;
            if (disponiveis) {
                stmt.setString(param++, Alimento.DISPONIVEL);
                stmt.setDate(param++, Date.valueOf(LocalDate.now()));
            }
            if (empresaId != null) stmt.setInt(param, empresaId);
            List<Alimento> alimentos = new ArrayList<>();
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Alimento a = new Alimento(rs.getInt("comercio_id"), rs.getString("nome_alimento"),
                            rs.getDouble("quantidade_kg"), rs.getDate("data_validade").toString());
                    a.setId(rs.getInt("id")); a.setStatus(rs.getString("status"));
                    a.setEmpresaNome(rs.getString("empresa_nome")); alimentos.add(a);
                }
            }
            return alimentos;
        } catch (SQLException e) {
            throw new DadosException("Não foi possível carregar os alimentos. Verifique a conexão com o MySQL.", e);
        }
    }
    public boolean reservar(int alimentoId, int empresaId) {
        if (alimentoId <= 0 || empresaId <= 0) throw new IllegalArgumentException("Lote ou empresa inválidos.");
        String sql = "UPDATE alimento SET status=? WHERE id=? AND comercio_id=? AND status=? AND data_validade>=?";
        try (Connection conn = conexoes.abrir(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Alimento.RESERVADO); stmt.setInt(2, alimentoId); stmt.setInt(3, empresaId);
            stmt.setString(4, Alimento.DISPONIVEL); stmt.setDate(5, Date.valueOf(LocalDate.now()));
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DadosException("Não foi possível reservar. Confira a conexão antes de tentar novamente.", e);
        }
    }
}
