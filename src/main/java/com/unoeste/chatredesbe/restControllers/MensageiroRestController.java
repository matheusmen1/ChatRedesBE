package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
    @Autowired
    private ConversionService conversionService;

    /**
     * Método que retorna a lista de todas as mensagens de um Usuário com outro Usuário
     * */
    @GetMapping("/getMensagensConversa/{idOrigem}/{idDestino}")
    public ResponseEntity<Object> getMensagensConversa(@PathVariable Long idOrigem, @PathVariable Long idDestino)
    {
        // implementar no banco para ser mais performático
        List<Mensagem> mensagensConversa = mensagemService.getAllConversa(idOrigem, idDestino);

        if(!mensagensConversa.isEmpty())
        {
            return ResponseEntity.ok(mensagensConversa);
        }
        return ResponseEntity.badRequest().body(new Erro("Nenhuma mensagem encontrada!!"));

//        // seleciona apenas as mensagens que o Destino me enviou
//        List<DestinatarioMensagem> recebidas = destinatarioMensagemService.getByIdDestinatario(idOrigem);
//        List<Mensagem> mensagensRecebidas = new ArrayList<>();
//        for(DestinatarioMensagem dm : recebidas)
//        {
//            if(dm.getMensagem().getRemetente().getId() == idDestino)
//                mensagensRecebidas.add(dm.getMensagem());
//        }
//
//        // seleciona apenas as mensagens que a Origem enviou
//        List<DestinatarioMensagem> enviadas = destinatarioMensagemService.getByIdDestinatario(idDestino);
//        List<Mensagem> mensagensEnviadas = new ArrayList<>();
//        for(DestinatarioMensagem dm : enviadas)
//        {
//            if(dm.getMensagem().getRemetente().getId() == idOrigem)
//                mensagensEnviadas.add(dm.getMensagem());
//        }
//
//        // ordenar
//        List<Mensagem> todasMensagens = new ArrayList<>();
//        todasMensagens.addAll(mensagensRecebidas);
//        todasMensagens.addAll(mensagensEnviadas);
//        todasMensagens.sort(Comparator.comparing(Mensagem::getDataHoraEnvio)); //ordenar pela data e hora
    }

    /**
     * Método que retorna a lista de TODAS as mensagens já recebidas de Usuário
     * */
    @GetMapping("/getMensagensRecebidas/{idDestinatario}")
    public ResponseEntity<Object> getMensagensRecebidas(@PathVariable Long idDestinatario)
    {
        List<DestinatarioMensagem> destinatarioMensagens = destinatarioMensagemService.getByIdDestinatario(idDestinatario);

        // Ordena a lista pela data e hora de entrega
        destinatarioMensagens.sort(Comparator.comparing(DestinatarioMensagem::getDataHoraEntrega));

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
            // verificando se existe os dois usuários
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
                SolicitacaoMensagem solicitacaoMensagemInverso = solicitacaoMensagemService.getByUsers(destino, origem);
                if(solicitacaoMensagemInverso == null)
                {
                    solicitacaoMensagemService.salvar(new SolicitacaoMensagem(destino, origem, "Confirmada"));
                }

                mensagem.setDataHoraEnvio(LocalDateTime.now());
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
            // verificando se existe os dois usuários
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
     * Endpoint para a criação de um novo Grupo
     * */
    @PostMapping("/criarNovoGrupo/{nomeGrupo}/{idCriador}")
    public ResponseEntity<Object> criarNovoGrupo(
            @PathVariable String nomeGrupo,
            @PathVariable Long idCriador,
            @RequestBody List<Usuario> usuarios)
    {
        try{
            Usuario usuario = usuarioService.getById(idCriador);
            if(usuario == null)
                return ResponseEntity.badRequest().body(new Erro("Usuário não existe, impossível criar o Grupo!!"));

            // verificar se não existe um grupo com o mesmo nome
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo != null)
                return ResponseEntity.badRequest().body(new Erro("Esse grupo já existe!!"));

            // adicionar o novo grupo
            Grupo novoGrupo = new Grupo(nomeGrupo, usuario, LocalDateTime.now());
            novoGrupo = grupoService.salvar(novoGrupo);

            // adicionar o criador ao grupo
            UsuarioGrupo usuarioGrupo = new UsuarioGrupo(usuario, novoGrupo, novoGrupo.getDataCriacao());
            usuarioGrupo = usuarioGrupoService.salvar(usuarioGrupo);

            // criar os convites ao grupo
            for(Usuario u : usuarios)
            {
                ConviteGrupo novoConviteGrupo = new ConviteGrupo(usuario, u, novoGrupo, "Pendente");
                conviteGrupoService.salvar(novoConviteGrupo);
            }

            return ResponseEntity.ok(novoGrupo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
    }

    /**
     * Método para CONFIRMAR o convite de entrada em um Grupo
     * */
    @PutMapping("/aceitarConviteGrupo/{idGrupo}/{idConvidado}")
    public ResponseEntity<Object> aceitarConviteGrupo(@PathVariable Long idGrupo, @PathVariable Long idConvidado)
    {
        try{
            ConviteGrupo conviteGrupo = conviteGrupoService.getByGrupoConvidado(idGrupo, idConvidado);
            if(conviteGrupo == null)
                return ResponseEntity.badRequest().body(new Erro("Esse convite não existe!!"));

            conviteGrupo.setStatus("Confirmado");
            conviteGrupoService.salvar(conviteGrupo);

            Usuario usuario = new Usuario(idConvidado);
            Grupo grupo = new Grupo(idGrupo);
            UsuarioGrupo usuarioGrupo = new UsuarioGrupo(usuario, grupo, LocalDateTime.now());
            usuarioGrupoService.salvar(usuarioGrupo);

            return ResponseEntity.ok(conviteGrupo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
    }

    /**
     * Método para REJEITAR o convite de entrada em um Grupo
     * */
    @PutMapping("/rejeitarConviteGrupo/{idGrupo}/{idConvidado}")
    public ResponseEntity<Object> rejeitarConviteGrupo(@PathVariable Long idGrupo, @PathVariable Long idConvidado)
    {
        try{
            ConviteGrupo conviteGrupo = conviteGrupoService.getByGrupoConvidado(idGrupo, idConvidado);
            if(conviteGrupo == null)
                return ResponseEntity.badRequest().body(new Erro("Esse convite não existe!!"));

            // editar o campo de status do convite
            conviteGrupo.setStatus("Rejeitado");
            conviteGrupoService.salvar(conviteGrupo);

            return ResponseEntity.ok(conviteGrupo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
    }

    /**
     * Endpoint para a adição (convite) de novos Usuários em um Grupo
     * */
    @PostMapping("/addUsersGrupo/{nomeGrupo}/{idCriador}")
    public ResponseEntity<Object> addUsersGrupo(@PathVariable String nomeGrupo, @PathVariable Long idCriador,@RequestBody List<Usuario> usuarios)
    {
        try{
            // verificando se existe o criador do grupo
            Usuario usuario = usuarioService.getById(idCriador);
            if(usuario == null)
                return ResponseEntity.badRequest().body(new Erro("Usuário não existe!!"));

            // verifica se existe o grupo e se o criador é o mesmo
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo == null || grupo.getCriador() != usuario)
            {
                if(grupo == null)
                    return ResponseEntity.badRequest().body(new Erro("O Grupo não existe!!"));
                else
                    return ResponseEntity.badRequest().body(new Erro("O Grupo não pertence a esse criador!!"));
            }

            // realizar convites
            List<ConviteGrupo> convites = new ArrayList<>();
            for(Usuario u : usuarios)
            {
                UsuarioGrupo usuarioGrupo = usuarioGrupoService.getByUsuarioGrupo(grupo.getId(), usuario.getId());
                if(usuarioGrupo == null)
                {
                    // realizar o convite pois o mesmo ainda não está no grupo
                    ConviteGrupo novoConviteGrupo = new ConviteGrupo(usuario, u, grupo, "Pendente");
                    conviteGrupoService.salvar(novoConviteGrupo);
                    convites.add(novoConviteGrupo);
                }
            }

            return ResponseEntity.ok(convites);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
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
    @PostMapping("solicitarEntradaGrupo/{nomeGrupo}/{idSolicitante}")
    public ResponseEntity<Object> solicitarEntradaGrupo(@PathVariable String nomeGrupo, @PathVariable Long idSolicitante)
    {
        try
        {
            // verificando se o usuário existe
            Usuario usuario = usuarioService.getById(idSolicitante);
            if(usuario == null)
                return ResponseEntity.badRequest().body(new Erro("Esse Usuário não existe!!"));

            // verificando se o grupo existe
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo == null)
                return ResponseEntity.badRequest().body(new Erro("Esse Grupo não existe!!"));

            // criar a solicitação
            SolicitacaoEntradaGrupo solicitacaoEntradaGrupo = new SolicitacaoEntradaGrupo(grupo, usuario, "Pendente");
            solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.salvar(solicitacaoEntradaGrupo);

            // criar os votos solicitação
            List<VotoSolicitacao> votos = new ArrayList<>();
            for(UsuarioGrupo ug : grupo.getUsuariosGrupo())
            {
                VotoSolicitacao votoSolicitacao = new VotoSolicitacao(ug.getUsuario(), solicitacaoEntradaGrupo, "Pendente");
                votoSolicitacaoService.salvar(votoSolicitacao);
                votos.add(votoSolicitacao);
            }

            return ResponseEntity.ok(votos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
    }

    /**
     * Método para votar na entrada de um usuário no Grupo
     * */
    @PutMapping("/votarEntradaGrupo/{idVotante}/{idSolicitacao}/{voto}")
    public ResponseEntity<Object> votarSolicitacaoEntradaGrupo(@PathVariable Long idVotante, @PathVariable Long idSolicitacao, @PathVariable Integer voto)
    {
        try
        {
            // verificar se a solicitação existe
            SolicitacaoEntradaGrupo solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.getById(idSolicitacao);
            if(solicitacaoEntradaGrupo == null)
                return ResponseEntity.badRequest().body(new Erro("Essa solicitação não existe!!"));

            // verificar se o existe o voto
            VotoSolicitacao votoSolicitacao = votoSolicitacaoService.getByIdVotanteSolicitacao(idVotante, idSolicitacao);
            if(votoSolicitacao == null)
                return ResponseEntity.badRequest().body(new Erro("Esse voto não existe!!"));

            if(solicitacaoEntradaGrupo.getStatus().equals("Pendente"))
            {
                // alterar o voto
                if(voto == 1)
                {
                    votoSolicitacao.setStatus("Permitido");
                    votoSolicitacao = votoSolicitacaoService.salvar(votoSolicitacao);

                    // verificar se todos os votos foram "Permitido" da solicitação
                    List<VotoSolicitacao> votos = votoSolicitacaoService.getAllSolicitacao(solicitacaoEntradaGrupo.getId());
                    boolean permitido = true;
                    for(int i=0; i<votos.size() && permitido; i++)
                    {
                        if(!votos.get(i).getStatus().equals("Permitido"))
                            permitido = false;
                    }
                    if(permitido) // deixar a solicitação como "Permitido"
                    {
                        solicitacaoEntradaGrupo.setStatus("Permitido");
                        solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.salvar(solicitacaoEntradaGrupo);

                        // criar um UsuarioGrupo
                        UsuarioGrupo usuarioGrupo = new UsuarioGrupo(
                                solicitacaoEntradaGrupo.getSolicitante(),
                                solicitacaoEntradaGrupo.getGrupo(),
                                LocalDateTime.now()
                        );
                    }

                    return ResponseEntity.ok(votoSolicitacao);
                }
                else
                {
                    votoSolicitacao.setStatus("Negado");
                    solicitacaoEntradaGrupo.setStatus("Negada");
                    solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.salvar(solicitacaoEntradaGrupo);
                    return ResponseEntity.ok(solicitacaoEntradaGrupo);
                }
            }
            else if(solicitacaoEntradaGrupo.getStatus().equals("Confirmado"))
            {
                return ResponseEntity.badRequest().body(new Erro("Votação já concluída, Usuário foi PERMITIDO!"));
            }
            else // aqui a solicitação foi negada
            {
                return ResponseEntity.badRequest().body(new Erro("Votação já concluída, Usuário foi NÃO FOI PERMITIDO!"));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new Erro("Erro!! " + e.getMessage()));
        }
    }




    // ==================================================================================================================================
    // MÉTODOS AUXILIARES
    // ==================================================================================================================================

}
