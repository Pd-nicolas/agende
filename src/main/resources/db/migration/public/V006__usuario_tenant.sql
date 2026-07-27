CREATE TABLE IF NOT EXISTS public.usuario_tenant (
     id          BIGSERIAL PRIMARY KEY,
     email       VARCHAR(255) NOT NULL UNIQUE,
    cliente_id  BIGINT       NOT NULL,
    schema_name VARCHAR(100) NOT NULL
);
