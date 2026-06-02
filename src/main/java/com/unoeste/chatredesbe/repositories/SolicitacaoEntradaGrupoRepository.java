package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.SolicitacaoEntradaGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SolicitacaoEntradaGrupoRepository extends JpaRepository<SolicitacaoEntradaGrupo, Long>
{
    @Query(value = "SELECT * FROM solicitacao_entrada_grupo WHERE solicitante_id = :idSolicitante AND grupo_id = :idGrupo", nativeQuery = true)
    public SolicitacaoEntradaGrupo getByGrupoSolicitante(Long idGrupo, Long idSolicitante);

    @Query(value = """
        SELECT DISTINCT seg.* FROM solicitacao_entrada_grupo AS seg INNER JOIN usuario_grupo AS ug 
        ON ug.grupo_id = seg.grupo_id AND ug.usuario_id = :idUsuario AND seg.status = 'Pendente'
        """, nativeQuery = true)
    public List<SolicitacaoEntradaGrupo> getAllEntradaGrupoUserIn(Long idUsuario);
}
