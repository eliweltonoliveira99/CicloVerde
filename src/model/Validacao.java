package model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
public final class Validacao {
    public static final DateTimeFormatter DATA_BR = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private Validacao() {}
    public static String texto(String valor, String campo, int limite, boolean obrigatorio) {
        String texto = valor == null ? "" : valor.trim();
        if (obrigatorio && texto.isEmpty()) throw new IllegalArgumentException("Informe " + campo + ".");
        if (texto.length() > limite) throw new IllegalArgumentException(campo + ": use até " + limite + " caracteres.");
        return texto;
    }
    public static double quantidade(String valor) {
        try {
            double kg = Double.parseDouble(valor.trim().replace(',', '.'));
            if (!Double.isFinite(kg) || kg <= 0) throw new NumberFormatException();
            return kg;
        } catch (NullPointerException | NumberFormatException e) {
            throw new IllegalArgumentException("Informe uma quantidade positiva em kg (ex.: 2,5).");
        }
    }
    public static LocalDate data(String texto) {
        if (texto == null || !texto.trim().matches("\\d{2}/\\d{2}/\\d{4}"))
            throw new IllegalArgumentException("Informe a validade no formato dd/MM/aaaa.");
        try {
            LocalDate data = LocalDate.parse(texto.trim(), DATA_BR);
            validarValidade(data);
            return data;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inexistente no calendário. Confira dia, mês e ano.");
        }
    }
    public static void validarValidade(LocalDate data) {
        if (data.isBefore(LocalDate.now())) throw new IllegalArgumentException("A validade não pode estar no passado.");
        if (data.getYear() > 9999) throw new IllegalArgumentException("Informe um ano com quatro dígitos.");
    }
    public static void alimento(Alimento alimento) {
        if (alimento == null || alimento.getComercioId() <= 0) throw new IllegalArgumentException("Selecione uma empresa cadastrada.");
        texto(alimento.getNomeAlimento(), "o nome do alimento", 100, true);
        quantidade(Double.toString(alimento.getQuantidadeKg()));
        try { validarValidade(LocalDate.parse(alimento.getDataValidade())); }
        catch (DateTimeParseException | NullPointerException e) { throw new IllegalArgumentException("Validade inválida."); }
    }
}
