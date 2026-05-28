package com.unoeste.chatredesbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ChatRedesBeApplication {

    private static final boolean ATIVAR_SOCKET = false;

    public static void main(String[] args) {

        // Sobe a aplicação Spring normalmente
        SpringApplication.run(ChatRedesBeApplication.class, args);
        //ConfigurableApplicationContext context = SpringApplication.run(ChatRedesBeApplication.class, args);

//        if (ATIVAR_SOCKET) {
//            ServidorSocket servidorSocket = context.getBean(ServidorSocket.class);
//            servidorSocket.iniciar();
//            System.out.println("Socket ativado manualmente.");
//        } else {
//            System.out.println("Socket desativado manualmente.");
//        }
    }
}