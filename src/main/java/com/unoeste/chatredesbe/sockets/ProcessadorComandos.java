//package com.unoeste.chatredesbe.sockets;
//
//import com.unoeste.chatredesbe.entities.SolicitacaoMensagem;
//import com.unoeste.chatredesbe.entities.Usuario;
//import com.unoeste.chatredesbe.services.*;
//import org.springframework.stereotype.Component;
//
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Component
//public class ProcessadorComandos {
//
//    private final UsuarioService usuarioService;
//    private final GerenciadorSessoes gerenciadorSessoes;
//
//    // Injetando os novos services de acordo com as suas tabelas
//    private final GrupoService grupoService;
//    private final SolicitacaoMensagemService solicitacaoMensagemService;
//    private final ConviteGrupoService conviteGrupoService;
//    private final SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;
//    private final MensagemService mensagemService;
//    //private final DestinarioMensagemService destinarioMensagemService;
//
//    // Pattern para detectar envio de msg num grupo ex: &nomegrupo: msg ou &grupo@usuario1: msg
//    private static final Pattern PATTERN_MSG_GRUPO = Pattern.compile("^&([^@:]+)(?:@([^:]+))?:\\s*(.*)$");
//    // Pattern para mensagem privada direta ex: @robson: como vai?
//    private static final Pattern PATTERN_MSG_PRIVADA = Pattern.compile("^@([^:]+):\\s*(.*)$");
//
//    public ProcessadorComandos(
//            UsuarioService usuarioService,
//            GerenciadorSessoes gerenciadorSessoes,
//            GrupoService grupoService,
//            SolicitacaoMensagemService solicitacaoMensagemService,
//            ConviteGrupoService conviteGrupoService,
//            SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService,
//            MensagemService mensagemService/*,
//            DestinarioMensagemService destinarioMensagemService*/) {
//        this.usuarioService = usuarioService;
//        this.gerenciadorSessoes = gerenciadorSessoes;
//        this.grupoService = grupoService;
//        this.solicitacaoMensagemService = solicitacaoMensagemService;
//        this.conviteGrupoService = conviteGrupoService;
//        this.solicitacaoEntradaGrupoService = solicitacaoEntradaGrupoService;
//        this.mensagemService = mensagemService;
//        //this.destinarioMensagemService = destinarioMensagemService;
//    }
//
//    public String processar(String comando, TratadorCliente tratadorCliente) {
//        try {
//            // Comandos básicos
//            if (comando.startsWith("login "))
//                return processarLogin(comando, tratadorCliente);
//            if (comando.equals("logout"))
//                return processarLogout(tratadorCliente);
//            if (comando.equals("listausuarios"))
//                return processarListaUsuarios();
//
//            // Novos comandos com base no enunciado
//            if (comando.startsWith("cadastrar "))
//                return processarCadastro(comando);
//            if (comando.startsWith("recuperarsenha "))
//                return processarRecuperacaoSenha(comando);
//            if (comando.startsWith("status "))
//                return processarStatus(comando, tratadorCliente);
//
//            if (comando.equals("listagrupos"))
//                return processarListaGrupos();
//            if (comando.startsWith("novogrupo "))
//                return processarNovoGrupo(comando, tratadorCliente);
//            if (comando.startsWith("inserir &"))
//                return processarInserirNoGrupo(comando, tratadorCliente);
//            if (comando.startsWith("entrar &"))
//                return processarSolicitarEntradaGrupo(comando, tratadorCliente);
//            if (comando.equals("sair"))
//                return processarSairGrupo(tratadorCliente);
//
//            if (comando.startsWith("responderprivado "))
//                return processarRespostaPrivado(comando, tratadorCliente);
//
//            // Tenta processar Regex para envio de mensagem em grupo/subgrupo (ex: &nomegrupo: msg ou &nomegrupo@usuario1: msg)
//            Matcher matcherGrupo = PATTERN_MSG_GRUPO.matcher(comando);
//            if (matcherGrupo.matches()) {
//                return processarMensagemGrupo(matcherGrupo, tratadorCliente);
//            }
//
//            // Tenta processar Regex para envio de mensagem privada (ex: @robson: como vc vai?)
//            Matcher matcherPrivado = PATTERN_MSG_PRIVADA.matcher(comando);
//            if (matcherPrivado.matches()) {
//                return processarMensagemPrivada(matcherPrivado, tratadorCliente);
//            }
//
//            return "Comando não reconhecido.";
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Erro ao processar comando: " + e.getMessage();
//        }
//    }
//
//    // --- MÉTODOS BÁSICOS JÁ FEITOS ---
//
//    private String processarLogin(String comando, TratadorCliente tratadorCliente) {
//        String[] partes = comando.split(" ");
//        if (partes.length < 3)
//            return "Uso: login <apelido> <senha>";
//        String apelido = partes[1];
//        String senha = partes[2];
//
//        Usuario usuario = usuarioService.logar(apelido, senha);
//        if (usuario == null)
//            return "Login ou senha inválidos.";
//
//        tratadorCliente.setApelidoUsuario(apelido);
//        gerenciadorSessoes.adicionar(apelido, tratadorCliente);
//
//        // Pelo enunciado: verificar as msgs não lidas ao fazer login
//        entregarMensagensPendentes(usuario, tratadorCliente);
//
//        return "Login realizado com sucesso. Bem-vindo, " + usuario.getNome();
//    }
//
//    private String processarLogout(TratadorCliente tratadorCliente) {
//        String apelido = tratadorCliente.getApelidoUsuario();
//        if (apelido != null)
//            gerenciadorSessoes.remover(apelido);
//        tratadorCliente.setApelidoUsuario(null);
//        return "Logout realizado.";
//    }
//
//    private String processarListaUsuarios() {
//        return "Usuários online: " + String.join(", ", gerenciadorSessoes.listarOnline());
//    }
//
//    // --- NOVOS MÉTODOS DE USUÁRIO E STATUS ---
//
//    private String processarCadastro(String comando) {
//        // Ex: cadastrar Joao Silva;joao123;joao@email.com;senha123
//        String dados = comando.replace("cadastrar ", "").trim();
//        String[] partes = dados.split(";");
//        if (partes.length < 4)
//            return "Uso: cadastrar <nomeCompleto>;<apelido>;<email>;<senha>";
//
//        try {
//            Usuario u = new Usuario();
//            u.setNome(partes[0].trim());
//            u.setApelido(partes[1].trim());
//            u.setEmail(partes[2].trim());
//            u.setSenha(partes[3].trim());
//            u.setStatus("offline");
//            usuarioService.salvar(u);
//            return "Usuário " + u.getApelido() + " cadastrado com sucesso!";
//        } catch (Exception e) {
//            return "Erro no cadastro. Talvez o apelido ou email já existam.";
//        }
//    }
//
//    private String processarRecuperacaoSenha(String comando) {
//        String email = comando.replace("recuperarsenha ", "").trim();
//        Usuario u = usuarioService.getByEmail(email);
//        if (u == null)
//            return "Email não encontrado.";
//        return "A senha vinculada a este email é: " + u.getSenha();
//    }
//
//    private String processarStatus(String comando, TratadorCliente tratadorCliente) {
//        String apelido = tratadorCliente.getApelidoUsuario();
//        if (apelido == null)
//            return "Faça login primeiro.";
//
//        String status = comando.replace("status ", "").trim().toLowerCase();
//        if (!status.equals("online") && !status.equals("offline") && !status.equals("ocupado")) {
//            return "Status inválido. Use online, offline ou ocupado.";
//        }
//
//        Usuario u = usuarioService.getByApelido(apelido);
//        u.setStatus(status);
//        usuarioService.salvar(u);
//
//        return "Seu status foi alterado para: " + status;
//    }
//
//    // --- MÉTODOS DE CHAT PRIVADO ---
//
//    /*private String processarMensagemPrivada(Matcher matcher, TratadorCliente tratadorCliente) {
//        String remetenteApelido = tratadorCliente.getApelidoUsuario();
//        if (remetenteApelido == null)
//            return "Faça login primeiro.";
//
//        // Regex captura o destinatario no grupo 1 e o conteudo no grupo 2
//        String destinatarioApelido = matcher.group(1).trim();
//        String textoMsg = matcher.group(2).trim();
//
//        Usuario remetente = usuarioService.getByApelido(remetenteApelido);
//        Usuario destinatario = usuarioService.getByApelido(destinatarioApelido);
//
//        if (destinatario == null)
//            return "Usuário destinatário não existe.";
//
//        // 1. Validar na tabela SolicitacaoMensagem se o destinatario já autorizou msg deste remetente
//        // No banco vc tem: SolicitacaoMensagem(usuario1_id, usuario2_id, status)
//        boolean autorizado = solicitacaoMensagemService.verificarSeAutorizado(remetente.getId(), destinatario.getId());
//
//        if (!autorizado) {
//            // Cria a solicitação pendente no banco
//            solicitacaoMensagemService.salvar(remetente.getId(), destinatario.getId(), "PENDENTE");
//
//            TratadorCliente tratDestino = gerenciadorSessoes.get(destinatarioApelido);
//            if (tratDestino != null) {
//                tratDestino.enviarMensagem("O usuário " + remetenteApelido + " quer enviar uma msg particular para vc, vc aceita: sim ou nao? Responda com 'responderprivado sim' ou 'responderprivado nao'");
//                return "Solicitação enviada. Aguardando aceite de " + destinatarioApelido;
//            } else {
//                return "Solicitação salva. O destinatário está offline e será notificado quando entrar.";
//            }
//        }
//
//        // Se estiver autorizado, salva a Mensagem e tabela DestinarioMensagem (como PENDENTE)
//        Long msgId = mensagemService.salvarMensagemPrivada(remetente.getId(), textoMsg);
//        destinarioMensagemService.salvarDestinatario(msgId, destinatario.getId());
//
//        // Tenta enviar se estiver online e não estiver ocupado
//        TratadorCliente tratDestino = gerenciadorSessoes.get(destinatarioApelido);
//        if (tratDestino != null && destinatario.getStatus().equals("online")) {
//            tratDestino.enviarMensagem(remetenteApelido + " enviou: " + textoMsg);
//            destinarioMensagemService.marcarComoEntregue(msgId, destinatario.getId());
//            return "Mensagem enviada com sucesso.";
//        } else {
//            return "Usuário offline ou ocupado. A mensagem será entregue depois.";
//        }
//    }*/
//
//    private String processarRespostaPrivado(String comando, TratadorCliente tratadorCliente) {
//        String apelido = tratadorCliente.getApelidoUsuario();
//        if (apelido == null) return "Faça login primeiro.";
//
//        String resposta = comando.replace("responderprivado ", "").trim().toLowerCase();
//        Usuario usuario = usuarioService.getByApelido(apelido);
//
//        // Busca no banco quem tem solicitacao pendente com ele
//        // Atualiza a tabela solicitacao_mensagem para ACEITA ou RECUSADA
//        boolean sucesso = solicitacaoMensagemService.responderSolicitacaoPendente(usuario.getId(), resposta.equals("sim") ? "ACEITA" : "RECUSADA");
//
//        if (sucesso && resposta.equals("sim")) {
//            return "Você aceitou receber mensagens. Agora o usuário pode te escrever.";
//        } else if (sucesso && resposta.equals("nao")) {
//            return "Você recusou a solicitação de conversa.";
//        }
//        return "Nenhuma solicitação pendente encontrada.";
//    }
//
//    // --- FUNÇÃO DE ENTREGA OFFLINE ---
//
//    private void entregarMensagensPendentes(Usuario usuario, TratadorCliente tratadorCliente) {
//        // Busca msgs do destinatario com status PENDENTE
//        // var pendentes = destinarioMensagemService.buscarPendentes(usuario.getId());
//        // loop {
//        //   tratadorCliente.enviarMensagem(remetente + " enviou enquanto vc estava fora: " + msg.getConteudo());
//        //   destinarioMensagemService.marcarComoEntregue(...)
//        // }
//    }
//
//    // (Abaixo você criaria os stubs pros métodos de Grupo, seguindo essa lógica com Banco de Dados)
//    private String processarNovoGrupo(String comando, TratadorCliente tratadorCliente) { return "Em construção"; }
//    private String processarListaGrupos() { return "Em construção"; }
//    private String processarInserirNoGrupo(String comando, TratadorCliente tratadorCliente) { return "Em construção"; }
//    private String processarSolicitarEntradaGrupo(String comando, TratadorCliente tratadorCliente) { return "Em construção"; }
//    private String processarMensagemGrupo(Matcher matcher, TratadorCliente tratadorCliente) { return "Em construção"; }
//    private String processarSairGrupo(TratadorCliente tratadorCliente) { return "Em construção"; }
//
//}