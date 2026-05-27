-- 1. Tabela Usuario
CREATE TABLE Usuario (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(255) NOT NULL,
                         apelido VARCHAR(100) NOT NULL UNIQUE,
                         email VARCHAR(255) NOT NULL UNIQUE,
                         senha VARCHAR(255) NOT NULL,
                         status VARCHAR(50) NOT NULL
);

-- 2. Tabela Grupo
CREATE TABLE Grupo (
                       id BIGSERIAL PRIMARY KEY,
                       nome VARCHAR(150) NOT NULL UNIQUE,
                       criador_id BIGINT NOT NULL,
                       dataCriacao TIMESTAMP NOT NULL,
                       CONSTRAINT FK_Grupo_Criador FOREIGN KEY (criador_id) REFERENCES Usuario(id)
);

-- 3. Tabela UsuarioGrupo
CREATE TABLE UsuarioGrupo (
                              id BIGSERIAL PRIMARY KEY,
                              usuario_id BIGINT NOT NULL,
                              grupo_id BIGINT NOT NULL,
                              dataEntrada TIMESTAMP NOT NULL,
                              CONSTRAINT FK_UsuarioGrupo_Usuario FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
                              CONSTRAINT FK_UsuarioGrupo_Grupo FOREIGN KEY (grupo_id) REFERENCES Grupo(id)
);

-- 4. Tabela SolicitacaoMensagem (Para chat privado)
CREATE TABLE SolicitacaoMensagem (
                                     id BIGSERIAL PRIMARY KEY,
                                     usuario1_id BIGINT NOT NULL,
                                     usuario2_id BIGINT NOT NULL,
                                     status VARCHAR(50) NOT NULL,
                                     CONSTRAINT FK_SolicitacaoMsg_Usuario1 FOREIGN KEY (usuario1_id) REFERENCES Usuario(id),
                                     CONSTRAINT FK_SolicitacaoMsg_Usuario2 FOREIGN KEY (usuario2_id) REFERENCES Usuario(id)
);

-- 5. Tabela ConviteGrupo
CREATE TABLE ConviteGrupo (
                              id BIGSERIAL PRIMARY KEY,
                              solicitante_id BIGINT NOT NULL,
                              convidado_id BIGINT NOT NULL,
                              grupo_id BIGINT NOT NULL,
                              status VARCHAR(50) NOT NULL,
                              CONSTRAINT FK_Convite_Solicitante FOREIGN KEY (solicitante_id) REFERENCES Usuario(id),
                              CONSTRAINT FK_Convite_Convidado FOREIGN KEY (convidado_id) REFERENCES Usuario(id),
                              CONSTRAINT FK_Convite_Grupo FOREIGN KEY (grupo_id) REFERENCES Grupo(id)
);

-- 6. Tabela SolicitacaoEntradaGrupo
CREATE TABLE SolicitacaoEntradaGrupo (
                                         id BIGSERIAL PRIMARY KEY,
                                         grupo_id BIGINT NOT NULL,
                                         solicitante_id BIGINT NOT NULL,
                                         status VARCHAR(50) NOT NULL,
                                         CONSTRAINT FK_SolEntrada_Grupo FOREIGN KEY (grupo_id) REFERENCES Grupo(id),
                                         CONSTRAINT FK_SolEntrada_Solicitante FOREIGN KEY (solicitante_id) REFERENCES Usuario(id)
);

-- 7. Tabela VotoSolicitacao
CREATE TABLE VotoSolicitacao (
                                 id BIGSERIAL PRIMARY KEY,
                                 votante_id BIGINT NOT NULL,
                                 solicitacao_id BIGINT NOT NULL,
                                 status VARCHAR(50) NOT NULL,
                                 CONSTRAINT FK_Voto_Votante FOREIGN KEY (votante_id) REFERENCES Usuario(id),
                                 CONSTRAINT FK_Voto_Solicitacao FOREIGN KEY (solicitacao_id) REFERENCES SolicitacaoEntradaGrupo(id)
);

-- 8. Tabela Mensagem
CREATE TABLE Mensagem (
                          id BIGSERIAL PRIMARY KEY,
                          remetente_id BIGINT NOT NULL,
                          conteudo TEXT NOT NULL,
                          grupo_id BIGINT NULL, -- Pode ser nulo se for mensagem direta
                          dataHoraEnvio TIMESTAMP NOT NULL,
                          CONSTRAINT FK_Mensagem_Remetente FOREIGN KEY (remetente_id) REFERENCES Usuario(id),
                          CONSTRAINT FK_Mensagem_Grupo FOREIGN KEY (grupo_id) REFERENCES Grupo(id)
);

-- 9. Tabela DestinarioMensagem
CREATE TABLE DestinarioMensagem (
                                    id BIGSERIAL PRIMARY KEY,
                                    destinatario_id BIGINT NOT NULL,
                                    mensagem_id BIGINT NOT NULL,
                                    status VARCHAR(50) NOT NULL,
                                    dataHoraEntrega TIMESTAMP NULL, -- Inicia nulo enquanto o status for 'PENDENTE'
                                    CONSTRAINT FK_DestMensagem_Destinatario FOREIGN KEY (destinatario_id) REFERENCES Usuario(id),
                                    CONSTRAINT FK_DestMensagem_Mensagem FOREIGN KEY (mensagem_id) REFERENCES Mensagem(id)
);