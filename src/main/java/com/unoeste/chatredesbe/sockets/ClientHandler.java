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
    private final UsuarioGrupoService usuarioGrupoService;
    private final SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;

    private Socket clienteSocket;
    private BufferedReader entrada;
    private PrintWriter saida;
    private Usuario usuarioLogado = null;
    private Usuario solicitacaoMensagem = null;
    private Usuario conversaAtual = null;
    private Grupo conversaGrupoAtual = null;
    private List<ConviteGrupo> convitesGruposPendetes = new ArrayList<>();
    public static List<ClientHandler> clientesConectados = new ArrayList<>();

    public ClientHandler(Socket clienteSocket, UsuarioService usuarioService, GrupoService grupoService, MensageiroFacade mensageiroFacade, UsuarioGrupoService usuarioGrupoService, SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService) {
        this.clienteSocket = clienteSocket;
        this.usuarioService = usuarioService;
        this.grupoService = grupoService;
        this.mensageiroFacade = mensageiroFacade;
        this.usuarioGrupoService = usuarioGrupoService;
        this.solicitacaoEntradaGrupoService = solicitacaoEntradaGrupoService;
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
                    // PERFIL E AUTENTICAÇÃO ===========================================================================
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
                        break;
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

                    // VISUALIZAÇÃO E LISTAGENS ========================================================================
                    case "ajuda": {
                        printInfo("======== COMANDOS ======== ");
                        // PERFIL
                        printInfo("cadastrar");
                        printInfo("login");
                        printInfo("logout");
                        printInfo("recuperarSenha");
                        printInfo("status");
                        printInfo("definirStatus");

                        // LISTAGENS
                        printInfo("listaUsuarios");
                        printInfo("listaGrupos");
                        printInfo("listaSolicitacoes");
                        printInfo("listaConversas");
                        printInfo("listaConvites");
                        printInfo("listaSolicitacoesGrupos");

                        // SOLICITAÇÕES E MENSAGENS PARTICULARES
                        printInfo("aceitarSolicitacao");
                        printInfo("recusarSolicitacao");
                        printInfo("enviarMensagem");
                        printInfo("cd");

                        // CONVITES E GRUPOS
                        printInfo("responderConvite");
                        printInfo("novoGrupo");
                        printInfo("inserir");
                        printInfo("entrarGrupo");
                        printInfo("sairGrupo");
                        printInfo("mensagemGrupo");
                        printInfo("responderPrivado");
                        printInfo("cdg");
                        printInfo("votar");
                        printInfo("mensagemGrupoSeletiva");

                        // RESPOSTAS EM TEMPO REAL
                        printInfo("sim");
                        printInfo("nao");
                        printInfo("simgrupo");
                        printInfo("naogrupo");

                        printInfo("=========================== ");
                        break;
                    }
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
                        {
                            listaConversasParticulares();
                            listaConversasGrupos();
                        }
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
                    case "listasolicitacoesgrupos": {
                        if (usuarioLogado != null)
                            listaSolicitGrupos();
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    // SOLICITAÇÕES E MENSAGENS PARTICULARES ===========================================================
                    case "aceitarsolicitacao": {
                        if (usuarioLogado != null ) {
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
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "recusarsolicitacao": {
                        if(usuarioLogado == null) {
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
                        }
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
                    case "cd":{
                        // CHANGE DIRECTORY, PARA IR EM CONVERSAS PARTICULARES
                        if (usuarioLogado != null)
                        {
                            printInfo("( <usuario> | <..> )");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(" ");
                                if (partes.length == 1)
                                {
                                    if(conversaAtual != null) // esta conversando no particular
                                    {
                                        if(partes[0].equalsIgnoreCase(".."))
                                        {
                                            conversaAtual = null;
                                            printSucesso("Saindo da conversa..");
                                        }
                                        else
                                            printAviso("Invalido");
                                    }
                                    else if(conversaGrupoAtual != null) // esta conversando no grupo
                                        printAviso("Voce esta conversando em um grupo, para sair, use: cdg ..");
                                    else
                                    {
                                        Usuario usuario = usuarioService.getByApelido(partes[0]);
                                        if(usuario == null)
                                            printErro("Esse usuario não existe!!");
                                        else
                                        {
                                            // verificar se esse usuário pode conversar com esse usuario
                                            ResultadoOperacao<List<Mensagem>> retorno = mensageiroFacade.getMensagensConversa(usuarioLogado.getId(), usuario.getId());
                                            if(retorno.isSucesso())
                                            {
                                                // aqui pode conversar, setar todas as mensagens dessa conversa como lidas

                                                mensageiroFacade.setarMensagensComoLida(usuarioLogado.getId(), usuario.getId(), retorno.getDados());
                                                listarMensagensConversaParticular(retorno.getDados(), usuario);
                                                printSucesso(retorno.getMensagem());
                                                conversaAtual = usuario;
                                            }
                                            else
                                                printErro(retorno.getMensagem());
                                        }
                                    }
                                }
                                else
                                    printAviso("Campo(s) Nao Informado");
                            }
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    // CONVITES E GRUPOS ===============================================================================
                    case "responderconvite": {
                        if (usuarioLogado != null) {
                            printAviso("<nomegrupo> <sim|nao>");
                            linha = entrada.readLine();
                            if (linha != null) {
                                partes = linha.split(" ");
                                if (partes.length == 2) {
                                    Grupo grupo = grupoService.getByName(partes[0]);
                                    responderConvite(grupo, partes[1]);
                                } else {
                                    printAviso("Campo(s) Nao Informado");
                                }
                            }
                        } else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "novogrupo": {
                        // novogrupo <nomegrupo>: Cria um novo grupo e adiciona o criador automaticamente nele.
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
                    case "entrargrupo": {
                        // entrargrupo &<nomegrupo>: Solicita a entrada em um grupo do qual o usuário ainda não faz parte.
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
                        break;
                    }
                    case "sairgrupo": {
                        // sair &<nomegrupo>: O usuário logado sai do grupo especificado (avisando os demais).
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
                    case "mensagemgrupo": {
                        // &<nomegrupo>: <mensagem>: Envia uma mensagem para todos os participantes do grupo.
                        if (usuarioLogado != null && usuarioLogado.getStatus().equals("online"))
                        {
                            printInfo("<nomegrupo>: <mensagem>");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(":");
                                if (partes.length == 2)
                                    enviarMensagemGrupo(partes);
                                else
                                    printAviso("Formato Invalido");
                            }
                        }
                        else if(usuarioLogado == null)
                            printAviso("Usuario Nao Logado");
                        else
                            printAviso("Usuario Nao esta Online");
                        break;
                    }
                    case "responderprivado": {
                        // estando um grupo, o usuário tem a opção de enviar a mensagem privativa para um usuário
                        // responderprivado <usuario>
                        if(usuarioLogado!= null)
                        {
                            printInfo("( <usuario> : <mensagem> )");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(":");
                                // será divido em duas partes:
                                //      0 -> usuário
                                //      1 -> mensagem
                                if (partes.length == 2)
                                {
                                    if(conversaGrupoAtual != null) // esta conversando no grupo -> pode enviar uma mensagem seletiva
                                    {
                                        String mensagem = partes[1].trim();
                                        Usuario usuario = usuarioService.getByApelido(partes[1].trim());
                                        if(usuario != null)
                                        {
                                            ResultadoOperacao<DestinatarioMensagem> ro = mensageiroFacade.enviarMensagemPessoa(usuarioLogado.getId(), usuario.getId(), mensagem);
                                            if(ro.isSucesso())
                                            {
                                                printSucesso(ro.getMensagem());
                                                mandarMensagemTempoReal(usuario, mensagem, ro.getDados());
                                            }
                                            else
                                                printErro(ro.getMensagem());
                                        }
                                        else
                                            printErro("Esse usuário não existe!!");
                                    }
                                    else if(conversaAtual != null) // esta conversando no particular
                                        printAviso("Voce esta em uma conversa particular, para sair, use: cd ..");
                                    else
                                        printErro("Voce nao esta em um chat de grupo, nao pode realizar a operacao!!");
                                }
                                else
                                    printAviso("Campo(s) Nao Informado");
                            }
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "cdg":{
                        // CHANGE DIRECTORY DE GRUPOS, PARA IR EM CONVERSAS EM GRUPOS
                        if (usuarioLogado != null)
                        {
                            printInfo("( <nomeGrupo> | <..> )");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(" ");
                                if (partes.length == 1)
                                {
                                    if(conversaGrupoAtual != null) // esta conversando no grupo
                                    {
                                        if(partes[0].equalsIgnoreCase(".."))
                                        {
                                            conversaGrupoAtual = null;
                                            printSucesso("Saindo da conversa..");
                                        }
                                        else
                                            printAviso("Invalido");
                                    }
                                    else if(conversaAtual != null) // esta conversando no particular
                                        printAviso("Voce esta em uma conversa particular, para sair, use: cd ..");
                                    else
                                    {
                                        Grupo grupo = grupoService.getByName(partes[0]);
                                        if(grupo == null)
                                            printErro("Esse grupo não existe!!");
                                        else
                                        {
                                            // verificar se esse usuário pode ver as mensagens do grupo
                                            UsuarioGrupo ug = usuarioGrupoService.getByUsuarioGrupo(grupo.getId(), usuarioLogado.getId());
                                            if(ug == null)
                                                printErro("Voce nao pertence a esse grupo!!");
                                            else {
                                                listarMensagensGrupoId(grupo);
                                                conversaGrupoAtual = grupo;
                                            }
                                        }
                                    }
                                }
                                else
                                    printAviso("Campo(s) Nao Informado");
                            }
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "votar": {
                        // votar &<nomegrupo> @<usuario_solicitante> <sim|nao>: Resposta dos membros atuais do grupo aprovando ou negando a entrada do solicitante.
                        if (usuarioLogado != null)
                        {
                            printInfo("( <grupo>  <usuarioSolicitante> < sim | nao >)");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(" ");
                                if (partes.length == 3)
                                {
                                    // Partes:
                                    //      0 -> grupo
                                    //      1 -> usuario
                                    //      2 -> voto
                                    Grupo grupo = grupoService.getByName(partes[0].trim());
                                    Usuario usuario = usuarioService.getByApelido(partes[1].trim());
                                    String voto = partes[2].trim();
                                    boolean votoValido = true;
                                    Integer votoFinal = 0;
                                    if(voto.trim().equalsIgnoreCase("sim"))
                                        votoFinal = 1;
                                    else if(voto.trim().equalsIgnoreCase("nao"))
                                        votoFinal = 0;
                                    else
                                        votoValido = false;
                                    if (votoValido)
                                    {
                                        // pegar a solicitação do banco de dados
                                        SolicitacaoEntradaGrupo seg = solicitacaoEntradaGrupoService.getByGrupoSolicitante(grupo.getId(), usuario.getId());
                                        if(seg != null)
                                        {
                                            ResultadoOperacao<StatusSolicitacaoVotacao> ro =
                                                    mensageiroFacade.votarSolicitacaoEntradaGrupo(
                                                            usuarioLogado.getId(),
                                                            seg.getId(),
                                                            votoFinal
                                                    );

                                            if (ro.isSucesso())
                                            {
                                                printSucesso(ro.getMensagem());

                                                if (ro.getDados() == StatusSolicitacaoVotacao.PERMITIDO)
                                                {
                                                    // usuário foi admitido
                                                    avisarUsuarioAdmitidoTempoReal(usuario, grupo);
                                                }
                                                else if (ro.getDados() == StatusSolicitacaoVotacao.NEGADO)
                                                {
                                                    // usuário foi negado
                                                    avisarUsuarioNegadoTempoRal(usuario, grupo);
                                                }
                                            }
                                            else
                                            {
                                                printErro(ro.getMensagem());
                                            }
                                        }
                                        else
                                            printErro("Nao existe essa solicitacao!!");
                                    }
                                    else
                                        printErro("Voto invalido!!");
                                }
                                else
                                    printAviso("Campo(s) Nao Informado");
                            }
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "mensagemgruposeletiva": {
                        // estando na conversa de um grupo, pode ser possível enviar mensagens individuais para membros desse grupo
                        if(usuarioLogado!= null)
                        {
                            printInfo("( <usuario> (,<outroUsuario>) : <mensagem> )");
                            linha = entrada.readLine();
                            if (linha != null)
                            {
                                partes = linha.split(":");
                                // será divido em duas partes:
                                //      0 -> usuários
                                //      1 -> mensagem
                                if (partes.length == 2)
                                {
                                    if(conversaGrupoAtual != null) // esta conversando no grupo -> pode enviar uma mensagem seletiva
                                    {
                                        EnviarMensagemSeletivaGrupoDTO emsg = new EnviarMensagemSeletivaGrupoDTO();
                                        List<Long> usersId = new ArrayList<>();
                                        String[] possiveisUsers = partes[0].split(",");
                                        // povoar a lista de ID -> dos usuários para o envio da mensagem
                                        for(String pu : possiveisUsers)
                                        {
                                            Usuario user = usuarioService.getByApelido(pu.trim());
                                            usersId.add(user.getId());
                                        }
                                        emsg.setUsuariosIds(usersId);
                                        emsg.setRemetenteId(usuarioLogado.getId());
                                        emsg.setConteudo(partes[1].trim());
                                        ResultadoOperacao<String> ro = mensageiroFacade.enviarMensagemSeletivaGrupo(conversaGrupoAtual.getId(), emsg);
                                        if(ro.isSucesso())
                                        {
                                            printSucesso(ro.getMensagem());
                                            mandarMensagemTempoRealGrupoSeletivo(conversaGrupoAtual, partes[1].trim(), usersId);
                                        }
                                        else
                                            printErro(ro.getMensagem());
                                    }
                                    else if(conversaAtual != null) // esta conversando no particular
                                        printAviso("Voce esta em uma conversa particular, para sair, use: cd ..");
                                }
                                else
                                    printAviso("Campo(s) Nao Informado");
                            }
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    // RESPOSTAS =======================================================================================
                    case "sim": {
                        if(usuarioLogado != null)
                        {
                            if (solicitacaoMensagem != null)
                            {
                                printSucesso("Voce Aceitou a Solicitacao de @" + solicitacaoMensagem.getApelido());
                                for (int i = 0; i < clientesConectados.size(); i++)
                                {
                                    ClientHandler clientHandler = clientesConectados.get(i);
                                    if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitacaoMensagem.getId()))
                                    {
                                        clientHandler.printSucesso("@" + usuarioLogado.getApelido() + " Aceitou sua Solicitacao");
                                        mensageiroFacade.confirmarSolicitacao(solicitacaoMensagem.getId(), usuarioLogado.getId(), "Confirmada");
                                    }
                                }
                                solicitacaoMensagem = null;
                            }
                            else
                                printAviso("Voce Nao Tem Solicitacoes Pendentes");
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "simgrupo": {
                        // ACEITAR O CONVITE DE UM GRUPO
                        if(usuarioLogado != null)
                        {
                            if (!convitesGruposPendetes.isEmpty())
                            {
                                ConviteGrupo conviteGrupo = convitesGruposPendetes.get(0);
                                convitesGruposPendetes.remove(0);
                                ResultadoOperacao<ConviteGrupo> resultado = mensageiroFacade.alterarConviteGrupo(conviteGrupo.getGrupo().getId(), conviteGrupo.getConvidado().getId(), 1);
                                if (resultado.isSucesso())
                                {
                                    printSucesso("[SISTEMA]: Voce Entrou no Grupo" +" ("+conviteGrupo.getGrupo().getNome()+")" );
                                    for (int i = 0; i < clientesConectados.size(); i++)
                                    {
                                        ClientHandler clientHandler = clientesConectados.get(i);
                                        if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(conviteGrupo.getSolicitante().getId()))
                                        {
                                            clientHandler.printSucesso("[SISTEMA]: "+"@"+usuarioLogado.getApelido()+" Aceitou seu Convite do Grupo" +" ("+conviteGrupo.getGrupo().getNome()+")" );
                                        }
                                    }
                                }
                                else
                                    printErro(resultado.getMensagem());
                            }
                            else
                                printAviso("Voce Nao Tem Convites Pendentes");
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "nao": {
                        if(usuarioLogado != null) {
                            if (solicitacaoMensagem != null)
                            {
                                printSucesso("Voce Recusou a Solicitacao de @" + solicitacaoMensagem.getApelido());
                                for (int i = 0; i < clientesConectados.size(); i++)
                                {
                                    ClientHandler clientHandler = clientesConectados.get(i);
                                    if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(solicitacaoMensagem.getId()))
                                    {
                                        clientHandler.printSucesso("@" + usuarioLogado.getApelido() + " Recusou sua Solicitacao");
                                        mensageiroFacade.confirmarSolicitacao(solicitacaoMensagem.getId(), usuarioLogado.getId(), "Recusada");
                                    }
                                }
                                solicitacaoMensagem = null;
                            }
                            else
                                printAviso("Voce Nao Tem Solicitacoes Pendentes");
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }
                    case "naogrupo": {
                        if(usuarioLogado != null) {
                            if (!convitesGruposPendetes.isEmpty())
                            {
                                ConviteGrupo conviteGrupo = convitesGruposPendetes.get(0);
                                convitesGruposPendetes.remove(0);
                                ResultadoOperacao<ConviteGrupo> resultado = mensageiroFacade.alterarConviteGrupo(conviteGrupo.getGrupo().getId(), conviteGrupo.getConvidado().getId(), 2);
                                if (resultado.isSucesso())
                                {
                                    printSucesso("[SISTEMA]: "+"@"+usuarioLogado.getApelido()+" Recusou seu Convite do Grupo" +" ("+conviteGrupo.getGrupo().getNome()+")" );
                                    for (int i = 0; i < clientesConectados.size(); i++)
                                    {
                                        ClientHandler clientHandler = clientesConectados.get(i);
                                        if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(conviteGrupo.getSolicitante().getId()))
                                        {
                                            clientHandler.printSucesso("[SISTEMA]: "+"@"+usuarioLogado.getApelido()+" Recusou seu Convite do Grupo" +" ("+conviteGrupo.getGrupo().getNome()+")" );
                                        }
                                    }
                                }

                            }
                            else
                                printAviso("Voce Nao Tem Convites Pendentes");
                        }
                        else
                            printAviso("Usuario Nao Logado");
                        break;
                    }

                    default: {
                        if(conversaAtual != null)
                        {
                            // está em uma conversa particular, apenas cria uma novo mensagem
                            ResultadoOperacao<DestinatarioMensagem> retorno = mensageiroFacade.enviarMensagemPessoa(usuarioLogado.getId(), conversaAtual.getId(), linha);
                            mandarMensagemTempoReal(conversaAtual, linha, retorno.getDados());
                            if(!retorno.isSucesso())
                                printAviso(retorno.getMensagem());
                        }
                        else if(conversaGrupoAtual != null)
                        {
                            // está conversando no grupo, apenas cria uma nova mensagem para todos do grupo
                            //String mensagem = criaMensagem(partes);
                            ResultadoOperacao<Mensagem> retorno = mensageiroFacade.enviarMensagemNoGrupo(
                                    new Mensagem(null, linha, null, null),
                                    usuarioLogado.getId(),
                                    conversaGrupoAtual.getId()
                            );
                            mandarMensagemTempoRealGrupo(conversaGrupoAtual, linha);
                            if(!retorno.isSucesso())
                                printAviso(retorno.getMensagem());
                        }
                        else
                            printAviso("Comando Invalido");
                        break;
                    }
                }
            } while (!comando.equals("logout"));
        }
        catch (Exception e)
        {
            printErro("Erro na Conexão com o Cliente: " + e.getMessage());
        }
        finally
        {
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

            if (usuarioLogado != null)
            {
                Grupo grupo = grupoService.getByName(nomeGrupo);
                if (grupo != null)
                {
                    Mensagem mensagem = new Mensagem();
                    mensagem.setConteudo(conteudo);
                    ResultadoOperacao<Mensagem> resultado = mensageiroFacade.enviarMensagemNoGrupo(mensagem, usuarioLogado.getId(), grupo.getId());

                    if (resultado.isSucesso())
                        printSucesso(resultado.getMensagem());
                    else
                        printErro(resultado.getMensagem());
                }
                else
                    printErro("Grupo Nao Encontrado");
            }
            else
                printAviso("Usuario Nao Logado");
        }
        catch (Exception e) {
            printErro("Erro ao Enviar Mensagem No Grupo");
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
                        ResultadoOperacao<List<VotoSolicitacao>> resultado = mensageiroFacade.solicitarEntradaGrupo(nomeGrupo, usuarioLogado.getId());
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

    private void listaConversasGrupos(){
        ResultadoOperacao<List<UsuarioGrupo>> resultado = mensageiroFacade.getConversasGrupos(usuarioLogado.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty()) {
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo("Grupo: " + resultado.getDados().get(i).getGrupo().getNome());
            }
            printSucesso(resultado.getMensagem());
        } else
            printErro(resultado.getMensagem());
    }

    private void listConvites() {
        ResultadoOperacao<List<ConviteGrupo>> resultado = mensageiroFacade.getAllConvitesByConvidado(usuarioLogado.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty()) {
            for (int i = 0; i < resultado.getDados().size(); i++) {
                printInfo("Grupo: " + resultado.getDados().get(i).getGrupo().getNome() + " [" + resultado.getDados().get(i).getStatus() + "]");
            }
            printSucesso(resultado.getMensagem());
        } else
            printErro(resultado.getMensagem());
    }

    private void listarMensagensGrupoId(Grupo grupo)
    {
        ResultadoOperacao<List<Mensagem>> resultado = mensageiroFacade.getMensagensGrupo(grupo.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty())
        {
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
            printInfo("Grupo: "+grupo.getNome()+" ===================================");
            for (int i = 0; i < resultado.getDados().size(); i++)
            {
                Mensagem mensagem = resultado.getDados().get(i);
                String horarioEnvio = mensagem.getDataHoraEnvio().format(formatador);
                printInfo("[" + horarioEnvio + "] " + "@" + mensagem.getRemetente().getApelido() + ": " + mensagem.getConteudo());
            }
            printSucesso(resultado.getMensagem());
        }
        else
            printSucesso(resultado.getMensagem());
    }

    private void listarMensagensConversaParticular(List<Mensagem> mensagens, Usuario user)
    {
        if(mensagens != null && !mensagens.isEmpty())
        {
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
            printInfo(user.getNome()+": ===================================");
            for (Mensagem mensagem : mensagens) {
                String horarioEnvio = mensagem.getDataHoraEnvio().format(formatador);
                printInfo("[" + horarioEnvio + "] " + "@" + mensagem.getRemetente().getApelido() + ": " + mensagem.getConteudo());
            }
        }
    }

    private void listaSolicitGrupos(){
        ResultadoOperacao<List<SolicitacaoEntradaGrupo>> resultado = mensageiroFacade.getAllEntradaGrupoUserIn(usuarioLogado.getId());
        if (resultado.getDados() != null && !resultado.getDados().isEmpty())
        {
            for (int i = 0; i < resultado.getDados().size(); i++)
            {
                Grupo g = resultado.getDados().get(i).getGrupo();
                Usuario u = resultado.getDados().get(i).getSolicitante();
                printInfo("Solicitacao: "+u.getApelido()+" entrar no grupo "+g.getNome()+" ["+resultado.getDados().get(i).getStatus()+"]");
            }
            printSucesso(resultado.getMensagem());
        }
        else
            printErro(resultado.getMensagem());
    }

    private void recuperarMensagensPendentes() {
        List<DestinatarioMensagem> pendentes = mensageiroFacade.getMensagensDestinariosPendenteAndConfirmadasByUser(usuarioLogado.getId(), "Pendente");
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

    private void recuperarConvitesGruposPendentes()
    {
        ResultadoOperacao<List<ConviteGrupo>> resultado = mensageiroFacade.getAllConvitesGruposByIdPendentes(usuarioLogado.getId(), "Pendente");
        List<ConviteGrupo> pendentes = resultado.getDados();
        if (pendentes != null && pendentes.size() > 0)
        {
            printAviso("Voce Tem " + pendentes.size() + " Convite(s) de Grupo(s) Pendente(s)");
            printInfo("===================================");
            for (int i = 0; i < pendentes.size(); i++)
            {
                ConviteGrupo conviteGrupo = pendentes.get(i);
                printInfo("@" + conviteGrupo.getSolicitante().getApelido() + " Enviou um Convite de Grupo"+" ("+conviteGrupo.getGrupo().getNome()+")");
            }
            printInfo("===================================");
        }

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

    private void mandarMensagemTempoReal(Usuario destinatario, String mensagem, DestinatarioMensagem dm)
    {
        if (destinatario != null)
        {
            // vou enviar a mensagem apenas para o destinatário correto -> EM TEMPO REAL

            // varrer lista de clientesConectados
            for (int i = 0; i < clientesConectados.size(); i++)
            {
                ClientHandler clientHandler = clientesConectados.get(i);
                if (clientHandler.usuarioLogado != null && clientHandler.usuarioLogado.getId().equals(destinatario.getId()))
                {
                    // achei o cliente correto para o envio da mensagem

                    if (clientHandler.usuarioLogado.getStatus().equals("online"))
                    {
                        // aparecer a mensagem apenas se ele estiver no mesmo chat junto de mim
                        if(clientHandler.conversaAtual != null && clientHandler.conversaAtual.getId() == usuarioLogado.getId()) {
                            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
                            String horarioAtual = LocalDateTime.now().format(formatador);
                            String mensagemNova = "[" + horarioAtual + "]" + " @" + this.usuarioLogado.getApelido() + ":" + mensagem;
                            clientHandler.printAviso(mensagemNova);
                            mensageiroFacade.confirmarEntregaMensagem(dm.getMensagem().getId(), clientHandler.usuarioLogado.getId());
                        }
                        else
                        {
                            // apenas enviar uma notificação -> "Usuário X te mandou mensagem"
                            clientHandler.printAviso("Usuário @" +usuarioLogado.getApelido()+ " te mandou uma mensagem!");
                        }
                    }
                }
            }
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
            String horarioAtual = LocalDateTime.now().format(formatador);
            String mensagemNova = "[" + horarioAtual + "]" + " @" + this.usuarioLogado.getApelido() + ":" + mensagem;
            printAviso(mensagemNova);
        }
    }

    private void mandarMensagemTempoRealGrupo(Grupo grupo, String mensagem)
    {
        if (grupo != null) {
            // vou enviar a mensagem apenas para o destinatário correto -> EM TEMPO REAL

            // varrer lista de clientesConectados
            for (int i = 0; i < clientesConectados.size(); i++)
            {
                ClientHandler clientHandler = clientesConectados.get(i);
                if (clientHandler.usuarioLogado != null && isInGrupo(clientHandler.usuarioLogado, grupo))
                {
                    // achei o cliente correto para o envio da mensagem

                    if (clientHandler.usuarioLogado.getStatus().equals("online"))
                    {
                        // aparecer a mensagem apenas se ele estiver no mesmo chat junto de mim
                        if(clientHandler.conversaGrupoAtual != null && clientHandler.conversaGrupoAtual.getId() == conversaGrupoAtual.getId()) {
                            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
                            String horarioAtual = LocalDateTime.now().format(formatador);
                            String mensagemNova = "[" + horarioAtual + "]" + " @" + this.usuarioLogado.getApelido() + ":" + mensagem;
                            clientHandler.printAviso(mensagemNova);
                        }
                        else
                        {
                            // apenas enviar uma notificação -> "Tem mensagem nova em &churrasco"
                            clientHandler.printAviso("Tem mensagem nova em &" +grupo.getNome());
                        }
                    }
                }
            }
        }
    }

    private void mandarMensagemTempoRealGrupoSeletivo(Grupo grupo, String mensagem, List<Long> usersId) {
        if (grupo != null && usersId != null && !usersId.isEmpty())
        {
            DateTimeFormatter formatador = DateTimeFormatter.ofPattern("HH:mm");
            String horarioAtual = LocalDateTime.now().format(formatador);
            String mensagemNova = "[" + horarioAtual + "] @" + this.usuarioLogado.getApelido() + ": " + mensagem;

            for (int i = 0; i < clientesConectados.size(); i++)
            {
                ClientHandler clientHandler = clientesConectados.get(i);

                if (clientHandler != null && clientHandler.usuarioLogado != null)
                {
                    Long idUsuarioLogado = clientHandler.usuarioLogado.getId();

                    // só envia para quem foi selecionado e está dentro do grupo
                    if (usersId.contains(idUsuarioLogado) && isInGrupo(clientHandler.usuarioLogado, grupo))
                    {
                        if (clientHandler.usuarioLogado.getStatus().equalsIgnoreCase("online"))
                        {
                            if (clientHandler.conversaGrupoAtual != null && clientHandler.conversaGrupoAtual.getId() == conversaGrupoAtual.getId())
                                clientHandler.printInfo(mensagemNova);
                            else
                                clientHandler.printAviso("Tem mensagem nova em &" + grupo.getNome());
                        }
                    }
                }
            }
        }
    }

    private void avisarUsuarioAdmitidoTempoReal(Usuario usuario, Grupo grupo)
    {
        if (usuario != null && usuario.getId() > 0)
        {
            String mensagemNova = "Voce foi ADMITIDO ao grupo: "+grupo.getNome();

            for (int i = 0; i < clientesConectados.size(); i++)
            {
                ClientHandler clientHandler = clientesConectados.get(i);

                if (clientHandler != null && clientHandler.usuarioLogado != null)
                {
                    Long idUsuarioLogado = clientHandler.usuarioLogado.getId();

                    if (
                            idUsuarioLogado != null &&
                            usuario.getId() == idUsuarioLogado &&
                            clientHandler.usuarioLogado.getStatus().equalsIgnoreCase("online")
                    )
                    {
                        clientHandler.printInfo(mensagemNova);
                    }
                }
            }
        }
    }

    private void avisarUsuarioNegadoTempoRal(Usuario usuario, Grupo grupo)
    {
        if (usuario != null && usuario.getId() > 0)
        {
            String mensagemNova = "Voce foi NEGADO de entrar no grupo: "+grupo.getNome();

            for (int i = 0; i < clientesConectados.size(); i++)
            {
                ClientHandler clientHandler = clientesConectados.get(i);

                if (clientHandler != null && clientHandler.usuarioLogado != null)
                {
                    Long idUsuarioLogado = clientHandler.usuarioLogado.getId();

                    if (
                            idUsuarioLogado != null &&
                                    usuario.getId() == idUsuarioLogado &&
                                    clientHandler.usuarioLogado.getStatus().equalsIgnoreCase("online")
                    )
                    {
                        clientHandler.printInfo(mensagemNova);
                    }
                }
            }
        }
    }

    private boolean isInGrupo(Usuario usuario, Grupo grupo)
    {
        UsuarioGrupo ug = usuarioGrupoService.getByUsuarioGrupo(grupo.getId(), usuario.getId());
        return ug != null;
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
        List<ConviteGrupo> conviteGrupos = retorno.getDados();
        for (int i = 0; i < conviteGrupos.size(); i++)
        {
            ConviteGrupo conviteGrupo = conviteGrupos.get(i);
            for (int j = 0; j < clientesConectados.size(); j++)
            {
                ClientHandler clientHandler = clientesConectados.get(j);
                if (clientHandler != null && clientHandler.usuarioLogado.getId().equals(conviteGrupo.getConvidado().getId()))
                {
                    clientHandler.convitesGruposPendetes.add(conviteGrupo);
                    clientHandler.printAviso("[SISTEMA]: O Usuario @" + this.usuarioLogado.getApelido() +
                            " Enviou um Convite Para o Grupo (" + nomeGrupo + "). Voce Aceita ? (simgrupo/naogrupo)");
                }
            }
        }
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
                recuperarConvitesGruposPendentes();
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

    private void responderConvite(Grupo grupo, String resposta)
    {
        Integer valor;
        if(resposta.equalsIgnoreCase("sim"))
        {
            resposta = "Confirmado";
            valor = 1;
        }
        else
        {
            resposta = "Recusado";
            valor = 0;
        }

        ResultadoOperacao<ConviteGrupo> resultado = mensageiroFacade.alterarConviteGrupo(grupo.getId(), usuarioLogado.getId(), valor);
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
