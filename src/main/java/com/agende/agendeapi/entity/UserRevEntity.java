package com.agende.agendeapi.entity;

import com.agende.agendeapi.config.UserRevisionListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.util.Date;

@Table(name = "revinfo")
@Entity
@RevisionEntity(UserRevisionListener.class)
@Getter
@Setter
public class UserRevEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revinfo_seq")
    @SequenceGenerator(name = "revinfo_seq", allocationSize = 1, sequenceName = "revinfo_seq")
    @RevisionNumber
    private Long rev;

    @RevisionTimestamp
    @Column(name = "atualizacao_data")
    private Date atualizacaoData;

    @Column(name = "atualizacao_usuario")
    private String atualizacaoUsuario;

    @Column(name = "atualizacao_usuario_email")
    private String atualizacaoUsuarioEmail;

    @Column(name = "tenant")
    private String tenant;

}
