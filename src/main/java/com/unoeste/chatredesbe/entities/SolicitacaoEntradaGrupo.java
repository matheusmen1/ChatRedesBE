package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitacao_entrada_grupo")
public class SolicitacaoEntradaGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @Column(name = "status")
    private String status;

    public SolicitacaoEntradaGrupo(long id, Grupo grupo, Usuario solicitante, String status) {
        this.id = id;
        this.grupo = grupo;
        this.solicitante = solicitante;
        this.status = status;
    }

    public SolicitacaoEntradaGrupo()
    {
        this(0L, null, null, "");
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }





}
