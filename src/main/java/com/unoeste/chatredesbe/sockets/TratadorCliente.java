package com.unoeste.chatredesbe.sockets;

// Importações usadas para leitura e escrita no socket
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Essa classe representa o atendimento de UM cliente conectado ao servidor.
// Como implementa Runnable, ela pode ser executada dentro de uma Thread.
// Assim, cada cliente pode ser tratado em paralelo.
public class TratadorCliente implements Runnable {

    // Socket específico da conexão com este cliente.
    // É por ele que o servidor recebe e envia dados.
    private final Socket socket;

    // Classe responsável por controlar quais usuários estão online
    // e associar um apelido a um ClientHandler ativo.
    private final GerenciadorSessoes gerenciadorSessoes;

    // Classe responsável por interpretar os comandos digitados pelo cliente,
    // como login, logout, listausuarios, etc.
    private final ProcessadorComandos processadorComandos;

    // Stream de entrada: usada para ler o que o cliente envia.
    // BufferedReader + readLine() facilita leitura de texto linha a linha.
    private BufferedReader in;

    // Stream de saída: usada para enviar respostas para o cliente.
    // O parâmetro true ativa o autoFlush em println(),
    private PrintWriter out;

    // Guarda o apelido do usuário logado nesta conexão.
    private String apelidoUsuario;

    // Construtor da classe.
    // Recebe:
    // - o socket da conexão atual
    // - o gerenciador de sessões online
    // - o processador dos comandos recebidos
    public TratadorCliente(Socket socket,
                         GerenciadorSessoes gerenciadorSessoes,
                         ProcessadorComandos processadorComandos)
    {
        this.socket = socket;
        this.gerenciadorSessoes = gerenciadorSessoes;
        this.processadorComandos = processadorComandos;
    }

    // Método executado quando a thread desse cliente é iniciada.
    // Aqui fica o ciclo principal de comunicação com o cliente.
    @Override
    public void run() {
        try {
            // Cria o leitor de entrada a partir do socket.
            // socket.getInputStream() fornece bytes;
            // InputStreamReader converte bytes em caracteres;
            // BufferedReader permite ler linha por linha.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Cria o escritor de saída para responder ao cliente.
            // O true faz com que println() envie imediatamente os dados.
            out = new PrintWriter(socket.getOutputStream(), true);

            // Mensagem inicial enviada assim que o cliente conecta.
            out.println("Conectado ao servidor do chat.");

            // Instrução simples para orientar o primeiro comando.
            out.println("Use: login <apelido> <senha>");

            // Variável que vai armazenar cada linha recebida do cliente.
            String linha;

            // Loop principal:
            // enquanto o cliente continuar conectado e mandando linhas,
            // o servidor fica lendo e processando.
            // Se readLine() retornar null, a conexão foi encerrada.
            while ((linha = in.readLine()) != null) {

                // Envia o texto digitado para o CommandProcessor,

                // que vai decidir o que fazer com esse comando.
                // Ex.: login, logout, listausuarios...
                String resposta = processadorComandos.processar(linha, this);

                // Se o processamento gerar uma resposta válida,
                // essa resposta é devolvida ao cliente.
                if (resposta != null && !resposta.isBlank()) {
                    out.println(resposta);
                }
            }

        } catch (Exception e) {
            // Se der erro durante a comunicação, informa no console do servidor.
            // Isso geralmente acontece quando o cliente fecha a conexão
            // ou ocorre alguma falha de rede.
            System.out.println("Conexão encerrada: " + e.getMessage());
        } finally {
            // Sempre executa no final, com erro ou sem erro.
            // Serve para limpar a sessão e fechar o socket corretamente.
            encerrar();
        }
    }

    // Método usado para enviar uma mensagem para esse cliente a qualquer momento.
    // Exemplo:
    // - outro usuário mandou mensagem privada
    // - chegou convite de grupo
    // - aviso de sistema
    public void enviarMensagem(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    // Retorna o apelido do usuário associado a essa conexão.
    public String getApelidoUsuario() {
        return apelidoUsuario;
    }

    // Define o apelido do usuário logado nessa conexão.
    // Normalmente isso é chamado depois que o login é validado.
    public void setApelidoUsuario(String apelidoUsuario) {
        this.apelidoUsuario = apelidoUsuario;
    }

    // Método interno para encerrar corretamente a conexão.
    private void encerrar() {

        // Remove o usuário da lista de online no SessionManager.
        // Isso evita que o sistema continue achando que ele está conectado.
        gerenciadorSessoes.remover(apelidoUsuario);

        try {
            // Fecha o socket da conexão.
            // Isso libera os recursos usados pela conexão.
            socket.close();
        } catch (IOException e) {
            // Se houver erro ao fechar o socket, imprime no console.
            e.printStackTrace();
        }
    }
}