package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.ConviteGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConviteGrupoRepository extends JpaRepository<ConviteGrupo, Long>
{
    @Query(value = "SELECT * FROM convite_grupo WHERE grupo_id = :grupo_id AND convidado_id = :convidado_id", nativeQuery = true)
    public ConviteGrupo findByIdGrupoConvidado(@Param("grupo_id") Long grupo_id, @Param("convidado_id") Long convidado_id );

    @Query(value = "SELECT * FROM convite_grupo WHERE convidado_id = :idConvidado", nativeQuery = true)
    public List<ConviteGrupo> getAllConvitesByConvidado(@Param("idConvidado") Long idConvidado);

    List<ConviteGrupo> findAllByConvidadoIdAndStatus(Long idUsuario, String status);
}
