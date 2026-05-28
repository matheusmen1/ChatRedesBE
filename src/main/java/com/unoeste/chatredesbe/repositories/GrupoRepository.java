package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GrupoRepository extends JpaRepository<Grupo, Long>
{
    @Query(value = "SELECT * FROM grupo WHERE nome = :nome ", nativeQuery = true)
    public Grupo findByName(@Param("nome") String nome);
}
