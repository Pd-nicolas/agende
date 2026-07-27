package com.agende.agendeapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "perfil")
@Setter
@Getter
public class Perfil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @ManyToMany(mappedBy = "perfis")
    private List<Usuario> usuarios;


    public Perfil() {}

    public Perfil(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

}
