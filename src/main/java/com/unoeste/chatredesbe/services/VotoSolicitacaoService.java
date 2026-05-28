package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.VotoSolicitacao;
import com.unoeste.chatredesbe.repositories.VotoSolicitacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotoSolicitacaoService
{
    @Autowired
    VotoSolicitacaoRepository votoSolicitacaoRepository;

    public List<VotoSolicitacao> getAll()
    {
        return votoSolicitacaoRepository.findAll();
    }

    public VotoSolicitacao getById(Long id)
    {
        return votoSolicitacaoRepository.findById(id).orElse(null);
    }

    public VotoSolicitacao salvar(VotoSolicitacao votoSolicitacao)
    {
        try {
            VotoSolicitacao novoVotoSolicitacao = votoSolicitacaoRepository.save(votoSolicitacao);
            return votoSolicitacaoRepository.save(novoVotoSolicitacao);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(VotoSolicitacao votoSolicitacao)
    {
        try{
            votoSolicitacaoRepository.delete(votoSolicitacao);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
