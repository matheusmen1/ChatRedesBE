package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.SolicitacaoEntradaGrupo;
import com.unoeste.chatredesbe.repositories.SolicitacaoEntradaGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolicitacaoEntradaGrupoService {
    @Autowired
    SolicitacaoEntradaGrupoRepository solicitacaoEntradaGrupoRepository;

    public List<SolicitacaoEntradaGrupo> getAll()
    {
        return solicitacaoEntradaGrupoRepository.findAll();
    }

    public SolicitacaoEntradaGrupo getById(Long id)
    {
        return solicitacaoEntradaGrupoRepository.findById(id).orElse(null);
    }

    public SolicitacaoEntradaGrupo salvar(SolicitacaoEntradaGrupo solicitacaoEntradaGrupo)
    {
        try {
            SolicitacaoEntradaGrupo novaSolicitacaoEntradaGrupo = solicitacaoEntradaGrupoRepository.save(solicitacaoEntradaGrupo);
            return solicitacaoEntradaGrupoRepository.save(novaSolicitacaoEntradaGrupo);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(SolicitacaoEntradaGrupo solicitacaoEntradaGrupo)
    {
        try{
            solicitacaoEntradaGrupoRepository.delete(solicitacaoEntradaGrupo);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
