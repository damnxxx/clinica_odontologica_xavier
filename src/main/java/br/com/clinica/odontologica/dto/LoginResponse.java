package br.com.clinica.odontologica.dto;
public record LoginResponse(String accessToken, String tokenType, long expiresIn, String email) { }
