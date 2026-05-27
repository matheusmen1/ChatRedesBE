package com.unoeste.chatredesbe.entities;
import jakarta.persistence.*;

@Entity
@Table(name = "ConviteGrupo")
public class ConviteGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Usuario solicitante;

    @ManyToOne
    @JoinColumn(name = "convidado_id")
    private Usuario convidado;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "status")
    private String status;

    public ConviteGrupo(Long id, Usuario solicitante, Usuario convidado, Grupo grupo, String status)
    {
        this.id = id;
        this.solicitante = solicitante;
        this.convidado = convidado;
        this.grupo = grupo;
        this.status = status;
    }

    public ConviteGrupo()
    {
        this(0L, null, null, null, "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Usuario solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getConvidado() {
        return convidado;
    }

    public void setConvidado(Usuario convidado) {
        this.convidado = convidado;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
