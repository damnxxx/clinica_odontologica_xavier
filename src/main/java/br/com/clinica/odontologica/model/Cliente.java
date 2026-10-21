package br.com.clinica.odontologica.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Modelo de domínio simples, sem dependência de ORM.
 */
public class Cliente {
    private UUID id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private LocalDate dataNascimento;
    private String observacoesClinicas;
    private final List<Endereco> enderecos = new ArrayList<>();

    public Cliente() { }

    public Cliente(String nome, String cpf, String email, String telefone, LocalDate dataNascimento, String observacoesClinicas) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.observacoesClinicas = observacoesClinicas;
    }

    public void adicionarEndereco(Endereco endereco) {
        enderecos.add(endereco);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getObservacoesClinicas() { return observacoesClinicas; }
    public void setObservacoesClinicas(String observacoesClinicas) { this.observacoesClinicas = observacoesClinicas; }
    public List<Endereco> getEnderecos() { return enderecos; }
}
