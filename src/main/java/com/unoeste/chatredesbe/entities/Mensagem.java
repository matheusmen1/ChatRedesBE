package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensagem")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "remetente_id")
    private Usuario remetente;

    @Column(name = "conteudo")
    private String conteudo;

    @ManyToOne
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "data_hora_envio")
    private LocalDateTime dataHoraEnvio;

    public Mensagem(Long id, Usuario remetente, String conteudo, Grupo grupo, LocalDateTime dataHoraEnvio) {
        this.id = id;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.grupo = grupo;
        this.dataHoraEnvio = dataHoraEnvio;
    }

    public Mensagem()
    {
        this(0L, null, null, null, null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getRemetente() {
        return remetente;
    }

    public void setRemetente(Usuario remetente) {
        this.remetente = remetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public LocalDateTime getDataHoraEnvio() {
        return dataHoraEnvio;
    }

    public void setDataHoraEnvio(LocalDateTime dataHoraEnvio) {
        this.dataHoraEnvio = dataHoraEnvio;
    }
}
