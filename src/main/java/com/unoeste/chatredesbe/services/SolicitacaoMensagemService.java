package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.ConviteGrupo;
import com.unoeste.chatredesbe.entities.SolicitacaoMensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import com.unoeste.chatredesbe.repositories.SolicitacaoMensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitacaoMensagemService
{
    @Autowired
    SolicitacaoMensagemRepository solicitacaoMensagemRepository;

    public List<SolicitacaoMensagem> getAll()
    {
        return solicitacaoMensagemRepository.findAll();
    }

    public List<SolicitacaoMensagem> getAllSolicitacoesById(Long idUsuario)
    {
        return solicitacaoMensagemRepository.getAllSolicitacoesById(idUsuario);
    }
    public List<SolicitacaoMensagem> getAllSolicitacoesByIdPendentes(Long idUsuario, String status)
    {
        return solicitacaoMensagemRepository.getAllSolicitacoesByIdPendentes(idUsuario, status);
    }

    public List<SolicitacaoMensagem> getConversas(Long idUsuario)
    {
        return solicitacaoMensagemRepository.getConversas(idUsuario);
    }

    public SolicitacaoMensagem getById(Long id)
    {
        return solicitacaoMensagemRepository.findById(id).orElse(null);
    }

    public SolicitacaoMensagem getByUsers(Usuario origem, Usuario destino)
    {
        return solicitacaoMensagemRepository.getByUsers(origem.getId(), destino.getId());
    }

    public SolicitacaoMensagem salvar(SolicitacaoMensagem solicitacaoMensagem)
    {
        try {
            SolicitacaoMensagem novaSolicitacaoMensagem = solicitacaoMensagemRepository.save(solicitacaoMensagem);
            return solicitacaoMensagemRepository.save(novaSolicitacaoMensagem);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(SolicitacaoMensagem solicitacaoMensagem)
    {
        try{
            solicitacaoMensagemRepository.delete(solicitacaoMensagem);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
