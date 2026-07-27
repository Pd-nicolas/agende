-- NÃO QUALIFICAR SCHEMA AQUI
create sequence if not exists revinfo_seq start with 1 increment by 1;

create table if not exists revinfo (
                                       rev bigint not null default nextval('revinfo_seq'),
    atualizacao_data timestamp,
    atualizacao_usuario varchar(255),
    atualizacao_usuario_email varchar(255),
    tenant varchar(255),
    constraint pk_revinfo primary key (rev)
    );