//package com.unoeste.chatredesbe.sockets;
//
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ExecutorSocket implements CommandLineRunner {
//
//    private final ServidorSocket servidorSocket;
//
//    public ExecutorSocket(ServidorSocket servidorSocket) {
//        this.servidorSocket = servidorSocket;
//    }
//
//    // Esse método é chamado automaticamente pelo Spring Boot
//    // depois que a aplicação termina de subir e o contexto é carregado.
//    // Como a classe implementa CommandLineRunner,
//    // o Spring executa o método run() uma vez na inicialização.
//    @Override
//    public void run(String... args) {
//
//        // Inicia o servidor socket automaticamente no startup da aplicação.
//        // Ou seja, assim que o backend subir, o socket já começa
//        // a escutar conexões na porta configurada.
//        servidorSocket.iniciar();
//    }
//}