//package com.unoeste.chatredesbe.sockets;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.stereotype.Component;
//
//@Component
//public class InicializadorSocket {
//
//    private final ServidorSocket servidorSocket;
//
//    public InicializadorSocket(ServidorSocket servidorSocket) {
//        this.servidorSocket = servidorSocket;
//    }
//
//    // Esse método é executado automaticamente pelo Spring
//    // logo após o bean ser criado e depois que todas as dependências
//    @PostConstruct
//    public void init() {
//
//        // Inicia o servidor socket assim que a aplicação Spring termina
//        // de montar esse componente.
//        servidorSocket.iniciar();
//    }
//}