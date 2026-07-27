package com.agende.agendeapi.config;

import com.agende.agendeapi.entity.UserRevEntity;
import com.agende.agendeapi.hibernate.TenantContext;
import com.agende.agendeapi.security.SecurityUtil;
import org.hibernate.envers.RevisionListener;

import java.util.Date;

public class UserRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {

        UserRevEntity rev = (UserRevEntity) revisionEntity;

        rev.setAtualizacaoData(new Date());

        String usuario = SecurityUtil.getCurrentUserNameSafe();
        String email   = SecurityUtil.getCurrentUserEmailSafe();

        rev.setAtualizacaoUsuario(usuario);
        rev.setAtualizacaoUsuarioEmail(email);

        String tenant = TenantContext.getCurrentTenant();

        if (tenant == null || tenant.isBlank()) {
            tenant = "public";
        }

        rev.setTenant(tenant);
    }
}
