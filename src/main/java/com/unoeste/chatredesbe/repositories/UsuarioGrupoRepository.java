package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.UsuarioGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, Long>
{
    @Query(value = "SELECT * FROM usuario_grupo WHERE grupo_id = :grupo_id AND usuario_id = :usuario_id", nativeQuery = true)
    public UsuarioGrupo getByUsuarioGrupo(@Param("grupo_id") Long grupo_id, @Param("usuario_id") Long usuario_id );

    @Query(value = "SELECT * FROM usuario_grupo WHERE grupo_id = :grupo_id", nativeQuery = true)
    public List<UsuarioGrupo> getAllUserByGrupo(@Param("grupo_id") Long grupo_id);

}
