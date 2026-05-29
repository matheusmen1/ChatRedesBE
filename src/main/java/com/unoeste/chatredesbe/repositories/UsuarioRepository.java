package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
    @Query(value = "SELECT * FROM usuario WHERE apelido = :apelido", nativeQuery = true)
    public Usuario getUsuarioByApelido(@Param("apelido") String apelido);

    @Query(value = "SELECT * FROM usuario WHERE email = :email", nativeQuery = true)
    public Usuario getUsuarioByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM usuario WHERE status = 'online'", nativeQuery = true)
    List<Usuario> getUsuarioByStatus();
}
