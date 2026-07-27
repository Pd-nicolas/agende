package com.agende.agendeapi.service;

import jakarta.transaction.Transactional;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Service
public class TenantService {

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class);

    private final DataSource dataSource;
    private final ClienteService clienteService;

    public TenantService(DataSource dataSource, @Lazy ClienteService clienteService) {
        this.dataSource = dataSource;
        this.clienteService = clienteService;
    }

    private void createSchema(String schemaName) {
        try (Connection c = dataSource.getConnection();
             Statement st = c.createStatement()) {
            st.execute("create schema if not exists " + schemaName);
            st.execute("create schema if not exists " + schemaName + "_audit");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createAndMigrateTenant(String schemaName) {
        createSchema(schemaName);

        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .locations("classpath:db/migration/tenant")
                .baselineOnMigrate(true)
                .load()
                .migrate();

        Flyway.configure()
                .dataSource(dataSource)
                .schemas(schemaName + "_audit")
                .defaultSchema(schemaName + "_audit")
                .locations("classpath:db/migration/audit")
                .baselineOnMigrate(true)
                .load()
                .migrate();
    }

    @Transactional
    public void migrateAllTenants() {

        clienteService.findAllAtivos().forEach(cliente -> {

            String schema = cliente.getSchema();

            logger.info("Migrando tenant: {}", schema);

            migrateSchema(schema, "classpath:db/migration/tenant");
            migrateSchema(schema + "_audit", "classpath:db/migration/audit");
        });
    }

    private void migrateSchema(String schema, String location) {

        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .schemas(schema)
                .defaultSchema(schema)
                .locations(location)
                .baselineOnMigrate(true)
                .load();

        flyway.migrate();
    }

}
