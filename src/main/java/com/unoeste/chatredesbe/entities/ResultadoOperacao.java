package com.unoeste.chatredesbe.entities;

public class ResultadoOperacao<T> {
    private boolean sucesso;
    private String mensagem;
    private T dados;

    public ResultadoOperacao(boolean sucesso, String mensagem, T dados) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.dados = dados;
    }

    public static <T> ResultadoOperacao<T> sucesso(String mensagem, T dados)
    {
        return new ResultadoOperacao<>(true, mensagem, dados);
    }

    public static <T> ResultadoOperacao<T> erro(String mensagem)
    {
        return new ResultadoOperacao<>(false, mensagem, null);
    }

    public boolean isSucesso()
    {
        return sucesso;
    }

    public String getMensagem()
    {
        return mensagem;
    }

    public T getDados()
    {
        return dados;
    }
}
