package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.SolicitacaoMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SolicitacaoMensagemRepository extends JpaRepository<SolicitacaoMensagem, Long>
{
    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario1_id = :origem AND usuario2_id = :destino", nativeQuery = true)
    public SolicitacaoMensagem getByUsers(@Param("origem") Long origem, @Param("destino") Long destino);

    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario1_id = :idUsuario OR usuario2_id = :idUsuario", nativeQuery = true)
    public List<SolicitacaoMensagem> getAllSolicitacoesById(@Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario2_id = :idUsuario AND status = 'Confirmada'", nativeQuery = true)
    public List<SolicitacaoMensagem> getConversas(@Param("idUsuario") Long idUsuario);

    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario2_id = :idUsuario AND status = :status", nativeQuery = true)
    public List<SolicitacaoMensagem> getAllSolicitacoesByIdPendentes(@Param("idUsuario") Long idUsuario, @Param("status") String status);
}
