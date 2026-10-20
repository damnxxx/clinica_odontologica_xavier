package br.com.clinica.odontologica.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "clientes")
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, length = 120)
    private String nome;
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;
    @Column(nullable = false, length = 160)
    private String email;
    @Column(nullable = false, length = 20)
    private String telefone;
    private LocalDate dataNascimento;
    @Column(length = 1000)
    private String observacoesClinicas;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    protected Cliente() { }
    public Cliente(String nome, String cpf, String email, String telefone, LocalDate dataNascimento, String observacoesClinicas) {
        this.nome = nome; this.cpf = cpf; this.email = email; this.telefone = telefone;
        this.dataNascimento = dataNascimento; this.observacoesClinicas = observacoesClinicas;
    }
    public void adicionarEndereco(Endereco endereco) { enderecos.add(endereco); endereco.setCliente(this); }
    public void removerEndereco(Endereco endereco) { enderecos.remove(endereco); endereco.setCliente(null); }
    public UUID getId() { return id; }
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
