package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Essa classe trata exclusivamente do controle de envio e recebimento de mensagens,
 *  deixando as outras RestController's apenas com responsabilidades básicas, como:
 *      Inserir,
 *      Remover,
 *      Editar.
 * Com isso essa classe irá comtemplar boa parte da complexidade de todo o sistema.
 * */
@RestController
@RequestMapping("apis/mensageiro")
@CrossOrigin
public class MensageiroRestController {
    // MensageiroRestController -> ENVIAR E RECEBER MENSAGENS RESTCONTROLLER

    // AUTOWIRED'S
    @Autowired
    ConviteGrupoService conviteGrupoService;
    @Autowired
    DestinatarioMensagemService destinatarioMensagemService;
    @Autowired
    GrupoService grupoService;
    @Autowired
    MensagemService mensagemService;
    @Autowired
    SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;
    @Autowired
    SolicitacaoMensagemService solicitacaoMensagemService;
    @Autowired
    UsuarioGrupoService usuarioGrupoService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    VotoSolicitacaoService votoSolicitacaoService;

    /**
     * Método que retorna a lista de todas as mensagens de um Usuário com outro Usuário
     * */
    @GetMapping("/getMensagensConversa/{idRemetente}/{idDestinatario}")
    public ResponseEntity<Object> getMensagensConversa(@PathVariable Long idRemetente, @PathVariable Long idDestinatario)
    {
//        List<Mensagem> mensagensEnviadas = mensagemService.getAllMensagensById(idRemetente, idDestinatario);
//        List<Mensagem> mensagensRecebidas = mensagemService.getAllMensagensById(idDestinatario, idRemetente);
//
//        List<Mensagem> todasMensagens = new ArrayList<>();
//        todasMensagens.addAll(mensagensEnviadas);
//        todasMensagens.addAll(mensagensRecebidas);
//        todasMensagens.sort(Comparator.comparing(Mensagem::getDataHoraEnvio)); //ordenar pela data e hora
//
//        if(!todasMensagens.isEmpty())
//        {
//            return ResponseEntity.ok(todasMensagens);
//        }
//        return ResponseEntity.badRequest().body(new Erro("Nenhuma mensagem encontrada!!"));
        return null;
    }

    /**
     * Método que retorna a lista de todas as mensagens que um Usuário recebeu
     * */
    @GetMapping("/getMensagensRecebidas/{idDestinatario}")
    public ResponseEntity<Object> getMensagensRecebidas(@PathVariable Long idDestinatario)
    {
        List<DestinatarioMensagem> destinatarioMensagens = destinatarioMensagemService.getByIdDestinatario(idDestinatario);

        //destinatarioMensagens.sort(Comparator.comparing(DestinatarioMensagem::getDataHoraEntrega)); //ordenar pela data e hora

        if(destinatarioMensagens.isEmpty())
            return ResponseEntity.badRequest().body(new Erro("Nenhuma mensagem encontrada!!"));
        else
        {
            // pego apenas as mensagens
            List<Mensagem> mensagensRecebidas = new ArrayList<>();
            for(int i=0; i<destinatarioMensagens.size(); i++)
                mensagensRecebidas.add(destinatarioMensagens.get(i).getMensagem());

            return ResponseEntity.ok(mensagensRecebidas);
        }
    }

    /**
     * Trata o envio de uma Mensagem de um Usuário REMETENTE até um Usuário DESTINATÁRIO
     * */
    @PostMapping("/enviarMensagemPessoa/{destinatario}")
    public ResponseEntity<Object> enviarMensagem(@PathVariable(value = "destinatario") Long destinatario, @RequestBody Mensagem mensagem)
    {
        try{
            if(!isIdValido(destinatario) || !isIdValido(mensagem.getRemetente().getId().longValue()))
                return ResponseEntity.badRequest().body(new Erro("IDs inválidos!!"));

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
                SolicitacaoMensagem solicitacaoMensagem = solicitacaoMensagemService.getByUsers(origem, destino);
                if(solicitacaoMensagem != null && solicitacaoMensagem.getStatus().equalsIgnoreCase("Confirmada"))
                {
                    // posso enviar a mensagem
                    Mensagem mensagemSalva = mensagemService.salvar(mensagem);
                    DestinatarioMensagem destinatarioMensagem = destinatarioMensagemService.salvar(new DestinatarioMensagem(
                            destino, mensagemSalva, "Pendente", mensagemSalva.getDataHoraEnvio()
                    ));
                    return ResponseEntity.ok(destinatarioMensagem);
                }
                else
                {
                    solicitacaoMensagemService.salvar(new SolicitacaoMensagem(origem, destino, "Pendente")); //solicitar novamente a mensagem
                    Mensagem mensagemSalva = mensagemService.salvar(mensagem);
                    destinatarioMensagemService.salvar(new DestinatarioMensagem(
                            destino, mensagemSalva, "Pendente", mensagemSalva.getDataHoraEnvio()
                    ));
                    if (solicitacaoMensagem == null)
                        return ResponseEntity.badRequest().body(new Erro("Enviando Solicitação de mensagem para " + destino.getApelido() + "..."));
                    else
                        return ResponseEntity.badRequest().body(new Erro("Solicitação de mensagem pendente para " + destino.getApelido() + "..."));
                }
            }
        }
        catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(new Erro("Erro interno no servidor!!"));
        }
    }

    /**
     * Confirmar UMA solicitação de envio de mensagem
     * */
    @PutMapping("/confirmarSolicitacao/{idRemetente}/{idDestinatario}")
    public ResponseEntity<Object> confirmarSolicitacao(@PathVariable Long idRemetente, @PathVariable Long idDestinatario)
    {
        try{
            if(!isIdValido(idRemetente) || !isIdValido(idDestinatario))
                return ResponseEntity.badRequest().body(new Erro("IDs inválidos!!"));

            Usuario origem = usuarioService.getById(idRemetente);
            Usuario destino = usuarioService.getById(idDestinatario);
            if(origem == null || destino == null)
            {
                if(origem == null)
                    return ResponseEntity.badRequest().body(new Erro("Usuário de Origem não existe!!"));
                return ResponseEntity.badRequest().body(new Erro("Usuário de Destino não existe!!"));
            }
            else
            {
                SolicitacaoMensagem solicitacaoMensagem = solicitacaoMensagemService.getByUsers(origem, destino);
                if(solicitacaoMensagem != null)
                {
                    solicitacaoMensagem.setStatus("Confirmada");
                    solicitacaoMensagemService.salvar(solicitacaoMensagem); //editar
                    return ResponseEntity.ok(solicitacaoMensagem);
                }
                else
                {
                    return ResponseEntity.badRequest().body(new Erro("Essa solicitação não existe!!"));
                }
            }
        }
        catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(new Erro("Erro interno no servidor!!"));
        }
    }

    /**
     * Confirmar TODAS as solicitações de envio de mensagem
     * */
    @PostMapping("/confirmarSolicitacao")
    public ResponseEntity<Object> confirmarSolicitacao()
    {
        return ResponseEntity.ok(new Erro("oi"));
    }

    /**
     * Trata o envio de uma mensagem para um determinado Grupo
     * */
    @PostMapping("/enviarMensagemGrupo")
    public ResponseEntity<Object> enviarMensagemNoGrupo(@RequestBody Mensagem mensagem)
    {
        return null;
    }

    /**
     * Trata a solicitação de um pedido de entrada em determinado Grupo
     * */
    @PostMapping
    public ResponseEntity<Object> solicitarEntradaGrupo(@RequestBody SolicitacaoEntradaGrupo solicitacaoEntradaGrupo)
    {
        return null;
    }

    /**
     * Trata a entrada de um Usuário em determinado Grupo.
     *      Aqui, deve-se ter certeza de que todos os Usuários do Grupo já aceitaram o mesmo a entrar
     * */
//    @PostMapping
//    public ResponseEntity<Object> entrarNoGrupo(@RequestBody UsuarioGrupo usuarioGrupo)
//    {
//        return null;
//    }

    // ==================================================================================================================================
    // MÉTODOS AUXILIARES
    // ==================================================================================================================================
    private boolean isIdValido(Long id)
    {
        return id != null && id > 0;
    }
}
