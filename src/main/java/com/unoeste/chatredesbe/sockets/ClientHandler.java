package com.unoeste.chatredesbe.sockets;

import com.unoeste.chatredesbe.Padrao.MensageiroFacade;
import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.services.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";
    private static final String BOLD = "\u001B[1m";


    private final MensageiroFacade mensageiroFacade;
    private final UsuarioService usuarioService;
    private final GrupoService grupoService;

    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private Usuario usuarioLogado = null;
    private Usuario solicitacaoMensagem = null;

    public static List<ClientHandler> clientesConectados = new ArrayList<>();


    public ClientHandler(Socket clienteSocket, UsuarioService usuarioService, GrupoService grupoService, MensageiroFacade mensageiroFacade) {
        this.clienteSocket = clienteSocket;
        this.usuarioService = usuarioService;
        this.grupoService = grupoService;
        this.mensageiroFacade = mensageiroFacade;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            saida = new PrintWriter(clienteSocket.getOutputStream(), true);

            String linha;
            String comando;
            saida.println(CYAN + "===================================" + RESET);
            saida.println(BOLD + BLUE + "   Bem Vindo(a) Ao CHAT de REDES" + RESET);
            saida.println(CYAN + "===================================" + RESET);
            saida.println(YELLOW + "Digite 'ajuda' Para Ver os Comandos" + RESET);
            do {
                linha = entrada.readLine();
                String[] partes = linha.split(" ");
                comando = partes[0].toLowerCase();
                switch (comando) {
                    case "ajuda": {
                        printInfo("======== COMANDOS ======== ");
                        printInfo("cadastrar");
                        printInfo("login");
                        printInfo("logout");
                        printInfo("recuperarSenha");
                        printInfo("status");
                        printInfo("definirStatus");

                        printInfo("listaUsuarios");
                        printInfo("listaGrupos");
                        printInfo("listaSolicitacoes");
                        printInfo("listaConversas");
                        printInfo("listaConvites");
                        printInfo("enviarMensagem");
                        //printInfo("responderprivado");

                        printInfo("aceitar");
                        printInfo("responderconvite");
                        printInfo("recusar");
                        printInfo("novogrupo");
                        printInfo("inserir");

                        printInfo("entrargrupo");

                        // entrar &<nomegrupo>: Solicita a entrada em um grupo do qual o usuário ainda não faz parte.
                        // votar &<nomegrupo> @<usuario_solicitante> <sim|nao>: Resposta dos membros atuais do grupo aprovando ou negando a entrada do solicitante.
                        printInfo("sairgrupo");
                        printInfo("mensagemgrupo");
                        printInfo("mensagemgruposeletiva");
                        printInfo("=========================== ");
                        break;
                    }
                    //->  perfil e autenticação
                    case "cadastrar": {
                        saida.println("<nome_completo>;<login>;<email>;<senha>");
                        linha = entrada.readLine();
                        if (linha != null) {
                            partes = linha.split(";");
                            if (partes.length == 4) {
                                cadastrarUsuario(partes);
                            } else {
                                printAviso("Campo(s) Nao Informado(s)");
                            }
                        }
                        break;
                    }
                    case "login": {//login <login> <senha>
                        saida.println("<login> <senha>");
                        linha = entrada.readLine();
                        if (linha != null) {
                            partes = linha.split(" ");
                            if (partes.length == 2) {
                                logar(partes);
                            } else {
                                printAviso("Campo(s) Nao Informado(s)");
                            }
                        }
                        break;
                    }
                    case "logout": {
                        if (usuarioLogado != null) {
                            saida.println("Encerrando Conexao...");
                            desconectarUsuario();
                            clientesConectados.remove(this);
                            clienteSocket.close();
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "recuperarsenha": {//recuperarSenha <email>: Exibe a senha do usuário vinculado ao e-mail.
                        if (usuarioLogado != null) {
                            saida.println("<email>");
                            linha = entrada.readLine();
                            if (linha != null) {
                                recuperarSenha(linha);
                            }
                            break;
                        } else
                            printAviso("Usuario Nao Logado");
                    }
                    case "status": {//status <online|offline|ocupado>: Altera o status atual do usuário.
                        if (usuarioLogado != null)
                            status();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "definirstatus": {//status <online|offline|ocupado>: Altera o status atual do usuário.
                        if (usuarioLogado != null) {
                            saida.println("Informe: online, offline ou ocupado");
                            saida.println("<status>");
                            linha = entrada.readLine();
                            if (linha != null)
                                definirStatus(linha);
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    // -> visualização e listagens
                    case "listausuarios": {//Lista os usuários cadastrados e online.
                        if (usuarioLogado != null)
                            listaUsuarios();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "listagrupos": {//Lista os grupos existentes no servidor.
                        if (usuarioLogado != null)
                            listaGrupos();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "listasolicitacoes": {
                        if (usuarioLogado != null)
                            listaSolicitacoes();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "listaconversas": {
                        if (usuarioLogado != null)
                            listaConversasParticulares();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "listaconvites": {
                        if (usuarioLogado != null)
                            listConvites();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    case "enviarmensagem": {
                        if (usuarioLogado != null) {
                            if (usuarioLogado.getStatus().equals("online")) {
                                printInfo("<@destinatario:> <mensagem>");
                                linha = entrada.readLine();
                                if (linha != null) {
                                    partes = linha.split(":");
                                    if (partes.length == 2) {
                                        partes[0] = partes[0] + ":";
                                        enviarMensagem(partes);
                                    } else {
                                        printAviso("Campo(s) Nao Informado");
                                    }
                                }
                            } else {
                                printAviso("Usuario Nao Online");
                            }
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    case "responderprivado":// responderprivado <usuario>
                    {

                    }

                    case "aceitar": {
                        printAviso("<usuario>");
                        linha = entrada.readLine();
                        if (linha != null) {
                            partes = linha.split(" ");
                            if (partes.length == 1) {
                                // verificar se o usuário existe
                                Usuario solicitante = usuarioService.getByApelido(partes[0]);
                                if (solicitante != null) {
                                    ResultadoOperacao<SolicitacaoMensagem> resultadoSoliticacao = mensageiroFacade.confirmarSolicitacao(
                                            solicitante.getId(), usuarioLogado.getId(), "Confirmada");
                                    if (resultadoSoliticacao.isSucesso()) {
                                        printSucesso(resultadoSoliticacao.getMensagem());
                                        for (int i = 0; i < clientesConectados.size(); i++) {
                                            ClientHandler clientHandler = clientesConectados.get(i);
                                            if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitante.getId())) {
                                                if (clientHandler.usuarioLogado.getStatus().equals("online")) {
                                                    clientHandler.printSucesso("@" + usuarioLogado.getApelido() + "Aceitou sua Solicitacao");
                                                }
                                            }
                                        }
                                    } else
                                        printErro(resultadoSoliticacao.getMensagem());
                                } else {
                                    printErro("Usuario digitado nao existe!!");
                                }
                            } else {
                                printAviso("Campo(s) Nao Informado");
                            }
                        }
                        break;
                    }
                    case "responderconvite": {
                        if (usuarioLogado != null) {
                            printAviso("<nomegrupo> <sim|nao>");
                            linha = entrada.readLine();
                            if (linha != null) {
                                partes = linha.split(" ");
                                if (partes.length == 2) {
                                    responderConvite(partes[0], partes[1]);
                                } else {
                                    printAviso("Campo(s) Nao Informado");
                                }
                            }
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "recusar": {
                        printAviso("<usuario>");
                        linha = entrada.readLine();
                        if (linha != null) {
                            partes = linha.split(" ");
                            if (partes.length == 1) {
                                // verificar se o usuário existe
                                Usuario solicitante = usuarioService.getByApelido(partes[0]);
                                if (solicitante != null) {
                                    ResultadoOperacao<SolicitacaoMensagem> resultadoSoliticacao = mensageiroFacade.confirmarSolicitacao(
                                            solicitante.getId(), usuarioLogado.getId(), "Recusada");
                                    if (resultadoSoliticacao.isSucesso()) {
                                        printSucesso(resultadoSoliticacao.getMensagem());
                                        for (int i = 0; i < clientesConectados.size(); i++) {
                                            ClientHandler clientHandler = clientesConectados.get(i);
                                            if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitante.getId())) {
                                                if (clientHandler.usuarioLogado.getStatus().equals("online")) {
                                                    clientHandler.printSucesso("@" + usuarioLogado.getApelido() + "Recusou sua Solicitacao");
                                                }
                                            }
                                        }
                                    } else
                                        printErro(resultadoSoliticacao.getMensagem());
                                } else {
                                    printErro("Usuario digitado nao existe!!");
                                }
                            } else {
                                printAviso("Campo(s) Nao Informado");
                            }
                        }
                        break;
                    }
                    // -> GERENCIAMENTO DE GRUPOS
                    case "novogrupo":// novogrupo <nomegrupo>: Cria um novo grupo e adiciona o criador automaticamente nele.
                    {
                        if (usuarioLogado != null) {
                            if (usuarioLogado.getStatus().equals("online")) {
                                printAviso("<nomegrupo>");
                                linha = entrada.readLine();
                                if (linha != null) {
                                    partes = linha.split(" ");
                                    if (partes.length == 1) {
                                        criarGrupo(partes[0]);
                                    } else {
                                        printAviso("Campo(s) Nao Informado");
                                    }
                                }
                            } else {
                                printAviso("Usuario Nao Online");
                            }
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "inserir": {// inserir &<nomegrupo>@<usuario1>,<usuario2>: Adiciona (convida) um ou mais usuários já cadastrados a um grupo.
                        if (usuarioLogado != null) {
                            if (usuarioLogado.getStatus().equals("online")) {
                                printAviso("<nomegrupo> <usuario1> (,<usuario2>)*");
                                linha = entrada.readLine();
                                if (linha != null) {
                                    partes = linha.split(" ");
                                    if (partes.length >= 2) {
                                        inserirNoGrupo(partes);
                                    } else {
                                        printAviso("Campo(s) Nao Informado");
                                    }
                                }
                            } else {
                                printAviso("Usuario Nao Online");
                            }
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    // entrargrupo &<nomegrupo>: Solicita a entrada em um grupo do qual o usuário ainda não faz parte.
                    case "entrargrupo": {
                        if (usuarioLogado != null) {
                            if (usuarioLogado.getStatus().equals("online"))
                            {
                                printInfo("&<nomegrupo>");
                                linha = entrada.readLine();
                                if(linha != null)
                                {
                                    linha = linha.trim();
                                    if(!linha.isEmpty() && linha.startsWith("&"))
                                        entrarGrupo(linha);
                                    else
                                        printAviso("Formato Invalido.");
                                }
                                else
                                    printAviso("Campo nao Informado");
                            }
                            else
                                printAviso("Usuario Nao Online");
                        }
                        else
                            printAviso("Usuario Nao Logado");
                    }

                    // votar &<nomegrupo> @<usuario_solicitante> <sim|nao>: Resposta dos membros atuais do grupo aprovando ou negando a entrada do solicitante.


                    case "sairgrupo":// sair &<nomegrupo>: O usuário logado sai do grupo especificado (avisando os demais).
                    {
                        if (usuarioLogado != null) {
                            printInfo("&<nomegrupo>");
                            linha = entrada.readLine().trim();
                            if (linha != null && linha.charAt(0) == '&') {
                                sairGrupo(linha);
                            }
                        } else
                            printAviso("Usuario Nao esta Logado");
                        break;
                    }
                    //-> mensagens em Grupo

                    // &<nomegrupo>: <mensagem>: Envia uma mensagem para todos os participantes do grupo.
                    case "mensagemgrupo": {
                        if (usuarioLogado != null && usuarioLogado.getStatus().equals("online")) {
                            printInfo("&<nomegrupo>: <mensagem>");
                            linha = entrada.readLine();
                            if (linha != null) {
                                partes = linha.split(":");
                                if (partes.length == 2 && partes[0].charAt(0) == '&')
                                    enviarMensagemGrupo(partes);
                                else
                                    printAviso("Formato Invalido");
                            }
                        } else
                            printAviso("Usuario Nao esta Online");
                        break;
                    }
                    // &<nomegrupo><@usuario1,@usuario2>: <mensagem>: Envia uma mensagem dentro do grupo apenas para os participantes especificados.
                    case "mensagemgruposeletiva": {
                        if (usuarioLogado != null && usuarioLogado.getStatus().equals("online")) {
                            printInfo("<&nomegrupo><@usuario1,@usuario2> : <mensagem>");
                            linha = entrada.readLine();
                            if (linha != null) {
                                partes = linha.split(":");
                                if (partes.length == 2) {
                                    String cabecalho = partes[0].trim();
                                    String conteudo = partes[1].trim();

                                    if (cabecalho.startsWith("&") && cabecalho.contains("@")) {
                                        String[] partesCabecalho = cabecalho.split("@");
                                        //pega o nome do grupo tirando o & da frente
                                        String nomeGrupo = partesCabecalho[0].substring(1).trim();

                                        List<String> listaUsuarios = new ArrayList<>();

                                        //indice 0 é nome do grupo
                                        for (int i = 1; i < partesCabecalho.length; i++) {
                                            String usuario = partesCabecalho[i].trim();
                                            if (!usuario.isEmpty())
                                                listaUsuarios.add(usuario);
                                        }

                                        if (nomeGrupo.isEmpty() || listaUsuarios.isEmpty())
                                            printAviso("Campo(s) Nao Informado(s)");
                                        else
                                            enviarMensagemGrupoSeletiva(nomeGrupo, listaUsuarios, conteudo);
                                    } else
                                        printAviso("Formato do Cabecalho Invalido");
                                } else
                                    printAviso("Formato Invalido");
                            } else
                                printAviso("Formato Invalido");
                        } else
                            printAviso("Usuario Nao esta Online");
                        break;
                    }
                    case "sim": {
                        if (solicitacaoMensagem != null) {
                            printSucesso("Voce Aceitou a Solicitacao de @" + solicitacaoMensagem.getApelido());
                            for (int i = 0; i < clientesConectados.size(); i++) {
                                ClientHandler clientHandler = clientesConectados.get(i);
                                if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitacaoMensagem.getId())) {
                                    clientHandler.printSucesso("@" + usuarioLogado.getApelido() + " Aceitou sua Solicitacao");
                                    mensageiroFacade.confirmarSolicitacao(solicitacaoMensagem.getId(), usuarioLogado.getId(), "Confirmada");
                                }
                            }
                            solicitacaoMensagem = null;
                        } else {
                            printAviso("Voce Nao Tem Solicitacoes Pendentes");
                        }
                        break;
                    }
                    case "nao": {
                        if (solicitacaoMensagem != null) {
                            printSucesso("Voce Recusou a Solicitacao de @" + solicitacaoMensagem.getApelido());
                            for (int i = 0; i < clientesConectados.size(); i++) {
                                ClientHandler clientHandler = clientesConectados.get(i);
                                if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitacaoMensagem.getId())) {
                                    clientHandler.printSucesso("@" + usuarioLogado.getApelido() + " Recusou sua Solicitacao");
                                    mensageiroFacade.confirmarSolicitacao(solicitacaoMensagem.getId(), usuarioLogado.getId(), "Recusada");
                                }
                            }
                            solicitacaoMensagem = null;
                        } else {
                            printAviso("Voce Nao Tem Solicitacoes Pendentes");
                        }
                    }
                    default: {
                        printAviso("Comando Invalido");
                        break;
                    }
                }
                //saida.println();

            } while (!comando.equals("logout"));

        } catch (Exception e) {
            printErro("Erro na Conexão com o Cliente: " + e.getMessage());
        } finally {
            try {
                if (usuarioLogado != null) {
                    desconectarUsuario();
                    clientesConectados.remove(this);
                }
                if (entrada != null)
                    entrada.close();
                if (saida != null)
                    saida.close();
                if (clienteSocket != null && !clienteSocket.isClosed())
                    clienteSocket.close();
            } catch (Exception e) {
                printErro(e.getMessage());
            }
        }
    }

    private void desconectarUsuario() {
        try {
            if (usuarioLogado != null) {
                mensageiroFacade.alterarStatus(usuarioLogado.getId(), "offline");
                usuarioLogado = null;
            }
        } catch (Exception e) {
            System.out.println("Erro ao desconectar usuario: " + e.getMessage());
        }
    }

    private void enviarMensagemGrupo(String[] partes) {
        try {
            String nomeGrupo = partes[0];
            String conteudo = partes[1].trim();

            if (usuarioLogado != null) {
                Grupo grupo = grupoService.getByName(nomeGrupo);
                if (grupo != null) {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setConteudo(conteudo);
                    ResultadoOperacao<Mensagem> resultado = mensageiroFacade.enviarMensagemNoGrupo(mensagem, usuarioLogado.getId(), grupo.getId());

                    if (resultado.isSucesso())
                        printSucesso(resultado.getMensagem());
                    else
                        printErro(resultado.getMensagem());
                } else
                    printErro("Grupo Nao Encontrado");
            } else
                printAviso("Usuario Nao Logado");
        } catch (Exception e) {
            printErro("Erro ao Enviar Mensagem No Grupo");
        }

    }

    private void enviarMensagemGrupoSeletiva(String nomeGrupo, List<String> apelidoUsuarios, String conteudo) {
        try {
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if (grupo != null) {
                //pegar ids usuarios
                List<Long> usuariosIds = new ArrayList<>();
                boolean EncontrouUsuario = true;
                for (int i = 0; i < apelidoUsuarios.size() && EncontrouUsuario; i++) {
                    Usuario usuario = usuarioService.getByApelido(apelidoUsuarios.get(i));
                    if (usuario != null)
                        usuariosIds.add(usuario.getId());
                    else {
                        printErro("Usuario " + apelidoUsuarios.get(i) + "Nao Encontrado");
                        EncontrouUsuario = false;
                    }
                }
                if (EncontrouUsuario) {
                    EnviarMensagemSeletivaGrupoDTO dto = new EnviarMensagemSeletivaGrupoDTO();
                    dto.setConteudo(conteudo);
                    dto.setRemetenteId(usuarioLogado.getId());
                    dto.setUsuariosIds(usuariosIds);

                    ResultadoOperacao<String> resultado = mensageiroFacade.enviarMensagemSeletivaGrupo(grupo.getId(), dto);

                    if (resultado.isSucesso())
                        printSucesso(resultado.getMensagem());
                    else
                        printErro(resultado.getMensagem());
                }
            } else
                printErro("Grupo Nao Encontrado");
        } catch (Exception e) {
            printErro("Erro ao Enviar Mensagem Seletiva No Grupo");
        }

    }

    private void sairGrupo(String nomeGrupo) {
        try {

            if (usuarioLogado != null) {
                Grupo grupo = grupoService.getByName(nomeGrupo);
                if (grupo != null) {
                    ResultadoOperacao<Erro> resultado = mensageiroFacade.deleteUsuarioGrupo(usuarioLogado.getId(), grupo.getId());
                    if (resultado.isSucesso())
                        printSucesso(resultado.getMensagem());
                    else
                        printErro(resultado.getMensagem());
                } else
                    printErro("Grupo Nao Encontrado");
            } else
                printAviso("Usuario Nao Logado");
        } catch (Exception e) {
            printErro("Erro ao Sair Do Grupo");
        }
    }

    private void entrarGrupo(String nomeGrupo)
    {
        try
        {
            if(nomeGrupo != null && !nomeGrupo.isBlank())
            {
                if(nomeGrupo.startsWith("&"))
                {
                    //tira & do nome do grupo
                    nomeGrupo = nomeGrupo.substring(1).trim();
                    if(!nomeGrupo.isBlank())
                    {
                        ResultadoOperacao<List<VotoSolicitacao>> resultado = mensageiroFacade.solicitarEntradaGrupo(nomeGrupo,usuarioLogado.getId());
                        if(resultado.isSucesso())
                            printSucesso(resultado.getMensagem());
                        else
                            printErro(resultado.getMensagem());
                    }
                    else
                        printAviso("Nome do Grupo Nao informado");
                }
                else
                    printAviso("Referencia de Grupo Incorreta");
            }
            else
                printAviso("Grupo Nao Informado");
        }
        catch (Exception e)
        {
            printErro("Erro Ao Solicitar Entrada No Grupo");
        }
    }


    private void listaGrupos() {
        ResultadoOperacao<List<Grupo>> resultado = mensageiroFacade.getAllGrupos();

        if (resultado.getDados() != null && resultado.getDados().size() > 0) {
            printInfo("GRUPO(S):");
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo(resultado.getDados().get(i).getNome());

            }
        } else {
            printAviso("Nenhum Grupo Criado");
        }

    }

    private void listaSolicitacoes() {
        ResultadoOperacao<List<SolicitacaoMensagem>> retorno = mensageiroFacade.getAllSolicitacoesById(usuarioLogado.getId());

        if (retorno != null && !retorno.getDados().isEmpty()) {
            for (int i = 0; i < retorno.getDados().size(); i++) {
                if (usuarioLogado.getId() == retorno.getDados().get(i).getUsuario2().getId())
                    printInfo("<- Pedido recebido de: " + retorno.getDados().get(i).getUsuario1().getApelido() + "[" + retorno.getDados().get(i).getStatus() + "]");
                else
                    printInfo("-> Pedido enviado para: " + retorno.getDados().get(i).getUsuario2().getApelido() + "[" + retorno.getDados().get(i).getStatus() + "]");
            }
            printSucesso(retorno.getMensagem());
        } else
            printErro(retorno.getMensagem());
    }

    private void listaUsuarios() {
        ResultadoOperacao<List<Usuario>> resultado = mensageiroFacade.getAllUsersOnline();
        if (resultado.getDados() != null && !resultado.getDados().isEmpty()) {
            printInfo("USUARIO(S):");
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo(resultado.getDados().get(i).getApelido());
            }
        } else {
            printAviso("Nenhum Usuario Online");
        }
    }

    private void listaConversasParticulares() {
        ResultadoOperacao<List<SolicitacaoMensagem>> resultado = mensageiroFacade.getConversasParticulares(usuarioLogado.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty()) {
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo("User: " + resultado.getDados().get(i).getUsuario1().getApelido());
            }
            printSucesso(resultado.getMensagem());
        } else
            printErro(resultado.getMensagem());
    }

    private void listConvites() {
        ResultadoOperacao<List<ConviteGrupo>> resultado = mensageiroFacade.getAllConvitesByConvidado(usuarioLogado.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty()) {
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo("Grupo: " + resultado.getDados().get(i).getGrupo() + " [" + resultado.getDados().get(i).getStatus() + "]");
            }
            printSucesso(resultado.getMensagem());
        } else
            printErro(resultado.getMensagem());
    }

    private void recuperarMensagensPendentes() {
        List<DestinatarioMensagem> pendentes = mensageiroFacade.getMensagensDestinariosPendenteByUser(usuarioLogado.getId(), "Pendente");
        if (pendentes != null && pendentes.size() > 0) {
            printAviso("Voce Tem " + pendentes.size() + " Mensagens Pendentes");
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
            printInfo("===================================");
            for (int i = 0; i < pendentes.size(); i++) {
                DestinatarioMensagem destinatarioMensagem = pendentes.get(i);
                Mensagem mensagem = destinatarioMensagem.getMensagem();
                String horarioEnvio = mensagem.getDataHoraEnvio().format(formatador);
                printInfo("[" + horarioEnvio + "] " + "@" + mensagem.getRemetente().getApelido() + ": " + mensagem.getConteudo());
                mensageiroFacade.confirmarEntregaMensagem(mensagem.getId(), usuarioLogado.getId());
            }
            printInfo("===================================");
        }
    }

    private void recuperarSolicitacoesMensagemPendentes() {
        ResultadoOperacao<List<SolicitacaoMensagem>> resultado = mensageiroFacade.getAllSolicitacoesByIdPendentes(usuarioLogado.getId(), "Pendente");
        List<SolicitacaoMensagem> pendentes = resultado.getDados();
        if (pendentes != null && pendentes.size() > 0) {
            printAviso("Voce Tem " + pendentes.size() + " Solicitacoes de Mensagens Pendentes");
            printInfo("===================================");
            for (int i = 0; i < pendentes.size(); i++) {
                SolicitacaoMensagem solicitacaoMensagem = pendentes.get(i);
                printInfo("@" + solicitacaoMensagem.getUsuario1().getApelido() + " Enviou uma Solicitacao de Mensagem");
            }
            printInfo("===================================");
        }
    }

    private void recuperarConvitesGruposPendentes() {

    }

    private void recuperarVotosSolicitacoesPendentes() {

    }

    private void recuperarSolicitacaoEntradaGrupo()
    {

    }
    private void enviarMensagem(String[] partes) {
        String destinatario = partes[0].trim();
        String mensagem = partes[1];
        if (destinatario.charAt(0) == '@' && destinatario.charAt(destinatario.length() - 1) == ':') {
            Usuario usuario = usuarioService.getByApelido(destinatario.substring(1, destinatario.length() - 1));
            ResultadoOperacao<DestinatarioMensagem> resultado;
            if (usuario != null) {
                resultado = mensageiroFacade.enviarMensagemPessoa(usuarioLogado.getId(), usuario.getId(), mensagem);
                if (resultado.getMensagem().equals("Mensagem Enviada com Sucesso")) {
                    for (int i = 0; i < clientesConectados.size(); i++) {
                        ClientHandler clientHandler = clientesConectados.get(i);
                        if (clientHandler.usuarioLogado != null && clientHandler.usuarioLogado.getId().equals(usuario.getId())) {
                            if (clientHandler.usuarioLogado.getStatus().equals("online")) {
                                DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
                                String horarioAtual = LocalDateTime.now().format(formatador);
                                String mensagemNova = "[" + horarioAtual + "]" + " @" + this.usuarioLogado.getApelido() + ":" + mensagem;
                                clientHandler.printAviso(mensagemNova);
                                mensageiroFacade.confirmarEntregaMensagem(resultado.getDados().getMensagem().getId(), clientHandler.usuarioLogado.getId());
                            }
                        }
                    }
                } else if (resultado.getMensagem().contains("Enviando Solicitacao de Mensagem Para")) {
                    for (int i = 0; i < clientesConectados.size(); i++) {
                        ClientHandler clientHandler = clientesConectados.get(i);
                        if (clientHandler.usuarioLogado != null && clientHandler.usuarioLogado.getId().equals(usuario.getId())) {
                            clientHandler.solicitacaoMensagem = this.usuarioLogado; // salva na memoria para o destinátario saber quem é o remetente
                            clientHandler.printAviso("[SISTEMA]: O Usuario @" + usuarioLogado.getApelido() + " Deseja Enviar uma Mensagem Privada Para Voce. Voce Aceita ? (SIM ou NAO)");
                        }
                    }
                }
                printSucesso(resultado.getMensagem());
            } else
                printErro("Usuario Nao Encontrado");

        } else {
            printAviso("Referencia de Destinatario Incorreta");
        }

    }

    private void criarGrupo(String nomeGrupo) {
        ResultadoOperacao<Grupo> retorno = mensageiroFacade.criarNovoGrupo(nomeGrupo, usuarioLogado.getId(), null);
        String mensagem = retorno.getMensagem();
        boolean sucesso = retorno.isSucesso();

        if (sucesso)
            printSucesso(mensagem);
        else
            printErro(mensagem);
    }

    private boolean cadastrarUsuario(String[] partes) {
        try {
            String nomeCompleto = partes[0];
            String apelido = partes[1];
            String email = partes[2];
            String senha = partes[3];
            Usuario usuario = new Usuario();
            usuario.setNome(nomeCompleto);
            usuario.setApelido(apelido);
            usuario.setEmail(email);
            usuario.setSenha(senha);
            usuario.setStatus("offline");

            Usuario usuarioExiste = usuarioService.getByEmail(email);
            if (usuarioExiste == null) {
                usuarioExiste = usuarioService.getByApelido(apelido);
                if (usuarioExiste == null) {
                    usuarioService.salvar(usuario);
                    printSucesso("Cadastrado Com Sucesso");
                    return true;
                } else
                    printAviso("Login Ja Cadastrado");
            } else
                printAviso("Email Ja Cadastrado");

        } catch (Exception e) {

            printErro("Erro Ao Cadastrar");
            return false;
        }
        return false;
    }

    private void inserirNoGrupo(String[] partes) {
        String nomeGrupo = partes[0];
        List<Usuario> users = new ArrayList<>();
        for (int i = 1; i < partes.length; i++) {
            String[] splitVirgula = partes[i].split(",");
            for (int j = 0; j < splitVirgula.length; j++) {
                Usuario user = usuarioService.getByApelido(splitVirgula[j].trim());
                if (user != null)
                    users.add(user);
            }
        }
        ResultadoOperacao<List<ConviteGrupo>> retorno = mensageiroFacade.addUsersGrupo(nomeGrupo, usuarioLogado.getId(), users);
        String mensagem = retorno.getMensagem();
        boolean sucesso = retorno.isSucesso();

        if (sucesso)
            printSucesso(mensagem);
        else
            printErro(mensagem);
    }

    private boolean logar(String[] partes) {
        try {
            Usuario usuario = usuarioService.logar(partes[0], partes[1]);
            if (usuario != null) {
                clientesConectados.add(this);
                printSucesso("Login Realizado Com Sucesso");
                usuarioLogado = usuario;
                usuarioLogado = mensageiroFacade.alterarStatus(usuarioLogado.getId(), "online").getDados();
                recuperarMensagensPendentes();
                recuperarSolicitacoesMensagemPendentes();
                return true;
            } else {
                printAviso("Usuario Nao Encontrado");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            printErro("erro!! " + e.getMessage());
            return false;
        }
    }

    private boolean recuperarSenha(String email) {
        try {
            Usuario usuario = usuarioService.getByEmail(email);
            if (usuario != null) {
                printInfo("Senha: " + usuario.getSenha());
                return true;
            } else {
                printAviso("Email Nao Encontrado");
                return false;
            }
        } catch (Exception e) {
            printErro("Erro Ao Recuperar");
            return false;
        }

    }

    private boolean status() {
        try {
            if (usuarioLogado == null) {
                printAviso("Usuario Nao Logado");
                return false;
            } else {
                printInfo("Status: " + usuarioLogado.getStatus());
                return true;
            }
        } catch (Exception e) {
            printErro("Erro Ao Buscar Status");
            return false;
        }
    }

    private boolean definirStatus(String status) {
        try {
            if (usuarioLogado == null) {
                printErro("Usuario Nao Logado!!");
                return false;
            }

            ResultadoOperacao<Usuario> resultado = mensageiroFacade.alterarStatus(usuarioLogado.getId(), status.toLowerCase());
            String mensagem = resultado.getMensagem();
            boolean sucesso = resultado.isSucesso();
            usuarioLogado.setStatus(resultado.getDados().getStatus());
            saida.println(mensagem);
            System.out.println(mensagem);
            return sucesso;
        } catch (Exception e) {
            printErro("Erro Ao Buscar Status");
            return false;
        }
    }

    private void responderConvite(String nomeGrupo, String resposta)
    {
        if(resposta.equalsIgnoreCase("sim"))
            resposta = "Confirmado";
        else
            resposta = "Recusado";

        ResultadoOperacao<ConviteGrupo> resultado = mensageiroFacade.responderConvite(nomeGrupo, usuarioLogado.getId(), resposta);
        if(resultado.isSucesso())
            printSucesso(resultado.getMensagem());
        else
            printErro(resultado.getMensagem());
    }

    //funções auxiliares exibição
    private void printInfo(String msg) {
        saida.println(CYAN + msg + RESET);
    }

    private void printSucesso(String msg) {
        saida.println(GREEN + msg + RESET);
    }

    private void printErro(String msg) {
        saida.println(RED + msg + RESET);
    }

    private void printAviso(String msg) {
        saida.println(YELLOW + msg + RESET);
    }
}
