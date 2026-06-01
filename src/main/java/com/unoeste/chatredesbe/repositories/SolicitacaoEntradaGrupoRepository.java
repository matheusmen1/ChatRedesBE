package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.SolicitacaoEntradaGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SolicitacaoEntradaGrupoRepository extends JpaRepository<SolicitacaoEntradaGrupo, Long>
{
    @Query(value = "SELECT * FROM solicitacao_entrada_grupo WHERE solicitante_id = :idSolicitante AND grupo_id = :idGrupo", nativeQuery = true)
    public SolicitacaoEntradaGrupo getByGrupoSolicitante(Long idGrupo, Long idSolicitante);
}
