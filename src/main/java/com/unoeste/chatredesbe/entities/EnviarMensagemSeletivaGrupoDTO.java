package com.unoeste.chatredesbe.entities;

import java.util.List;

public class EnviarMensagemSeletivaGrupoDTO
{
    private String conteudo;
    private Long remetenteId;
    private List<Long> usuariosIds;

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Long getRemetenteId() {
        return remetenteId;
    }

    public void setRemetenteId(Long remetenteId) {
        this.remetenteId = remetenteId;
    }

    public List<Long> getUsuariosIds() {
        return usuariosIds;
    }

    public void setUsuariosIds(List<Long> usuariosIds) {
        this.usuariosIds = usuariosIds;
    }
}
