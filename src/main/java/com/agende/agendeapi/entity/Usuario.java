package com.agende.agendeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@Audited
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", length = 80)
    private String nome;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "cpf", length = 11, unique = true)
    private String cpf;

    @Column(name = "ativo")
    private Boolean ativo;

    @Column(name = "primeiro_acesso")
    private  Boolean primeiroAcesso = Boolean.TRUE;

    @CreatedDate
    @Column(name = "dt_criacao")
    private LocalDateTime dataCriacao;

    @Transient
    private List<Cliente> clientes;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_perfil",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id")
    )
    @NotAudited
    private List<Perfil> perfis;
}
