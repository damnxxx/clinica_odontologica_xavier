package br.com.clinica.odontologica.exception;
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException() { super("E-mail ou senha inválidos."); }
}
