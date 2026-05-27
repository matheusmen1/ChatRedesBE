package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "SolicitacaoMensagem")
public class SolicitacaoMensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario1_id")
    private Usuario usuario1;

    @ManyToOne
    @JoinColumn(name = "usuario2_id")
    private Usuario usuario2;

    @Column(name = "status")
    private String status;

    //construtor

    public SolicitacaoMensagem(Long id, Usuario usuario1, Usuario usuario2, String status) {
        this.id = id;
        this.usuario1 = usuario1;
        this.usuario2 = usuario2;
        this.status = status;
    }

    public SolicitacaoMensagem()
    {
        this(0L, null, null, "");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario1() {
        return usuario1;
    }

    public void setUsuario1(Usuario usuario1) {
        this.usuario1 = usuario1;
    }

    public Usuario getUsuario2() {
        return usuario2;
    }

    public void setUsuario2(Usuario usuario2) {
        this.usuario2 = usuario2;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //gets e sets
}
