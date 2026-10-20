package br.com.clinica.odontologica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enderecos")
public class Endereco {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120) private String logradouro;
    @Column(nullable = false, length = 20) private String numero;
    @Column(length = 80) private String complemento;
    @Column(nullable = false, length = 80) private String bairro;
    @Column(nullable = false, length = 80) private String cidade;
    @Column(nullable = false, length = 2) private String estado;
    @Column(nullable = false, length = 9) private String cep;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    protected Endereco() { }
    public Endereco(String logradouro, String numero, String complemento, String bairro, String cidade, String estado, String cep) {
        this.logradouro = logradouro; this.numero = numero; this.complemento = complemento;
        this.bairro = bairro; this.cidade = cidade; this.estado = estado; this.cep = cep;
    }
    public Long getId() { return id; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public Cliente getCliente() { return cliente; }
    void setCliente(Cliente cliente) { this.cliente = cliente; }
}
