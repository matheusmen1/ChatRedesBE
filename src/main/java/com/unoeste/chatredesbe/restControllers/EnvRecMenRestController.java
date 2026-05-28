package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Essa classe trata exclusivamente do controle de envio e recebimento de mensagens,
 *  deixando as outras RestController's apenas com responsabilidades básicas, como:
 *      Inserir,
 *      Remover,
 *      Editar.
 * Com isso essa classe irá comtemplar boa parte da complexidade de todo o sistema
 * */
@RestController
@RequestMapping("apis/mensagem")
@CrossOrigin
public class EnvRecMenRestController {
    // EnvRecMenRestController -> ENVIAR E RECEBER MENSAGENS RESTCONTROLLER
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    MensagemService mensagemService;
    @Autowired
    DestinatarioMensagemService destinatarioMensagemService;
    @Autowired
    GrupoService grupoService;
    @Autowired
    SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;
    @Autowired
    UsuarioGrupoService usuarioGrupoService;

    /**
     * Trata o envio de uma mensagem de um remetente até um destinatário
     * */
    @PostMapping
    public ResponseEntity<Object> enviarMensagem(@PathVariable Long destinatario, @RequestBody Mensagem mensagem)
    {
        Usuario origem = usuarioService.getById(mensagem.getRemetente().getId());
        Usuario destino = usuarioService.getById(destinatario);

        if(origem == null || destino == null)
        {
            if(origem == null)
                return ResponseEntity.badRequest().body(new Erro("Usuário de Origem não existe!!"));
            return ResponseEntity.badRequest().body(new Erro("Usuário de Destino não existe!!"));
        }
        else
        {
            // posso enviar a mensagem
            Mensagem mensagemSalva = mensagemService.salvar(mensagem);
            destinatarioMensagemService(new DestinatarioMensagem(
                    destino, mensagemSalva, "Ativa",
                    ));
        }
    }

    /**
     * Trata o envio de uma mensagem para um determinado Grupo
     * */
    @PostMapping
    public ResponseEntity<Object> enviarMensagemNoGrupo(@RequestBody Mensagem mensagem)
    {
    }

    /**
     * Trata a solicitação de um pedido de entrada em determinado Grupo
     * */
    @PostMapping
    public ResponseEntity<Object> solicitarEntradaGrupo(@RequestBody SolicitacaoEntradaGrupo solicitacaoEntradaGrupo)
    {
    }

    /**
     * Trata a entrada de um Usuário em determinado Grupo.
     *      Aqui, deve-se ter certeza de que todos os Usuários do Grupo já aceitaram o mesmo a entrar
     * */
    @PostMapping
    public ResponseEntity<Object> entrarNoGrupo(@RequestBody UsuarioGrupo usuarioGrupo)
    {
    }
}
