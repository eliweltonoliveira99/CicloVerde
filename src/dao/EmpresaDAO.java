package dao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Empresa;
import model.Validacao;
public class EmpresaDAO {
    private final Conexoes conexoes;
    public EmpresaDAO() { this(ConnectionFactory::getConnection); }
    public EmpresaDAO(Conexoes conexoes) { this.conexoes = conexoes; }
    public List<Empresa> listar() {
        try (Connection conn = conexoes.abrir(); PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM comercio ORDER BY demonstracao DESC,nome,id"); ResultSet rs = stmt.executeQuery()) {
            List<Empresa> empresas = new ArrayList<>();
            while (rs.next()) empresas.add(new Empresa(rs.getInt("id"), rs.getString("nome"), rs.getString("cnpj"),
                    rs.getString("cidade"), rs.getString("responsavel"), rs.getString("endereco"),
                    rs.getString("descricao"), rs.getBoolean("demonstracao")));
            return empresas;
        } catch (SQLException e) { throw new DadosException("Não foi possível carregar as empresas. Verifique o MySQL e a atualização do banco.", e); }
    }
    public int cadastrar(Empresa empresa) {
        String nome = Validacao.texto(empresa.getNome(), "o nome da empresa", 100, true);
        String cnpj = Validacao.texto(empresa.getCnpj(), "CNPJ", 20, false);
        String cidade = Validacao.texto(empresa.getCidade(), "a cidade/UF", 100, true);
        String responsavel = Validacao.texto(empresa.getResponsavel(), "o responsável", 100, false);
        String endereco = Validacao.texto(empresa.getEndereco(), "o local de retirada", 255, false);
        String descricao = Validacao.texto(empresa.getDescricao(), "a descrição", 500, false);
        String sql = "INSERT INTO comercio(nome,cnpj,cidade,responsavel,endereco,descricao,demonstracao) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = conexoes.abrir(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            if (cnpj.isEmpty()) stmt.setNull(2, Types.VARCHAR); else stmt.setString(2, cnpj);
            stmt.setString(3, cidade); stmt.setString(4, responsavel); stmt.setString(5, endereco);
            stmt.setString(6, descricao); stmt.setBoolean(7, empresa.isDemonstracao()); stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
                throw new SQLException("Identificador não retornado pelo banco.");
            }
        } catch (SQLException e) { throw new DadosException("Não foi possível cadastrar a empresa. Confira se o CNPJ já está cadastrado e se o banco está conectado.", e); }
    }
}
