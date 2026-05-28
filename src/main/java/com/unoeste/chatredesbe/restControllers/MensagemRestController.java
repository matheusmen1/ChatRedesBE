package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.DestinatarioMensagem;
import com.unoeste.chatredesbe.entities.Erro;
import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import com.unoeste.chatredesbe.services.DestinatarioMensagemService;
import com.unoeste.chatredesbe.services.MensagemService;
import com.unoeste.chatredesbe.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apis/mensagem")
@CrossOrigin
public class MensagemRestController {
    @Autowired
    private MensagemService mensagemService;
    @Autowired
    private DestinatarioMensagemService destinatarioMensagemService;
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Mensagem> mensagens = mensagemService.getAll();
        if (mensagens.size() > 0)
            return ResponseEntity.ok(mensagens);
        else
            return ResponseEntity.badRequest().body(new Erro("Nenhuma mensagem encontrada!!"));
    }

    @GetMapping("/getAllByRemetente/{login}")
    public ResponseEntity<Object> getByRemetenteAll(@PathVariable String login) {
        Usuario usuario = usuarioService.getByApelido(login);
        if (usuario != null)
        {
            List<Mensagem> mensagens = mensagemService.getByRemetenteAll(usuario.getId());
            if (mensagens.size() > 0)
                return ResponseEntity.ok(mensagens);
            else
                return ResponseEntity.badRequest().body(new Erro("Nenhuma Mensagem para esse remetente encontrada!!"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new Erro("Esse remetente não existe!!"));
        }
    }

    @GetMapping(value = "/getById/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        Mensagem mensagem = mensagemService.getById(id);
        if (mensagem != null)
            return ResponseEntity.ok(mensagem);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }

    // enviar uma mensagem
    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Mensagem mensagem) {
        try {
            Mensagem nova = mensagemService.salvar(mensagem);
            return ResponseEntity.ok(nova);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Erro(e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Object> alterarMensagem(@RequestBody Mensagem mensagem)
    {
        Mensagem novaMensagem = mensagemService.salvar(mensagem);
        if (novaMensagem != null)
            return ResponseEntity.ok(novaMensagem);
        else
            return ResponseEntity.badRequest().body(new Erro("Erro ao Alterar Mensagem"));
    }

    @DeleteMapping
    public ResponseEntity<Object> excluirMensagem(@PathVariable Long id)
    {
        if(mensagemService.excluir(id))
        {
            return ResponseEntity.ok(new Erro("Mensagem excluída com sucesso!"));
        }
        else
        {
            return ResponseEntity.badRequest().body(new Erro("Erro ao Excluir Mensagem"));
        }
    }
}
