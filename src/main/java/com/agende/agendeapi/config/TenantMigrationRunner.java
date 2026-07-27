package com.agende.agendeapi.config;

import com.agende.agendeapi.service.TenantService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class TenantMigrationRunner implements ApplicationRunner {

    private final TenantService tenantService;

    public TenantMigrationRunner(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @Override
    public void run(ApplicationArguments args) {
        tenantService.migrateAllTenants();
    }
}
