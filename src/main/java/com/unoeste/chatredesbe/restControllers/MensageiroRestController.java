package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.Padrao.MensageiroFacade;
import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apis/mensageiro")
@CrossOrigin
public class MensageiroRestController {
    // MensageiroRestController -> ENVIAR E RECEBER MENSAGENS RESTCONTROLLER

    // AUTOWIRED'S
    @Autowired ConviteGrupoService conviteGrupoService;
    @Autowired DestinatarioMensagemService destinatarioMensagemService;
    @Autowired GrupoService grupoService;
    @Autowired MensagemService mensagemService;
    @Autowired SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;
    @Autowired SolicitacaoMensagemService solicitacaoMensagemService;
    @Autowired UsuarioGrupoService usuarioGrupoService;
    @Autowired UsuarioService usuarioService;
    @Autowired VotoSolicitacaoService votoSolicitacaoService;
    @Autowired MensageiroFacade mensageiroFacade;

    // ===================================================================================================================
    // GET's
    // ===================================================================================================================
    /**
     * Método que retorna a lista de todas as {@link Mensagem} de um {@link Usuario} com outro {@link Usuario}
     * */
    @GetMapping("/getMensagensConversa/{idOrigem}/{idDestino}")
    public ResponseEntity<Object> getMensagensConversa(@PathVariable Long idOrigem, @PathVariable Long idDestino)
    {
        ResultadoOperacao<List<Mensagem>> resultado = mensageiroFacade.getMensagensConversa(idOrigem, idDestino);

        if (resultado.isSucesso())
            return ResponseEntity.ok(resultado.getDados());

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método Para Listar Todos os Grupos Cadastrados no Banco de Dados
     * */
    @GetMapping("/listarGrupos")
    public ResponseEntity<Object> getAllGrupos()
    {
        ResultadoOperacao<List<Grupo>> resultado = mensageiroFacade.getAllGrupos();

        if (resultado.isSucesso())
            return ResponseEntity.ok().body(resultado.getDados());

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método Para Listar Todos Os Grupos que um Usuário Pertence
     * */
    @GetMapping("/listarGruposUsuario/{usuarioId}")
    public ResponseEntity<Object> getAllGruposUsuario(@PathVariable Long idUsuario)
    {
        ResultadoOperacao<List<Grupo>> resultado = mensageiroFacade.getAllGruposUsuario(idUsuario);

        if (resultado.isSucesso())
            return ResponseEntity.ok().body(resultado.getDados());

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método Para Listar Todos os Usuários com o Status Online
     * */
    @GetMapping("/listarUsuariosOnline")
    public ResponseEntity<Object> getAllUsersOnline()
    {
        ResultadoOperacao<List<Usuario>> resultado = mensageiroFacade.getAllUsersOnline();

        if (resultado.isSucesso())
            return ResponseEntity.ok(resultado.getDados());

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método para retornar todas as Mensagens de um Grupo
     * */
    @GetMapping("/getMensagensGrupo/{idGrupo}")
    public ResponseEntity<Object> getMensagensGrupo(@PathVariable Long idGrupo)
    {
        ResultadoOperacao<List<Mensagem>> resultado = mensageiroFacade.getMensagensGrupo(idGrupo);

        if (resultado.isSucesso())
            return ResponseEntity.ok(resultado.getDados());

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    // ===================================================================================================================
    // POST's
    // ===================================================================================================================
    /**
     * Trata o envio de uma Mensagem de um Usuário REMETENTE até um Usuário DESTINATÁRIO
     * */
    @PostMapping("/enviarMensagemPessoa/{destinatario}")
    public ResponseEntity<Object> enviarMensagem(@PathVariable Long destinatario, @RequestBody Mensagem mensagem)
    {
        ResultadoOperacao<DestinatarioMensagem> resultado =
                mensageiroFacade.enviarMensagemPessoa(mensagem.getRemetente().getId(), destinatario, mensagem.getConteudo());

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }
        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Endpoint para a criação de um novo Grupo
     * */
    @PostMapping("/criarNovoGrupo/{nomeGrupo}/{idCriador}")
    public ResponseEntity<Object> criarNovoGrupo(
            @PathVariable String nomeGrupo,
            @PathVariable Long idCriador,
            @RequestBody List<Usuario> usuarios)
    {
        ResultadoOperacao<Grupo> resultado = mensageiroFacade.criarNovoGrupo(nomeGrupo, idCriador, usuarios);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método para a adição (convite) de novos Usuários em um Grupo
     * */
    @PostMapping("/addUsersGrupo/{nomeGrupo}/{idCriador}")
    public ResponseEntity<Object> addUsersGrupo(
            @PathVariable String nomeGrupo,
            @PathVariable Long idCriador,
            @RequestBody List<Usuario> usuarios)
    {
        ResultadoOperacao<List<ConviteGrupo>> resultado = mensageiroFacade.addUsersGrupo(nomeGrupo, idCriador, usuarios);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Trata o envio de uma mensagem para um determinado Grupo
     * */
    @PostMapping("/enviarMensagemGrupo/{idRemetente}/{idGrupo}")
    public ResponseEntity<Object> enviarMensagemNoGrupo(
            @RequestBody Mensagem mensagem,
            @PathVariable Long idRemetente,
            @PathVariable Long idGrupo)
    {
        ResultadoOperacao<Mensagem> resultado = mensageiroFacade.enviarMensagemNoGrupo(mensagem, idRemetente, idGrupo);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método para enviar seletivamente mensagem para Usuários de um Grupo.
     * Exemplo de objeto JSON:
     * {
     *      "conteudo": "Olá, pessoal!",
     *      "remetenteId": 1,
     *      "usuariosIds": [2, 3, 5]
     * }
     */
    @PostMapping("/enviarMensagemSeletivaGrupo/{idGrupo}")
    public ResponseEntity<Object> enviarMensagemSeletivaGrupo(
            @PathVariable Long idGrupo,
            @RequestBody EnviarMensagemSeletivaGrupoDTO dto)
    {
        ResultadoOperacao<String> resultado = mensageiroFacade.enviarMensagemSeletivaGrupo(idGrupo, dto);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok().body(new Erro(resultado.getMensagem()));
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Trata a solicitação de um pedido de entrada em determinado Grupo
     * */
    @PostMapping("solicitarEntradaGrupo/{nomeGrupo}/{idSolicitante}")
    public ResponseEntity<Object> solicitarEntradaGrupo(@PathVariable String nomeGrupo, @PathVariable Long idSolicitante)
    {
        ResultadoOperacao<List<VotoSolicitacao>> resultado = mensageiroFacade.solicitarEntradaGrupo(nomeGrupo, idSolicitante);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    // ===================================================================================================================
    // PUT's
    // ===================================================================================================================

    /**
     * Confirmar UMA solicitação de envio de mensagem
     * */
    @PutMapping("/confirmarSolicitacao/{idRemetente}/{idDestinatario}")
    public ResponseEntity<Object> confirmarSolicitacao(@PathVariable Long idRemetente, @PathVariable Long idDestinatario)
    {
        ResultadoOperacao<SolicitacaoMensagem> resultado = mensageiroFacade.confirmarSolicitacao(idRemetente, idDestinatario, "Confirmada");

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método para ALTERAR o convite de entrada em um Grupo.
     *      Aceitar (1),
     *      Rejeitar (0)
     * */
    @PutMapping("/alterarConviteGrupo/{idGrupo}/{idConvidado}/{voto}")
    public ResponseEntity<Object> alterarConviteGrupo(@PathVariable Long idGrupo, @PathVariable Long idConvidado, @PathVariable Integer voto)
    {
        ResultadoOperacao<Object> resultado = mensageiroFacade.alterarConviteGrupo(idGrupo, idConvidado, voto);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    /**
     * Método para votar na entrada de um usuário no Grupo
     * */
    @PutMapping("/votarEntradaGrupo/{idVotante}/{idSolicitacao}/{voto}")
    public ResponseEntity<Object> votarSolicitacaoEntradaGrupo(@PathVariable Long idVotante, @PathVariable Long idSolicitacao, @PathVariable Integer voto)
    {
        ResultadoOperacao<Object> resultado = mensageiroFacade.votarSolicitacaoEntradaGrupo(idVotante, idSolicitacao, voto);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }

    // ===================================================================================================================
    // DELETE's
    // ===================================================================================================================
    /**
     * Método Para Sair de um Grupo
     * */
    @DeleteMapping("/sairGrupo/{idUsuario}/{idGrupo}")
    public ResponseEntity<Object> deleteUsuarioGrupo(@PathVariable Long idUsuario, @PathVariable Long idGrupo)
    {
        ResultadoOperacao<Erro> resultado = mensageiroFacade.deleteUsuarioGrupo(idUsuario, idGrupo);

        if (resultado.isSucesso()) {
            return ResponseEntity.ok(resultado.getDados());
        }

        return ResponseEntity.badRequest().body(new Erro(resultado.getMensagem()));
    }
}
