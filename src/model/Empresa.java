package model;
public class Empresa {
    private final int id;
    private final String nome, cnpj, cidade, responsavel, endereco, descricao;
    private final boolean demonstracao;
    public Empresa(int id, String nome, String cnpj, String cidade, String responsavel,
                   String endereco, String descricao, boolean demonstracao) {
        this.id = id; this.nome = nome; this.cnpj = cnpj; this.cidade = cidade;
        this.responsavel = responsavel; this.endereco = endereco; this.descricao = descricao;
        this.demonstracao = demonstracao;
    }
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCnpj() { return cnpj; }
    public String getCidade() { return cidade; }
    public String getResponsavel() { return responsavel; }
    public String getEndereco() { return endereco; }
    public String getDescricao() { return descricao; }
    public boolean isDemonstracao() { return demonstracao; }
    @Override public String toString() { return nome + (demonstracao ? " (demonstração)" : ""); }
}
