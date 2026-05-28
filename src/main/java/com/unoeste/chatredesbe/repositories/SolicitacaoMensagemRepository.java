package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.SolicitacaoMensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitacaoMensagemRepository extends JpaRepository<SolicitacaoMensagem, Long>
{
    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario1_id = :origem AND usuario2_id = :destino", nativeQuery = true)
    public SolicitacaoMensagem getByUsers(@Param("origem") Long origem, @Param("destino") Long destino);
}
