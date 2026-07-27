-- NÃO QUALIFICAR SCHEMA AQUI
-- Esse script será executado em:
--  - public_audit
--  - <tenant>_audit (para cada cliente novo)

create table if not exists usuario_aud (
    id            bigint       not null,
    rev           bigint       not null,
    revtype       smallint,
    nome        VARCHAR(80),
    email       VARCHAR(255),
    senha       VARCHAR(255),
    cpf         VARCHAR(11),
    ativo       BOOLEAN,
    primeiro_acesso       BOOLEAN,
    dt_criacao  TIMESTAMP,
    constraint pk_usuario_aud primary key (id, rev)
    );

alter table usuario_aud
    add constraint fk_usuario_aud_on_rev
        foreign key (rev) references revinfo (rev);

CREATE TABLE cliente_aud
(
    rev               BIGINT NOT NULL,
    revtype           SMALLINT,
    id                BIGINT NOT NULL,
    nome              VARCHAR(255),
    codigo            VARCHAR(255),
    schema            VARCHAR(255),
    descricao_empresa VARCHAR(255),
    ativo boolean,
    CONSTRAINT pk_cliente_aud PRIMARY KEY (rev, id)
);

ALTER TABLE cliente_aud
    ADD CONSTRAINT FK_CLIENTE_AUD_ON_REV FOREIGN KEY (rev) REFERENCES revinfo (rev);
