package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Usuario")
public class Usuario
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "apelido")
    private String apelido;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status; // online, ocupado e offline

    @OneToMany(mappedBy = "usuario")
    private List<UsuarioGrupo> gruposUsuario;

    public Usuario(Long id, String nome, String apelido, String email, String status) {
        this.id = id;
        this.nome = nome;
        this.apelido = apelido;
        this.email = email;
        this.status = status;
    }

    public Usuario()
    {
        this(0L, "", "", "", "");
    }

    public List<UsuarioGrupo> getGruposUsuario() {
        return gruposUsuario;
    }

    public void setGruposUsuario(List<UsuarioGrupo> gruposUsuario) {
        this.gruposUsuario = gruposUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
