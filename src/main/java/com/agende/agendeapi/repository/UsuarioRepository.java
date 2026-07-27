package com.agende.agendeapi.repository;

import com.agende.agendeapi.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCpf(String cpf);

    @Query("SELECT DISTINCT u FROM Usuario u " +
           "LEFT JOIN FETCH u.perfis p " +
           "WHERE (:filtro IS NULL OR " +
           "LOWER(u.nome) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
           "u.cpf LIKE CONCAT('%', :filtro, '%') OR " +
           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :filtro, '%')))")
    Page<Usuario> findUsuariosFiltrados(
            @Param("filtro") String filtro,
            Pageable pageable
    );

    Optional<Usuario> findByIdAndAtivoIsTrue(Long id);

    Optional<Usuario> findByIdAndAtivoIsFalse(Long id);

    boolean existsByPerfis_Id(Long perfilId);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.perfis WHERE u.email = :email")
    Optional<Usuario> findByEmail(@Param("email") String email);

    @Query(value = "SELECT schema_name FROM usuario_tenant WHERE email = :email", nativeQuery = true)
    String findSchemaByEmail(String email);

    @Modifying
    @Query(value = "INSERT INTO usuario_tenant (email, cliente_id, schema_name) " +
           "VALUES (:email, :clienteId, :schema)", nativeQuery = true)
    void registrarUsuarioNoPublic(@Param("email") String email,
                                  @Param("clienteId") Long clienteId,
                                  @Param("schema") String schema);

}
