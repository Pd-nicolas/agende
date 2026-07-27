ALTER TABLE public.usuario
    ADD CONSTRAINT uq_usuario_email UNIQUE (email);

