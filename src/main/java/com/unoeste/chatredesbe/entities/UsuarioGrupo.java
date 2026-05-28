package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_grupo")
public class UsuarioGrupo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "data_entrada")
    private LocalDateTime dataEntrada;

    public UsuarioGrupo(Long id, Usuario usuario, Grupo grupo, LocalDateTime dataEntrada) {
        this.id = id;
        this.usuario = usuario;
        this.grupo = grupo;
        this.dataEntrada = dataEntrada;
    }

    public UsuarioGrupo(Usuario usuario, Grupo grupo, LocalDateTime dataEntrada) {
        this.usuario = usuario;
        this.grupo = grupo;
        this.dataEntrada = dataEntrada;
    }

    public UsuarioGrupo()
    {
        this(0L, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public LocalDateTime getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDateTime dataEntrada) {
        this.dataEntrada = dataEntrada;
    }
}
