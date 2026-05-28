package com.unoeste.chatredesbe.services;

import com.unoeste.chatredesbe.entities.DestinatarioMensagem;
import com.unoeste.chatredesbe.repositories.DestinatarioMensagemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinatarioMensagemService {
    @Autowired
    DestinatarioMensagemRepository destinatarioMensagemRepository;

    public List<DestinatarioMensagem> getAll()
    {
        return destinatarioMensagemRepository.findAll();
    }

    public DestinatarioMensagem getById(Long id)
    {
        return destinatarioMensagemRepository.findById(id).orElse(null);
    }

    public List<DestinatarioMensagem> getByIdDestinatario(Long Id) {
        return destinatarioMensagemRepository.getByDestinatario(Id);
    }


    public DestinatarioMensagem salvar(DestinatarioMensagem destinatarioMensagem)
    {
        try {
            DestinatarioMensagem novoDestinatarioMensagem = destinatarioMensagemRepository.save(destinatarioMensagem);
            return destinatarioMensagemRepository.save(novoDestinatarioMensagem);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public boolean deletar(DestinatarioMensagem destinatarioMensagem)
    {
        try{
            destinatarioMensagemRepository.delete(destinatarioMensagem);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
