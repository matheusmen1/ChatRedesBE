package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.repositories.MensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensagemService
{
    @Autowired
    private MensagemRepository mensagemRepository;

    public List<Mensagem> getAll()
    {
        return mensagemRepository.findAll();
    }

    public List<Mensagem> getByRemetenteAll(Long id_usuario)
    {
        return mensagemRepository.getByRemetenteAll(id_usuario);
    }

    public List<Mensagem> getAllConversa(Long idOrigem, Long idDestino)
    {
        return mensagemRepository.getAllConversa(idOrigem, idDestino);
    }

    public Mensagem getById(Long id)
    {
        return mensagemRepository.findById(id).orElse(null);
    }

    public Mensagem salvar(Mensagem mensagem)
    {
        try {
            return mensagemRepository.save(mensagem);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean excluir(Long id)
    {
        try {
            mensagemRepository.deleteById(id);
            return true;
        }
        catch (Exception e){
            return false;
        }
    }


}
