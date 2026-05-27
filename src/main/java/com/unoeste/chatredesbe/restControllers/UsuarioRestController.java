package com.unoeste.chatredesbe.restControllers;

import com.unoeste.chatredesbe.entities.Erro;
import com.unoeste.chatredesbe.entities.Usuario;
import com.unoeste.chatredesbe.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("apis/usuario")
@CrossOrigin
public class UsuarioRestController
{
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<Object> getAll()
    {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios.size() > 0)
            return ResponseEntity.ok(usuarios);
        else
            return ResponseEntity.badRequest().body(new Erro("Nenhum Usurio Encontrado"));
    }
    @GetMapping(value = "/getById/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id)
    {
        Usuario usuario = usuarioService.getById(id);
        if (usuario != null)
            return ResponseEntity.ok(usuario);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }
    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<Object> getByEmail(@PathVariable("email") String email)
    {
        Usuario usuario = usuarioService.getByEmail(email);
        if (usuario != null)
            return ResponseEntity.ok(usuario);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }
    @GetMapping("/getByApelido/{apelido}") // apelido
    public ResponseEntity<Object> getByApelido(@PathVariable("apelido") String apelido)
    {
        Usuario usuario = usuarioService.getByApelido(apelido);
        if (usuario != null)
            return ResponseEntity.ok(usuario);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }
    @PostMapping("/logar")
    public ResponseEntity<Object> logar(@RequestParam String login, @RequestParam String senha)
    {
        Usuario usuario = usuarioService.logar(login, senha);
        if (usuario != null)
            return ResponseEntity.ok(usuario);
        else
            return ResponseEntity.badRequest().body(new Erro("Usuario Nao Encontrado"));
    }

    @PostMapping
    public ResponseEntity<Object> salvar(@RequestBody Usuario usuario)
    {
        try
        {
            Usuario novo = usuarioService.salvar(usuario);
            return ResponseEntity.ok(novo);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new Erro(e.getMessage()));
        }
    }
    @PutMapping
    public ResponseEntity<Object> alterarStatus(@RequestBody Usuario usuario)
    {
        Usuario novoUsuario;
        novoUsuario = usuarioService.salvar(usuario);
        if (novoUsuario != null)
            return ResponseEntity.ok(novoUsuario);
        else
            return ResponseEntity.badRequest().body(new Erro("Erro ao Alterar Status"));
    }

}
