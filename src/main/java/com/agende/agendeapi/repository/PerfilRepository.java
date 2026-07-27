package com.agende.agendeapi.repository;

import com.agende.agendeapi.entity.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {

    Optional<Perfil> findByDescricao(String descricao);
}
