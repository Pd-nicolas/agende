package com.agende.agendeapi.config;

import com.agende.agendeapi.hibernate.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(2)
@Component
public class TenantLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        try {

            if (path.startsWith("/usuario/auth") || path.startsWith("/usuario/primeiro-acesso")) {
                TenantContext.setCurrentTenant("public");
                filterChain.doFilter(request, response);
                return;
            }

            if (path.startsWith("/v1/usuario/cadastrar")) {
                filterChain.doFilter(request, response);
                return;
            }

            // Header
            String tenantHeader = request.getHeader("X-Tenant-ID");
            if (tenantHeader != null && !tenantHeader.isBlank()) {
                TenantContext.setCurrentTenant(tenantHeader);
                logger.info("Tenant definido pelo HEADER: {}", tenantHeader);
            }
            // tenant (JWT)
            else if (TenantContext.getCurrentTenant() != null) {
                logger.info("Tenant definido pelo JWT: {}", TenantContext.getCurrentTenant());
            }
            else {
                TenantContext.setCurrentTenant("public");
                logger.info("Nenhum tenant encontrado. Usando PUBLIC.");
            }

            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
