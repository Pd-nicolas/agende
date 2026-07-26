package com.agende.agendeapi.hibernate;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {
    private final DataSource dataSource;

    public MultiTenantConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        final Connection connection = dataSource.getConnection();
        try (Statement st = connection.createStatement()) {
            // Fallback global
            st.execute("set search_path to public, public_audit, public");
        }
        return connection;
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        final Connection connection = dataSource.getConnection();
        try (Statement st = connection.createStatement()) {
            final String tenant = (tenantIdentifier == null || tenantIdentifier.isBlank())
                    ? "public" : tenantIdentifier.toLowerCase();

            // Define ordem de resolução: primeiro o schema do tenant, depois o audit do tenant, depois public
            final String auditSchema = tenant + "_audit";
            st.execute("set search_path to " + tenant + ", " + auditSchema + ", public");
        }
        return connection;
    }





    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        try (Statement st = connection.createStatement()) {
            // Restaura para algo neutro ao devolver pro pool
            st.execute("set search_path to public");
        } finally {
            connection.close();
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.equals(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType)) {
            return (T) this;
        }
        return null;
    }
}
