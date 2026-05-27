package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Mensagem;
import com.unoeste.chatredesbe.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MensagemRepository extends JpaRepository<Mensagem, Long>
{
    @Query(value = "SELECT * FROM mensagem WHERE remetente_id = :remetente_id", nativeQuery = true)
    public Usuario getMensagemByRemetente(@Param("remetente_id") Long remetente_id);

    @Query(value = "SELECT * FROM mensagem WHERE email = :email", nativeQuery = true)
    public Usuario getUsuarioByEmail(@Param("email") String email);
}
