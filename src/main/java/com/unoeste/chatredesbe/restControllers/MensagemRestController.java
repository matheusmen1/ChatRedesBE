package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.Erro;
import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.services.MensagemService;
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

    @GetMapping
    public ResponseEntity<Object> getAll() {
        List<Mensagem> mensagens = mensagemService.getAll();
        if (mensagens.size() > 0)
            return ResponseEntity.ok(mensagens);
        else
            return ResponseEntity.badRequest().body(new Erro("Nenhuma mensagem encontrada!!"));
    }
    @GetMapping("/getAllByRemetente/{id}")
    public ResponseEntity<Object> getAllByRemetente() {
        List<Mensagem> mensagens = mensagemService.getAllByRemetente();
        if (mensagens.size() > 0)
            return ResponseEntity.ok(mensagens);
        else
            return ResponseEntity.badRequest().body(new Erro("Nenhuma Mensagem para esse remetente encontrada!!"));
    }

    @GetMapping(value = "/getById/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        Mensagem mensagem = mensagemService.getById(id);
        if (mensagem != null)
            return ResponseEntity.ok(mensagem);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }

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
    public ResponseEntity<Object> alterarStatus(@RequestBody Mensagem mensagem) {
        Mensagem novaMensagem = mensagemService.salvar(usuario);
        if (novaMensagem != null)
            return ResponseEntity.ok(novaMensagem);
        else
            return ResponseEntity.badRequest().body(new Erro("Erro ao Alterar Status"));
    }
}
