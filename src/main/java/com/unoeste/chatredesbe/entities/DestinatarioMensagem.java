package com.unoeste.chatredesbe.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DestinatarioMensagem")
public class DestinatarioMensagem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "destinatario_id")
    private Usuario destinatario;

    @ManyToOne
    @JoinColumn(name = "mensagem_id")
    private Mensagem mensagem;

    @Column(name = "status")
    private String status;

    @Column(name = "dataHoraEntrega")
    private LocalDateTime dataHoraEntrega;

    public DestinatarioMensagem(Long id, Usuario destinatario, Mensagem mensagem, String status, LocalDateTime dataHoraEntrega) {
        this.id = id;
        this.destinatario = destinatario;
        this.mensagem = mensagem;
        this.status = status;
        this.dataHoraEntrega = dataHoraEntrega;
    }

    public DestinatarioMensagem()
    {
        this(0L, null, null, "",null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(Usuario destinatario) {
        this.destinatario = destinatario;
    }

    public Mensagem getMensagem() {
        return mensagem;
    }

    public void setMensagem(Mensagem mensagem) {
        this.mensagem = mensagem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataHoraEntrega() {
        return dataHoraEntrega;
    }

    public void setDataHoraEntrega(LocalDateTime dataHoraEntrega) {
        this.dataHoraEntrega = dataHoraEntrega;
    }
}
