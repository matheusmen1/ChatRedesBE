package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.DestinatarioMensagem;
import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.entities.SolicitacaoMensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinatarioMensagemRepository extends JpaRepository<DestinatarioMensagem, Long>
{
    @Query(value = "SELECT * FROM destinatario_mensagem WHERE destinatario_id = :id", nativeQuery = true)
    public List<DestinatarioMensagem> getByDestinatario(@Param("id") Long id);

    @Query(value = "SELECT * FROM solicitacao_mensagem WHERE usuario1_id = :origem AND usuario2_id = :destino", nativeQuery = true)
    public DestinatarioMensagem getByMensagemAndDestinatario();

    DestinatarioMensagem findByMensagemAndDestinatario(Mensagem mensagem, Usuario destino);

    List<DestinatarioMensagem> findAllByDestinatarioIdAndStatus(Long idDestinatario, String status);
}
