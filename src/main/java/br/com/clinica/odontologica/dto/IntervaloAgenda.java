package br.com.clinica.odontologica.dto;
import java.time.LocalDateTime;
public record IntervaloAgenda(LocalDateTime inicio, LocalDateTime fim) {}
