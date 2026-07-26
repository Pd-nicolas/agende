package com.agende.agendeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Audited;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@Audited
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "codigo")
    private String codigo;

    @Column(name = "schema")
    private String schema;

    @Column(name = "descricao_empresa")
    private String descricaoEmpresa;

    @Column(name = "ativo")
    private Boolean ativo = Boolean.TRUE;

}
