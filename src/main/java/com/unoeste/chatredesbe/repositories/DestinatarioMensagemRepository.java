package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.DestinatarioMensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DestinatarioMensagemRepository extends JpaRepository<DestinatarioMensagem, Long>
{
    @Query(value = "SELECT * FROM destinatario_mensagem WHERE destinatario_id = :id", nativeQuery = true)
    public List<DestinatarioMensagem> getByDestinatario(@Param("id") Long id);
}
