package com.agende.agendeapi.service;

import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class PublicSchemaMigrationService {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void migratePublicSchema() {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas("public")
                .defaultSchema("public")
                .locations("classpath:db/migration/public")
                .baselineOnMigrate(true)
                .load()
                .migrate();
    }
}
