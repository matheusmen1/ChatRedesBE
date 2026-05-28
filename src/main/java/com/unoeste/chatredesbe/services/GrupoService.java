package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.Grupo;
import com.unoeste.chatredesbe.repositories.GrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GrupoService {
    @Autowired
    GrupoRepository grupoRepository;

    public List<Grupo> getAll()
    {
        return grupoRepository.findAll();
    }

    public Grupo getById(Long id)
    {
        return grupoRepository.findById(id).orElse(null);
    }

    public Grupo getByName(String nome)
    {
        return grupoRepository.findByName(nome);
    }

    public Grupo salvar(Grupo grupo)
    {
        try {
            Grupo novoGrupo = grupoRepository.save(grupo);
            return grupoRepository.save(novoGrupo);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(Grupo grupo)
    {
        try{
            grupoRepository.delete(grupo);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
