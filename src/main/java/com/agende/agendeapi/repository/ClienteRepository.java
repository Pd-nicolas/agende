package com.agende.agendeapi.repository;

import com.agende.agendeapi.entity.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsBySchema(String schema);


    Optional<Cliente> findBySchema(String schema);

    @Query("""
    SELECT DISTINCT c FROM Cliente c
    WHERE (
        :filtro IS NULL OR
        LOWER(c.nome) LIKE LOWER(CONCAT('%', :filtro, '%'))
    )
    """)
    Page<Cliente> findClientesFiltrados(
            @Param("filtro") String filtro,
            Pageable pageable
    );

    List<Cliente> findAllByAtivoIsTrue();
}
