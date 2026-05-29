package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long>
{
    @Query(value = "SELECT * FROM grupo WHERE nome = :nome ", nativeQuery = true)
    public Grupo findByName(@Param("nome") String nome);

    @Query(value = "SELECT g.* FROM grupo AS g INNER JOIN usuario_grupo AS ug ON g.grupo_id = u.grupo_id AND u.usuario_id = :id ", nativeQuery = true)
    List<Grupo> getAllGruposUsuario(Long id);
}
