package com.unoeste.chatredesbe.Padrao;

import com.unoeste.chatredesbe.entities.*;
import com.unoeste.chatredesbe.repositories.UsuarioRepository;
import com.unoeste.chatredesbe.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MensageiroFacade
{
    @Autowired private UsuarioService usuarioService;
    @Autowired private GrupoService grupoService;
    @Autowired private UsuarioGrupoService usuarioGrupoService;
    @Autowired private MensagemService mensagemService;
    @Autowired private DestinatarioMensagemService destinatarioMensagemService;
    @Autowired private SolicitacaoMensagemService solicitacaoMensagemService;
    @Autowired private ConviteGrupoService conviteGrupoService;
    @Autowired private SolicitacaoEntradaGrupoService solicitacaoEntradaGrupoService;
    @Autowired private VotoSolicitacaoService votoSolicitacaoService;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // ===================================================================================================================
    // GET's
    // ===================================================================================================================
    /**
     * Método que retorna a lista de todas as {@link Mensagem} de um {@link Usuario} com outro {@link Usuario}
     * */
    public ResultadoOperacao<List<Mensagem>> getMensagensConversa(Long idOrigem, Long idDestino)
    {
        // verificar se os usuários existem
        Usuario user1 = usuarioService.getById(idOrigem);
        Usuario user2 = usuarioService.getById(idDestino);
        if(user1 == null || user2 == null)
            return ResultadoOperacao.erro("Usuario nao existe!!");

        // verificar se o origem pode enviar mensagem para o destino
        SolicitacaoMensagem sm = solicitacaoMensagemService.getByUsers(user1, user2);
        if(sm == null)
            return ResultadoOperacao.erro("Voce nunca solicitou uma conversa com "+user2.getNome());
        else if(sm.getStatus().equalsIgnoreCase("rejeitada"))
            return ResultadoOperacao.erro("Voces nao podem se comunicar!!. "+user2.getNome()+" nao te aceitou!!");
        else if(sm.getStatus().equalsIgnoreCase("pendente"))
            return ResultadoOperacao.erro("Voces nao podem se comunicar!!. "+user2.getNome()+" ainda nao te aceitou!!");

        // implementar no banco para ser mais performático
        List<Mensagem> mensagensConversa = mensagemService.getAllConversa(idOrigem, idDestino);

        if(!mensagensConversa.isEmpty())
            return ResultadoOperacao.sucesso("Mensagens encontradas com sucesso!", mensagensConversa);
        else
            return ResultadoOperacao.sucesso("Nenhuma mensagem encontrada!", null);
    }

    /**
     * Método para listar todas as conversas com particulares com Usuários
     * */
    public ResultadoOperacao<List<SolicitacaoMensagem>> getConversasParticulares(Long idUsuario)
    {
        try
        {
            List<SolicitacaoMensagem> conversas = solicitacaoMensagemService.getConversas(idUsuario);
            if (conversas != null && !conversas.isEmpty())
                return ResultadoOperacao.sucesso("Conversas encontradas com sucesso!", conversas);
            else if(conversas == null)
                return ResultadoOperacao.erro("Erro ao recuperar Conversas Particulares!!");
            else
                return ResultadoOperacao.erro("Nenhuma conversa particular encontrada!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método para listar todas as conversas com Grupos
     * */
    public ResultadoOperacao<List<UsuarioGrupo>> getConversasGrupos(Long idUsuario)
    {
        try
        {
            List<UsuarioGrupo> conversasGrupos = usuarioGrupoService.getAllGrupoByUser(idUsuario);
            if (conversasGrupos != null && !conversasGrupos.isEmpty())
                return ResultadoOperacao.sucesso("Conversas encontradas com sucesso!", conversasGrupos);
            else if(conversasGrupos == null)
                return ResultadoOperacao.erro("Erro ao recuperar Conversas dos Grupos!!");
            else
                return ResultadoOperacao.sucesso("Nenhuma conversa com Grupo encontrada!!", null);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método Para Listar Todos os Grupos Cadastrados no Banco de Dados
     * */
    public ResultadoOperacao<List<Grupo>> getAllGrupos()
    {
        try
        {
            List<Grupo> grupos = grupoService.getAll();
            if (grupos != null && grupos.size() > 0)
                return ResultadoOperacao.sucesso("Grupos encontrados com sucesso!!", grupos);
            else
                return ResultadoOperacao.erro("Nenhum Grupo Encontrado!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método Para Listar Todos Os Grupos que um Usuário Pertence
     * */
    public ResultadoOperacao<List<Grupo>> getAllGruposUsuario(Long idUsuario)
    {
        try
        {
            List<Grupo> grupos = grupoService.getAllGruposUsuario(idUsuario);
            if (!grupos.isEmpty())
                return ResultadoOperacao.sucesso("Grupos do usuário encontrados com sucesso!!", grupos);
            else
                return ResultadoOperacao.erro("Nenhum Grupo Encontrado!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método Para Listar Todos os Usuários com o Status Online
     * */
    public ResultadoOperacao<List<Usuario>> getAllUsersOnline()
    {
        try
        {
            List<Usuario> usuarios = usuarioService.getByStatus();
            if (usuarios != null && !usuarios.isEmpty())
                return ResultadoOperacao.sucesso("Usuários online encontrados com sucesso!!", usuarios);
            else
                return ResultadoOperacao.erro("Nenhum Usuario Encontrado!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método Para Listar Todas as Solicitações de um Usuário
     * */
    public ResultadoOperacao<List<SolicitacaoMensagem>> getAllSolicitacoesById(Long idUsuario)
    {
        try
        {
            // as duas consultas em uma lista só
            List<SolicitacaoMensagem> solicitacaoMensagens = solicitacaoMensagemService.getAllSolicitacoesById(idUsuario);
            if(solicitacaoMensagens != null && !solicitacaoMensagens.isEmpty())
                return ResultadoOperacao.sucesso("Solicitacoes encontradas com sucesso!", solicitacaoMensagens);
            else
                return ResultadoOperacao.erro("Nenhuma Solicitacao de Mensagem Encontrada!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    public ResultadoOperacao<List<SolicitacaoMensagem>> getAllSolicitacoesByIdPendentes(Long idUsuario, String status)
    {
        try
        {
            List<SolicitacaoMensagem> solicitacaoMensagens = solicitacaoMensagemService.getAllSolicitacoesByIdPendentes(idUsuario, status);
            if(solicitacaoMensagens != null && !solicitacaoMensagens.isEmpty())
                return ResultadoOperacao.sucesso("Solicitacoes encontradas com sucesso!", solicitacaoMensagens);
            else
                return ResultadoOperacao.erro("Nenhuma Solicitacao de Mensagem Encontrada!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }
    public ResultadoOperacao<List<ConviteGrupo>> getAllConvitesGruposByIdPendentes(Long idUsuario, String status)
    {
        try
        {
            List<ConviteGrupo> conviteGrupos = conviteGrupoService.getAllConvitesGruposByIdPendentes(idUsuario, status);
            if(conviteGrupos != null && !conviteGrupos.isEmpty())
                return ResultadoOperacao.sucesso("Convites encontrados com sucesso!", conviteGrupos);
            else
                return ResultadoOperacao.erro("Nenhuma Convite Encontrado!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }
    public ResultadoOperacao<List<ConviteGrupo>> getAllConvitesByConvidado(Long idConvidado)
    {
        try
        {
            List<ConviteGrupo> convites = conviteGrupoService.getAllConvitesByConvidado(idConvidado);
            if(convites != null && !convites.isEmpty())
                return ResultadoOperacao.sucesso("Convites encontrados com sucesso!", convites);
            else
                return ResultadoOperacao.erro("Nenhum Convite de Grupo Encontrado!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }
    
    /**
     * Método para retornar todas as Mensagens de um Grupo
     * */
    public ResultadoOperacao<List<Mensagem>> getMensagensGrupo(Long idGrupo)
    {
        try
        {
            // verificar se o GRUPO existe
            Grupo grupo = grupoService.getById(idGrupo);
            if(grupo == null)
                return ResultadoOperacao.erro("Grupo  não existe!!");

            List<Mensagem> mensagensGrupo = mensagemService.getAllByGrupo(idGrupo);

            return ResultadoOperacao.sucesso("Mensagens do grupo encontradas com sucesso!!", mensagensGrupo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    // ===================================================================================================================
    // POST's
    // ===================================================================================================================
    public ResultadoOperacao<DestinatarioMensagem> enviarMensagemPessoa(Long idRemetente, Long idDestinatario, String conteudo)
    {
        try
        {
            Usuario origem = usuarioService.getById(idRemetente);
            Usuario destino = usuarioService.getById(idDestinatario);
            SolicitacaoMensagem inversa = solicitacaoMensagemService.getByUsers(destino, origem);
            if (inversa == null)
            {
                solicitacaoMensagemService.salvar(new SolicitacaoMensagem(destino, origem, "Confirmada"));
            }

            SolicitacaoMensagem solicitacao = solicitacaoMensagemService.getByUsers(origem, destino);

            Mensagem mensagem = new Mensagem();
            mensagem.setRemetente(origem);
            mensagem.setConteudo(conteudo);
            mensagem.setDataHoraEnvio(LocalDateTime.now());

            Mensagem mensagemSalva = mensagemService.salvar(mensagem);

            DestinatarioMensagem dm = destinatarioMensagemService.salvar(
                    new DestinatarioMensagem(destino, mensagemSalva, "Pendente", mensagemSalva.getDataHoraEnvio()));

            if (solicitacao != null && "Confirmada".equalsIgnoreCase(solicitacao.getStatus())) {
                return ResultadoOperacao.sucesso("Mensagem Enviada com Sucesso!", dm);
            }
            else if(solicitacao != null)
            {
                return ResultadoOperacao.sucesso("Mensagem Enviada mas o Destino Ainda Nao Aceitou", dm);
            }

            solicitacaoMensagemService.salvar(new SolicitacaoMensagem(origem, destino, "Pendente"));

            if (solicitacao == null)
            {
                return ResultadoOperacao.erro("Enviando Solicitacao de Mensagem Para " + destino.getApelido() + "...");
            }

            // Ja recebeu uma solicitacao de mensagem antes
            return ResultadoOperacao.erro("Solicitacaoo de Mensagem Pendente Para " + destino.getApelido() + "...");
        }
        catch (Exception e)
        {
            return ResultadoOperacao.erro("Erro Interno no Servidor: " + e.getMessage());
        }
    }
    /**
     * Endpoint para a criação de um novo Grupo
     * */
    public ResultadoOperacao<Grupo> criarNovoGrupo(String nomeGrupo,Long idCriador,List<Usuario> usuarios)
    {
        try{
            Usuario usuario = usuarioService.getById(idCriador);
            if(usuario == null)
                return ResultadoOperacao.erro("Usuário não existe, impossível criar o Grupo!!");

            // verificar se não existe um grupo com o mesmo nome
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo != null)
                return ResultadoOperacao.erro("Esse grupo já existe!!");

            // adicionar o novo grupo
            Grupo novoGrupo = new Grupo(nomeGrupo, usuario, LocalDateTime.now());
            novoGrupo = grupoService.salvar(novoGrupo);

            // adicionar o criador ao grupo
            UsuarioGrupo usuarioGrupo = new UsuarioGrupo(usuario, novoGrupo, novoGrupo.getDataCriacao());
            usuarioGrupo = usuarioGrupoService.salvar(usuarioGrupo);

            // criar os convites ao grupo
            if(usuarios != null)
            {
                for(Usuario u : usuarios)
                {
                    ConviteGrupo novoConviteGrupo = new ConviteGrupo(usuario, u, novoGrupo, "Pendente");
                    conviteGrupoService.salvar(novoConviteGrupo);
                }
            }

            return ResultadoOperacao.sucesso("Grupo criado com sucesso!! ", novoGrupo);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método para a adição (convite) de novos Usuários em um Grupo
     * */
    public ResultadoOperacao<List<ConviteGrupo>> addUsersGrupo(String nomeGrupo, Long idCriador, List<Usuario> usuarios)
    {
        try{
            // verificando se existe o criador do grupo
            Usuario usuario = usuarioService.getById(idCriador);
            if(usuario == null)
                return ResultadoOperacao.erro("Usuário não existe!!");

            // verifica se existe o grupo e se o criador é o mesmo
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo == null || grupo.getCriador().getId() != usuario.getId())
            {
                if(grupo == null)
                    return ResultadoOperacao.erro("O Grupo não existe!!");
                else
                    return ResultadoOperacao.erro("O Grupo não pertence a esse criador!!");
            }

            // realizar convites
            List<ConviteGrupo> convites = new ArrayList<>();
            for(Usuario u : usuarios)
            {
                Usuario userValido = usuarioService.getById(u.getId());
                if(userValido != null)
                {
                    UsuarioGrupo usuarioGrupo = usuarioGrupoService.getByUsuarioGrupo(grupo.getId(), u.getId());
                    if(usuarioGrupo == null)
                    {
                        // realizar o convite pois o mesmo ainda não está no grupo
                        ConviteGrupo novoConviteGrupo = new ConviteGrupo(usuario, u, grupo, "Pendente");
                        conviteGrupoService.salvar(novoConviteGrupo);
                        convites.add(novoConviteGrupo);
                    }
                }
            }

            return ResultadoOperacao.sucesso("Convites enviados com sucesso!!", convites);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Trata o envio de uma mensagem para um determinado Grupo
     * */
    public ResultadoOperacao<Mensagem> enviarMensagemNoGrupo(Mensagem mensagem, Long idRemetente, Long idGrupo)
    {
        try
        {
            // verificar se o USUÁRIO existe
            Usuario usuario = usuarioService.getById(idRemetente);
            if(usuario == null)
                return ResultadoOperacao.erro("Usuário remetente não existe!!");

            // verificar se o GRUPO existe
            Grupo grupo = grupoService.getById(idGrupo);
            if(grupo == null)
                return ResultadoOperacao.erro("Grupo  não existe!!");

            // setar as informações e persistir a mensagem
            mensagem.setGrupo(grupo);
            mensagem.setRemetente(usuario);
            mensagem.setDataHoraEnvio(LocalDateTime.now());
            mensagem = mensagemService.salvar(mensagem);

            return ResultadoOperacao.sucesso("Mensagem enviada para o grupo com sucesso!!", mensagem);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
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
    public ResultadoOperacao<String> enviarMensagemSeletivaGrupo(
            Long idGrupo,
            EnviarMensagemSeletivaGrupoDTO dto)
    {
        try {
            // verificar se USUÁRIO existe
            Usuario usuario = usuarioService.getById(dto.getRemetenteId());
            if (usuario == null)
                return ResultadoOperacao.erro("Usuário remetente não existe!!");

            // verificar se GRUPO existe
            Grupo grupo = grupoService.getById(idGrupo);
            if (grupo == null)
                return ResultadoOperacao.erro("Grupo não existe!!");

            // verificando se todos estão no grupo
            List<UsuarioGrupo> usuariosGrupo = usuarioGrupoService.getAllUserByGrupo(grupo.getId());
            List<Long> idsDoGrupo = new ArrayList<>();
            for (UsuarioGrupo ug : usuariosGrupo) {
                idsDoGrupo.add(ug.getUsuario().getId());
            }
            for (Long idUsuario : dto.getUsuariosIds()) {
                if (!idsDoGrupo.contains(idUsuario)) {
                    return ResultadoOperacao.erro("Nem todos os Usuários estão no grupo!!");
                }
            }

            // enviar a mensagem para todos do GRUPO
            for (Long idUsuario : dto.getUsuariosIds()) {
                Usuario destinatario = usuarioService.getById(idUsuario);
                if (destinatario == null)
                    return ResultadoOperacao.erro("Usuário de destino não existe!!");

                Mensagem msg = new Mensagem();
                msg.setRemetente(usuario);
                msg.setConteudo(dto.getConteudo());
                msg.setGrupo(grupo);
                msg.setDataHoraEnvio(LocalDateTime.now());

                enviarMensagemPessoa(msg.getRemetente().getId(), destinatario.getId(), msg.getConteudo());
            }

            return ResultadoOperacao.sucesso("Mensagens enviadas com sucesso!", "Mensagens enviadas com sucesso!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Trata a solicitação de um pedido de entrada em determinado Grupo
     * */
    public ResultadoOperacao<List<VotoSolicitacao>> solicitarEntradaGrupo(String nomeGrupo, Long idSolicitante)
    {
        try
        {
            // verificando se o usuário existe
            Usuario usuario = usuarioService.getById(idSolicitante);
            if(usuario == null)
                return ResultadoOperacao.erro("Esse Usuário não existe!!");

            // verificando se o grupo existe
            Grupo grupo = grupoService.getByName(nomeGrupo);
            if(grupo == null)
                return ResultadoOperacao.erro("Esse Grupo não existe!!");

            //verificar se o idSolicitante já não está no grupo
            UsuarioGrupo usuarioGrupo = usuarioGrupoService.getByUsuarioGrupo(grupo.getId(),usuario.getId());
            if(usuarioGrupo != null)
                return ResultadoOperacao.erro("Usuario ja esta no grupo");

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

            return ResultadoOperacao.sucesso("Solicitação de entrada criada com sucesso!!", votos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    // ===================================================================================================================
    // PUT's
    // ===================================================================================================================
    /**
     * Confirmar UMA solicitação de envio de mensagem
     * */
    public ResultadoOperacao<SolicitacaoMensagem> confirmarSolicitacao(Long idRemetente, Long idDestinatario, String status)
    {
        try{
            // verificando se existe os dois usuários
            Usuario origem = usuarioService.getById(idRemetente);
            Usuario destino = usuarioService.getById(idDestinatario);
            if(origem == null || destino == null)
            {
                if(origem == null)
                    return ResultadoOperacao.erro("Usuário de Origem não existe!!");
                return ResultadoOperacao.erro("Usuário de Destino não existe!!");
            }
            else
            {
                SolicitacaoMensagem solicitacaoMensagem = solicitacaoMensagemService.getByUsers(origem, destino);
                if(solicitacaoMensagem != null)
                {
                    if (status.equals("Confirmada"))
                    {
                        solicitacaoMensagem.setStatus("Confirmada");
                        solicitacaoMensagemService.salvar(solicitacaoMensagem); //editar
                        return ResultadoOperacao.sucesso("Solicitação confirmada com sucesso!!", solicitacaoMensagem);
                    }
                    else
                    {
                        solicitacaoMensagem.setStatus("Recusada");
                        solicitacaoMensagemService.salvar(solicitacaoMensagem); //editar
                        return ResultadoOperacao.sucesso("Solicitação recusada com sucesso!!", solicitacaoMensagem);
                    }
                }
                else
                {
                    return ResultadoOperacao.erro("Essa solicitação não existe!!");
                }
            }
        }
        catch (Exception e)
        {
            return ResultadoOperacao.erro("Erro interno no servidor!!");
        }
    }
    @Transactional
    public ResultadoOperacao<DestinatarioMensagem> confirmarEntregaMensagem(Long idMensagem, Long idDestinatario)
    {
        try
        {

            Mensagem mensagem = mensagemService.getById(idMensagem);
            Usuario destino = usuarioService.getById(idDestinatario);

            if(mensagem == null || destino == null)
            {
                if(mensagem == null)
                    return ResultadoOperacao.erro("Mensagem não existe!!");
                return ResultadoOperacao.erro("Usuário de Destino não existe!!");
            }
            else
            {
                DestinatarioMensagem dm = destinatarioMensagemService.getByMensagemAndDestinatario(mensagem, destino);
                if(dm != null)
                {
                    dm.setStatus("Entregue");
                    dm.setDataHoraEntrega(LocalDateTime.now());
                    destinatarioMensagemService.salvar(dm);
                    return ResultadoOperacao.sucesso("Mensagem confirmada como Entregue!!", dm);
                }
                else
                {
                    return ResultadoOperacao.erro("Esse destinatário não está vinculado a esta mensagem!!");
                }
            }
        }
        catch (Exception e)
        {
            return ResultadoOperacao.erro("Erro interno no servidor: " + e.getMessage());
        }
    }
    /**
     * Método para ALTERAR o convite de entrada em um Grupo.
     *      Aceitar (1),
     *      Rejeitar (0)
     * */
    public ResultadoOperacao<ConviteGrupo> alterarConviteGrupo(Long idGrupo, Long idConvidado, Integer voto)
    {
        try{
            // verifica se existe o convite
            ConviteGrupo conviteGrupo = conviteGrupoService.getByGrupoConvidado(idGrupo, idConvidado);
            if(conviteGrupo == null)
                return ResultadoOperacao.erro("Esse convite não existe!!");

            if(voto == 1) // Aceitar
            {
                conviteGrupo.setStatus("Confirmado");
                conviteGrupoService.salvar(conviteGrupo);

                // criar uma linha para representar que o usuário entrou no grupo
                Usuario usuario = new Usuario(idConvidado);
                Grupo grupo = new Grupo(idGrupo);
                UsuarioGrupo usuarioGrupo = new UsuarioGrupo(usuario, grupo, LocalDateTime.now());
                usuarioGrupoService.salvar(usuarioGrupo);
                mensagemService.salvar(new Mensagem(null, "Usuario "+usuario.getNome()+" entrou no grupo!", grupo,LocalDateTime.now()));

                return ResultadoOperacao.sucesso("Convite confirmado com sucesso!!", conviteGrupo);
            }
            else
            {
                conviteGrupo.setStatus("Rejeitado");
                conviteGrupo = conviteGrupoService.salvar(conviteGrupo);

                return ResultadoOperacao.sucesso("Convite rejeitado com sucesso!!", conviteGrupo);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    /**
     * Método para votar na entrada de um usuário no Grupo
     * */
    public ResultadoOperacao<Object> votarSolicitacaoEntradaGrupo(Long idVotante, Long idSolicitacao, Integer voto)
    {
        try
        {
            // verificar se a solicitação existe
            SolicitacaoEntradaGrupo solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.getById(idSolicitacao);
            if(solicitacaoEntradaGrupo == null)
                return ResultadoOperacao.erro("Essa solicitação não existe!!");

            // verificar se o existe o voto
            VotoSolicitacao votoSolicitacao = votoSolicitacaoService.getByIdVotanteSolicitacao(idVotante, idSolicitacao);
            if(votoSolicitacao == null)
                return ResultadoOperacao.erro("Esse voto não existe!!");

            if(solicitacaoEntradaGrupo.getStatus().equals("Pendente"))
            {
                // alterar o voto
                if(voto == 1) // -> Aceitar
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

                    return ResultadoOperacao.sucesso("Voto realizado com sucesso!!", votoSolicitacao);
                }
                else
                {
                    votoSolicitacao.setStatus("Negado");
                    votoSolicitacaoService.salvar(votoSolicitacao);
                    solicitacaoEntradaGrupo.setStatus("Negada");
                    solicitacaoEntradaGrupo = solicitacaoEntradaGrupoService.salvar(solicitacaoEntradaGrupo);
                    return ResultadoOperacao.sucesso("Solicitação negada com sucesso!!", solicitacaoEntradaGrupo);
                }
            }
            else if(solicitacaoEntradaGrupo.getStatus().equals("Confirmado"))
            {
                return ResultadoOperacao.erro("Votação já concluída, Usuário foi PERMITIDO!");
            }
            else // aqui a solicitação foi negada
            {
                return ResultadoOperacao.erro("Votação já concluída, Usuário foi NÃO FOI PERMITIDO!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    @Transactional
    public ResultadoOperacao<Usuario> alterarStatus(Long idUsuario, String status)
    {
        try
        {
            Usuario novoUsuario = usuarioService.getById(idUsuario);
            if (novoUsuario != null)
            {
                if(!novoUsuario.getStatus().equals(status))
                {
                    if (status.equals("online") || status.equals("offline") || status.equals("ocupado"))
                    {
                        novoUsuario.setStatus(status);//online, ocupado e offline
                        novoUsuario = usuarioService.salvar(novoUsuario);
                        return ResultadoOperacao.sucesso("Status Alterado com Sucesso", novoUsuario);
                    }
                    else
                        return ResultadoOperacao.erro("Status Nao Existe (Online, Offline ou Ocupado)");

                }
                else
                    return ResultadoOperacao.erro("Usuario Ja " + novoUsuario.getStatus());
            }
            else
                return ResultadoOperacao.erro("Usuario Nao Encontrado");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro ao Alterar Status: " + e.getMessage());
        }
    }

    // ===================================================================================================================
    // DELETE's
    // ===================================================================================================================
    /**
     * Método Para Sair de um Grupo
     * */
    public ResultadoOperacao<Erro> deleteUsuarioGrupo(Long idUsuario, Long idGrupo)
    {
        try
        {
            // verificar se existe o usuário
            Usuario usuario = usuarioService.getById(idUsuario);
            if(usuario == null)
                return ResultadoOperacao.erro("Nenhum Usuario Encontrado!!");

            // verificar se existe o grupo
            Grupo grupo = grupoService.getById(idGrupo);
            if (grupo == null)
                return ResultadoOperacao.erro("Nenhum Grupo Encontrado!!");

            // deletar do grupo
            UsuarioGrupo usuarioGrupo = usuarioGrupoService.getByUsuarioGrupo(usuario.getId(), grupo.getId());
            if(usuarioGrupo == null)
                return ResultadoOperacao.erro("Usuario Nao Pertence ao Grupo");

            if (usuarioGrupoService.deletar(usuarioGrupo))
            {
                // avisar no grupo que o Usuário saiu
                Mensagem mensagem = new Mensagem();
                mensagem.setConteudo(usuario.getApelido()+" Saiu do Grupo!");
                enviarMensagemNoGrupo(mensagem, idUsuario, idGrupo);

                return ResultadoOperacao.sucesso(
                        "Usuario " + usuario.getApelido() + " Saiu do Grupo!",
                        new Erro("Usuario " + usuario.getApelido() + " Saiu do Grupo!")
                );
            }
            else
                return ResultadoOperacao.erro("Erro Ao Sair do Grupo!!");
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResultadoOperacao.erro("Erro!! " + e.getMessage());
        }
    }

    public List<DestinatarioMensagem> getMensagensDestinariosPendenteAndConfirmadasByUser(Long id, String status)
    {
        try {
            return destinatarioMensagemService.getMensagensDestinariosPendenteAndConfirmadasByUser(id, status);

        }
        catch (Exception e){}
        return null;
    }
}
