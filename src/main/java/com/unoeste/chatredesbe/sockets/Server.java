package com.unoeste.chatredesbe.sockets;

import com.unoeste.chatredesbe.Padrao.MensageiroFacade;
import com.unoeste.chatredesbe.services.GrupoService;
import com.unoeste.chatredesbe.services.SolicitacaoEntradaGrupoService;
import com.unoeste.chatredesbe.services.UsuarioGrupoService;
import com.unoeste.chatredesbe.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@Component
public class Server implements CommandLineRunner
{
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private MensageiroFacade mensageiroFacade;

    @Autowired
    private UsuarioGrupoService usuarioGrupoService;

    @Autowired
    private SolicitacaoEntradaGrupoService solicitaccaoEntradaGrupoService;

    @Override
    public void run(String... args) throws Exception {
        int porta = 5000;
        InetAddress enderecoRadmin = InetAddress.getByName("25.32.77.213");
        try
        {
            ServerSocket serverSocket = new ServerSocket(porta, 50, enderecoRadmin);
            System.out.println("=================================================");
            System.out.println("SERVIDOR DE CHAT (SOCKETS) INICIADO NA PORTA " + porta);
            System.out.println("Aguardando Conexões de Clientes...");
            System.out.println("=================================================");
            while (true)
            {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Novo Cliente Conectado IP: "+ clienteSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clienteSocket, usuarioService, grupoService, mensageiroFacade, usuarioGrupoService, solicitaccaoEntradaGrupoService);
                new Thread(clientHandler).start();
            }
        }
        catch (Exception e)
        {
            System.out.println("Erro Ao Iniciar o Servidor Socket: "+ e.getMessage());
        }

    }
}

