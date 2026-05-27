-- 1. Tabela Usuario
CREATE TABLE usuario (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    apelido VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- 2. Tabela Grupo
CREATE TABLE grupo (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL UNIQUE,
    criador_id BIGINT NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT FK_grupo_criador FOREIGN KEY (criador_id) REFERENCES usuario(id)
);

-- 3. Tabela UsuarioGrupo
CREATE TABLE usuario_grupo (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    data_entrada TIMESTAMP NOT NULL,
    CONSTRAINT FK_usuario_grupo_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id),
    CONSTRAINT FK_usuario_grupo_grupo FOREIGN KEY (grupo_id) REFERENCES grupo(id)
);

-- 4. Tabela SolicitacaoMensagem (Para chat privado)
CREATE TABLE solicitacao_mensagem (
    id BIGSERIAL PRIMARY KEY,
    usuario1_id BIGINT NOT NULL,
    usuario2_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT FK_solicitacao_msg_usuario1 FOREIGN KEY (usuario1_id) REFERENCES usuario(id),
    CONSTRAINT FK_solicitacao_msg_usuario2 FOREIGN KEY (usuario2_id) REFERENCES usuario(id)
);

-- 5. Tabela ConviteGrupo
CREATE TABLE convite_grupo (
    id BIGSERIAL PRIMARY KEY,
    solicitante_id BIGINT NOT NULL,
    convidado_id BIGINT NOT NULL,
    grupo_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT FK_convite_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuario(id),
    CONSTRAINT FK_convite_convidado FOREIGN KEY (convidado_id) REFERENCES usuario(id),
    CONSTRAINT FK_convite_grupo FOREIGN KEY (grupo_id) REFERENCES grupo(id)
);

-- 6. Tabela SolicitacaoEntradaGrupo
CREATE TABLE solicitacao_entrada_grupo (
    id BIGSERIAL PRIMARY KEY,
    grupo_id BIGINT NOT NULL,
    solicitante_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT FK_sol_entrada_grupo FOREIGN KEY (grupo_id) REFERENCES grupo(id),
    CONSTRAINT FK_sol_entrada_solicitante FOREIGN KEY (solicitante_id) REFERENCES usuario(id)
);

-- 7. Tabela VotoSolicitacao
CREATE TABLE voto_solicitacao (
    id BIGSERIAL PRIMARY KEY,
    votante_id BIGINT NOT NULL,
    solicitacao_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    CONSTRAINT FK_voto_votante FOREIGN KEY (votante_id) REFERENCES usuario(id),
    CONSTRAINT FK_voto_solicitacao FOREIGN KEY (solicitacao_id) REFERENCES solicitacao_entrada_grupo(id)
);

-- 8. Tabela Mensagem
CREATE TABLE mensagem (
    id BIGSERIAL PRIMARY KEY,
    remetente_id BIGINT NOT NULL,
    conteudo TEXT NOT NULL,
    grupo_id BIGINT NULL, -- Pode ser nulo se for mensagem direta
    data_hora_envio TIMESTAMP NOT NULL,
    CONSTRAINT FK_mensagem_remetente FOREIGN KEY (remetente_id) REFERENCES usuario(id),
    CONSTRAINT FK_mensagem_grupo FOREIGN KEY (grupo_id) REFERENCES grupo(id)
);

-- 9. Tabela DestinarioMensagem
CREATE TABLE destinario_mensagem (
    id BIGSERIAL PRIMARY KEY,
    destinatario_id BIGINT NOT NULL,
    mensagem_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    data_hora_entrega TIMESTAMP NULL, -- Inicia nulo enquanto o status for 'PENDENTE'
    CONSTRAINT FK_dest_mensagem_destinatario FOREIGN KEY (destinatario_id) REFERENCES usuario(id),
    CONSTRAINT FK_dest_mensagem_mensagem FOREIGN KEY (mensagem_id) REFERENCES mensagem(id)
);
