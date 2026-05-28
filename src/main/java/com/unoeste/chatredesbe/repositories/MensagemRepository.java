package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long>
{
    @Query(value = "SELECT * FROM mensagem WHERE remetente_id = :remetente_id", nativeQuery = true)
    public List<Mensagem> getByRemetenteAll(@Param("remetente_id") Long remetente_id);
}
