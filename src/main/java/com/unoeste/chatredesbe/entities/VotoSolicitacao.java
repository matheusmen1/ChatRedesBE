package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "VotoSolicitacao")
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

    //construtores


    //gets e sets


}
