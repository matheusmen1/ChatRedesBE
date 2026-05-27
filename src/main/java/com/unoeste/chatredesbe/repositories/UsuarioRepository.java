package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>
{
    @Query(value = "SELECT * FROM Usuario WHERE apelido = :apelido", nativeQuery = true)
    public Usuario getUsuarioByApelido(@Param("apelido") String apelido);

    @Query(value = "SELECT * FROM Usuario WHERE email = :email", nativeQuery = true)
    public Usuario getUsuarioByEmail(@Param("email") String email);
}
