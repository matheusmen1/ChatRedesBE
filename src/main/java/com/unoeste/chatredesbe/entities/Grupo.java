package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "grupo")
public class Grupo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome")
    private String nome;

    @ManyToOne
    @JoinColumn(name = "criador_id", nullable = false)
    private Usuario criador;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @OneToMany(mappedBy = "grupo")
    private List<UsuarioGrupo> usuariosGrupo;

    public Grupo(Long id, String nome, Usuario usuario, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.criador = usuario;
        this.dataCriacao = dataCriacao;
    }

    public Grupo()
    {
        this(0L, "", null, null);
    }

    public List<UsuarioGrupo> getUsuariosGrupo() {
        return usuariosGrupo;
    }

    public void setUsuariosGrupo(List<UsuarioGrupo> usuariosGrupo) {
        this.usuariosGrupo = usuariosGrupo;
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

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario usuario) {
        this.criador = usuario;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
