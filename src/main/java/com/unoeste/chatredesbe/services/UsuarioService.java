package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.repositories.UsuarioRepository;
import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAll()
    {
        return usuarioRepository.findAll();
    }

    public Usuario getById(Long id)
    {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario getByEmail(String email)
    {
        return usuarioRepository.getUsuarioByEmail(email);
    }

    public Usuario getByApelido(String login)
    {
        return usuarioRepository.getUsuarioByApelido(login);
    }

    public Usuario salvar(Usuario usuario)
    {
        try {
            return usuarioRepository.save(usuario);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public Usuario logar(String login, String senha)
    {
        Usuario usuario = usuarioRepository.getUsuarioByApelido(login);
        if (usuario != null)
        {
            if (usuario.getSenha().equals(senha))
                return usuario;
            else
                return null;
        }
        return usuario;
    }

    public boolean deletar(Usuario usuario)
    {
        try{
            usuarioRepository.delete(usuario);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public List<Usuario> getByStatus()
    {
        List<Usuario> usuarios = usuarioRepository.getUsuarioByStatus();
        if (!usuarios.isEmpty())
            return usuarios;
        else
            return null;
    }
}
