package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.ConviteGrupo;
import com.unoeste.chatredesbe.repositories.ConviteGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConviteGrupoService
{
    @Autowired
    ConviteGrupoRepository conviteGrupoRepository;

    public List<ConviteGrupo> getAll()
    {
        return conviteGrupoRepository.findAll();
    }

    public ConviteGrupo getById(Long id)
    {
        return conviteGrupoRepository.findById(id).orElse(null);
    }

    public ConviteGrupo salvar(ConviteGrupo conviteGrupo)
    {
        try {
            ConviteGrupo novoConviteGrupo = conviteGrupoRepository.save(conviteGrupo);
            return conviteGrupoRepository.save(novoConviteGrupo);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(ConviteGrupo conviteGrupo)
    {
        try{
            conviteGrupoRepository.delete(conviteGrupo);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
