package com.unoeste.chatredesbe.sockets;

import com.unoeste.chatredesbe.entities.Usuario;
import com.unoeste.chatredesbe.services.GrupoService;
import com.unoeste.chatredesbe.services.MensagemService;
import com.unoeste.chatredesbe.services.UsuarioService;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProcessadorComandos {

    private final UsuarioService usuarioService;
    private final GerenciadorSessoes gerenciadorSessoes;
    private final GrupoService grupoService;
    private final MensagemService mensagemService;
    private final PendenteService pendenteService;

    private final Map<String, String> solicitacoesPrivadasPendentes = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> conversasAutorizadas = new ConcurrentHashMap<>();

    public ProcessadorComandos(
            UsuarioService usuarioService,
            GerenciadorSessoes gerenciadorSessoes,
            GrupoService grupoService,
            MensagemService mensagemService,
            PendenteService pendenteService)
    {
        this.usuarioService = usuarioService;
        this.gerenciadorSessoes = gerenciadorSessoes;
        this.grupoService = grupoService;
        this.mensagemService = mensagemService;
        this.pendenteService = pendenteService;
    }

    public String processar(String comando, TratadorCliente tratadorCliente) {
        try {
            if (comando.startsWith("login "))
                return processarLogin(comando, tratadorCliente);
            if (comando.equals("logout"))
                return processarLogout(tratadorCliente);
            if (comando.equals("listausuarios"))
                return processarListaUsuarios();
            if (comando.equals("listagrupos"))
                return processarListaGrupos();
            if (comando.startsWith("recuperarsenha "))
                return processarRecuperacaoSenha(comando);
            if (comando.startsWith("status "))
                return processarStatus(comando, tratadorCliente);
            if (comando.startsWith("novogrupo "))
                return processarNovoGrupo(comando, tratadorCliente);
            if (comando.startsWith("inserir &"))
                return processarInserirNoGrupo(comando, tratadorCliente);
            if (comando.startsWith("entrar &"))
                return processarSolicitacaoEntradaGrupo(comando, tratadorCliente);
            if (comando.startsWith("&"))
                return processarComandoGrupo(comando, tratadorCliente);
            if (comando.startsWith("@"))
                return processarMensagemPrivada(comando, tratadorCliente);
            if (comando.startsWith("responderprivado "))
                return processarRespostaPrivado(comando, tratadorCliente);
            if (comando.startsWith("responderconvitegrupo "))
                return processarRespostaConviteGrupo(comando, tratadorCliente);
            if (comando.startsWith("responderentrada "))
                return processarRespostaEntradaGrupo(comando, tratadorCliente);

            return "Comando não reconhecido.";
        } catch (Exception e) {
            return "Erro ao processar comando: " + e.getMessage();
        }
    }


    private String processarMensagemPrivada(String comando, TratadorCliente tratadorCliente) {
        String remetente = tratadorCliente.getApelidoUsuario();
        if (remetente == null) return "Faça login primeiro.";

        int idx = comando.indexOf(":");
        if (idx < 0) return "Uso: @usuario: mensagem";

        String destino = comando.substring(1, idx).trim();
        String texto = comando.substring(idx + 1).trim();

        Usuario usuarioDestino = usuarioService.getByApelido(destino);
        if (usuarioDestino == null) return "Usuário destino não encontrado.";

        boolean autorizado = conversaAutorizada(remetente, destino);

        if (!autorizado) {
            solicitacoesPrivadasPendentes.put(destino, remetente);

            TratadorCliente tratadorDestino = gerenciadorSessoes.get(destino);
            if (tratadorDestino != null) {
                tratadorDestino.enviarMensagem("O usuário " + remetente + " quer conversar em privado com você. Responda: responderprivado sim ou responderprivado nao");
                return "Solicitação de conversa privada enviada para " + destino + ".";
            }

            pendenteService.adicionarAviso(destino, "O usuário " + remetente + " quer conversar em privado com você. Responda: responderprivado sim ou responderprivado nao");
            return "Usuário offline. Solicitação será exibida quando ele ficar online.";
        }

        entregarOuArmazenarPrivado(remetente, destino, texto);
        return "Mensagem enviada para " + destino + ".";
    }

    private String processarRecuperacaoSenha(String comando) {
        String email = comando.replace("recuperarsenha", "").trim();
        Usuario usuario = usuarioService.getByEmail(email);

        if (usuario == null) return "Email não encontrado.";

        return "Senha do usuário " + usuario.getApelido() + ": " + usuario.getSenha();
    }

    private String processarStatus(String comando, TratadorCliente tratadorCliente) {
        String apelido = tratadorCliente.getApelidoUsuario();
        if (apelido == null)
            return "Faça login primeiro.";

        String novoStatus = comando.replace("status", "").trim().toLowerCase();

        if (!novoStatus.equals("online") && !novoStatus.equals("offline") && !novoStatus.equals("ocupado")) {
            return "Status inválido. Use: online, offline ou ocupado.";
        }

        Usuario usuario = usuarioService.getByApelido(apelido);
        if (usuario == null)
            return "Usuário não encontrado.";

        usuario.setStatus(novoStatus);
        usuarioService.salvar(usuario);

        return "Status alterado para " + novoStatus + ".";
    }

    private String processarRespostaPrivado(String comando, TratadorCliente tratadorCliente) {
        String usuario = tratadorCliente.getApelidoUsuario();
        if (usuario == null)
            return "Faça login primeiro.";

        String resposta = comando.replace("responderprivado", "").trim().toLowerCase();
        String solicitante = solicitacoesPrivadasPendentes.get(usuario);

        if (solicitante == null)
            return "Não há solicitação privada pendente.";

        if (resposta.equals("sim")) {
            autorizarConversa(solicitante, usuario);
            solicitacoesPrivadasPendentes.remove(usuario);

            TratadorCliente origem = gerenciadorSessoes.get(solicitante);
            if (origem != null) {
                origem.enviarMensagem(usuario + " aceitou sua conversa privada.");
            }

            return "Conversa privada aceita.";
        }

        if (resposta.equals("nao")) {
            solicitacoesPrivadasPendentes.remove(usuario);

            TratadorCliente origem = gerenciadorSessoes.get(solicitante);
            if (origem != null) {
                origem.enviarMensagem(usuario + " recusou sua conversa privada.");
            }

            return "Conversa privada recusada.";
        }

        return "Resposta inválida. Use: responderprivado sim ou responderprivado nao";
    }
}