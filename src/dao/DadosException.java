package dao;
public class DadosException extends RuntimeException {
    public DadosException(String mensagem, Throwable causa) { super(mensagem, causa); }
}
