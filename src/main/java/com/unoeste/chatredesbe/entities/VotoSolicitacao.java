package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "voto_solicitacao")
public class VotoSolicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "votante_id")
    private Usuario votante;

    @ManyToOne
    @JoinColumn(name = "solicitacao_id")
    private SolicitacaoEntradaGrupo solicitacao;

    @Column(name = "status")
    private String status;

    public VotoSolicitacao(Long id, Usuario votante, SolicitacaoEntradaGrupo solicitacao, String status) {
        this.id = id;
        this.votante = votante;
        this.solicitacao = solicitacao;
        this.status = status;
    }

    public VotoSolicitacao( Usuario votante, SolicitacaoEntradaGrupo solicitacao, String status) {
        this.votante = votante;
        this.solicitacao = solicitacao;
        this.status = status;
    }

    public VotoSolicitacao()
    {
        this(0L, null, null, "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getVotante() {
        return votante;
    }

    public void setVotante(Usuario votante) {
        this.votante = votante;
    }

    public SolicitacaoEntradaGrupo getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoEntradaGrupo solicitacao) {
        this.solicitacao = solicitacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
