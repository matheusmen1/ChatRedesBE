//package com.unoeste.chatredesbe.sockets;
//
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//public class GerenciadorSessoes {
//
//    // Mapa que armazena os usuários atualmente online.
//    // A chave é o apelido do usuário.
//    // O valor é o ClientHandler associado à conexão daquele usuário.
//    //
//    // Exemplo:
//    // "matheus" -> handler da conexão do Matheus
//    // "joao" -> handler da conexão do João
//    //
//    // Foi usado ConcurrentHashMap porque vários clientes podem acessar
//    // essa estrutura ao mesmo tempo, em threads diferentes.
//    // Ele permite operações thread-safe como put, get, remove e containsKey. [web:117][web:120]
//    private final Map<String, TratadorCliente> usuariosOnline = new ConcurrentHashMap<>();
//
//    // Adiciona um usuário na lista de online.
//    // Isso normalmente acontece após login bem-sucedido.
//    //
//    // apelido = nome usado como identificação no chat
//    // clientHandler = objeto que representa a conexão ativa desse usuário
//    public void adicionar(String apelido, TratadorCliente clientHandler) {
//        usuariosOnline.put(apelido, clientHandler);
//    }
//
//    // Remove um usuário da lista de online.
//    // Isso normalmente acontece no logout ou quando a conexão é encerrada.
//    //
//    // O if evita tentar remover um apelido nulo.
//    public void remover(String apelido) {
//        if (apelido != null) {
//            usuariosOnline.remove(apelido);
//        }
//    }
//
//    // Retorna o TratadorCliente de um usuário específico.
//    // Isso é útil quando você quiser enviar uma mensagem direta
//    // para um usuário que está online.
//    //
//    // Exemplo:
//    // TratadorCliente handler = sessionManager.get("joao");
//    // if (handler != null) {
//    //     handler.enviarMensagem("Olá!");
//    // }
//    public TratadorCliente get(String apelido) {
//        return usuariosOnline.get(apelido);
//    }
//
//    // Verifica se determinado usuário está online no momento.
//    // Retorna true se o apelido existir no mapa, false caso contrário. [web:112][web:115]
//    public boolean estaOnline(String apelido) {
//        return usuariosOnline.containsKey(apelido);
//    }
//
//    // Retorna um conjunto com todos os apelidos atualmente online.
//    // Isso é útil para o comando "listausuarios".
//    //
//    // Como vem de um ConcurrentHashMap, essa visão é segura para acesso concorrente,
//    // com iteração fracamente consistente, ou seja, ela não lança
//    // ConcurrentModificationException durante modificações concorrentes. [web:111][web:114]
//    public Set<String> listarOnline() {
//        return usuariosOnline.keySet();
//    }
//}