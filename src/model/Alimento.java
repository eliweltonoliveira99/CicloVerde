package model;

public class Alimento {
    private int id;
    private int comercioId;
    private String nomeAlimento;
    private double quantidadeKg;
    private String dataValidade;
    private String status;

    public Alimento(int comercioId, String nomeAlimento, double quantidadeKg, String dataValidade) {
        this.comercioId = comercioId;
        this.nomeAlimento = nomeAlimento;
        this.quantidadeKg = quantidadeKg;
        this.dataValidade = dataValidade;
        this.status = "Disponível";
    }

    // Getters
    public int getId() { return id; }
    public int getComercioId() { return comercioId; }
    public String getNomeAlimento() { return nomeAlimento; }
    public double getQuantidadeKg() { return quantidadeKg; }
    public String getDataValidade() { return dataValidade; }
    public String getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStatus(String status) { this.status = status; }
}

