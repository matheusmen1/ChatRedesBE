package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.UsuarioGrupo;
import com.unoeste.chatredesbe.repositories.UsuarioGrupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioGrupoService
{
    @Autowired
    UsuarioGrupoRepository usuarioGrupoRepository;

    public List<UsuarioGrupo> getAll()
    {
        return usuarioGrupoRepository.findAll();
    }

    public UsuarioGrupo getById(Long id)
    {
        return usuarioGrupoRepository.findById(id).orElse(null);
    }

    public UsuarioGrupo salvar(UsuarioGrupo usuarioGrupo)
    {
        try {
            UsuarioGrupo novoUsuarioGrupo = usuarioGrupoRepository.save(usuarioGrupo);
            return usuarioGrupoRepository.save(novoUsuarioGrupo);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(UsuarioGrupo usuarioGrupo)
    {
        try{
            usuarioGrupoRepository.delete(usuarioGrupo);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
