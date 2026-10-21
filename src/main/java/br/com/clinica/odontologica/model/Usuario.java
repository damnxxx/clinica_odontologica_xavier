package br.com.clinica.odontologica.model;
import java.util.UUID;
public record Usuario(UUID id, String nome, String email, String senhaHash, boolean ativo) { }
