//package com.unoeste.chatredesbe.sockets;
//
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//@Component
//public class ServidorSocket {
//
//    // Responsável por controlar os usuários online no sistema.
//    // Esse objeto será compartilhado com os handlers de cliente.
//    private final GerenciadorSessoes gerenciadorSessoes;
//
//    // Responsável por interpretar os comandos que chegam dos clientes.
//    // Exemplo: login, logout, listausuarios, etc.
//    private final ProcessadorComandos processadorComandos;
//
//    // Construtor com injeção de dependências.
//    // O Spring injeta automaticamente o SessionManager e o CommandProcessor.
//    public ServidorSocket(GerenciadorSessoes gerenciadorSessoes, ProcessadorComandos processadorComandos) {
//        this.gerenciadorSessoes = gerenciadorSessoes;
//        this.processadorComandos = processadorComandos;
//    }
//
//    // Método responsável por iniciar o servidor socket.
//    public void iniciar() {
//
//        // Cria uma thread separada só para o servidor.
//        Thread threadServidor = new Thread(() -> {
//
//
//            try (ServerSocket serverSocket = new ServerSocket(6789)) {
//
//                // Mensagem exibida no console para indicar que o servidor subiu.
//                System.out.println("Servidor socket iniciado na porta 6789");
//
//                // Loop infinito para manter o servidor sempre pronto
//                // para aceitar novas conexões de clientes.
//                while (true) {
//
//                    // Fica aguardando até que algum cliente tente se conectar.
//                    // Esse metodo bloqueia a execução até chegar uma conexão
//                    Socket socket = serverSocket.accept();
//
//                    // Quando um cliente conecta, cria um tratador específico para cuidar daquela conexão.
//                    // Esse tratadorCliente vai ler comandos do cliente, processar e responder.
//                    TratadorCliente tratadorCliente =
//                            new TratadorCliente(socket, gerenciadorSessoes, processadorComandos);
//
//                    // Cria uma nova thread para atender esse cliente.
//                    // Isso permite que o servidor continue aceitando outros clientes
//                    // ao mesmo tempo, sem ficar preso a apenas um.
//                    new Thread(tratadorCliente).start();
//                }
//
//            } catch (IOException e) {
//                // Se ocorrer erro ao abrir a porta, aceitar conexões
//                // ou trabalhar com o socket, imprime o erro no console.
//                e.printStackTrace();
//            }
//        });
//
//        // Define um nome para a thread do servidor.
//        // Isso ajuda bastante em debug e logs.
//        threadServidor.setName("socket-server-thread");
//
//        // Inicia a thread do servidor.
//        // A partir daqui ele começa a escutar conexões na porta 6789.
//        threadServidor.start();
//    }
//}