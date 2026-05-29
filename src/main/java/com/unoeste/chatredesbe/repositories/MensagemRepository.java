package com.unoeste.chatredesbe.repositories;

import com.unoeste.chatredesbe.entities.Mensagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensagemRepository extends JpaRepository<Mensagem, Long>
{
    @Query(value = "SELECT * FROM mensagem WHERE remetente_id = :remetente_id", nativeQuery = true)
    public List<Mensagem> getByRemetenteAll(@Param("remetente_id") Long remetente_id);

    @Query(value = """
            SELECT m.id, m.remetente_id, m.conteudo, m.grupo_id, m.data_hora_envio
            FROM mensagem AS m
            INNER JOIN destinatario_mensagem AS dm
            ON dm.destinatario_id = :idDestino AND m.remetente_id = :idOrigem
            
            UNION
            
            SELECT m.id, m.remetente_id, m.conteudo, m.grupo_id, m.data_hora_envio
            FROM mensagem AS m
            INNER JOIN destinatario_mensagem AS dm
            ON dm.destinatario_id = :idOrigem AND m.remetente_id = :idDestino
            ORDER BY data_hora_envio ASC""", nativeQuery = true)
    public List<Mensagem> getAllConversa(@Param("idOrigem") Long idOrigem, @Param("idDestino") Long idDestino);

    @Query(value = "SELECT * FROM mensagem WHERE grupo_id = :grupo_id ORDER BY data_hora_envio ASC", nativeQuery = true)
    public List<Mensagem> getAllByGrupo(@Param("grupo_id") Long grupo_id);
}
