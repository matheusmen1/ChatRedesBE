package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.VotoSolicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VotoSolicitacaoRepository extends JpaRepository<VotoSolicitacao, Long>
{
    @Query(value = "SELECT * FROM voto_solicitacao WHERE votante_id = :votante_id AND solicitacao_id = :solicitacao_id", nativeQuery = true)
    public VotoSolicitacao getByIdVotanteSolicitacao(@Param("votante_id") Long votante_id, @Param("solicitacao_id") Long solicitacao_id );

    @Query(value = "SELECT * FROM voto_solicitacao WHERE solicitacao_id = :solicitacao_id", nativeQuery = true)
    public List<VotoSolicitacao> getAllSolicitacao(@Param("solicitacao_id") Long solicitacao_id);
}
