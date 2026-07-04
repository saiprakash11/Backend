package com.ems.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final boolean permitAllApi;
    private final String swaggerDevEmployeeCode;

    public JwtAuthFilter(
            JwtUtil jwtUtil,
            @Value("${app.security.permit-all-api:false}") boolean permitAllApi,
            @Value("${app.security.swagger-dev-employee-code:EMP-ADMIN-001}") String swaggerDevEmployeeCode) {
        this.jwtUtil = jwtUtil;
        this.permitAllApi = permitAllApi;
        this.swaggerDevEmployeeCode = swaggerDevEmployeeCode;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && authHeader != null
                && authHeader.startsWith("Bearer ")) {

            try {
                String token = authHeader.substring(7);

                if (jwtUtil.isValid(token)) {

                    String employeeCode = jwtUtil.getEmployeeCode(token);
                    String role = jwtUtil.getRole(token);

                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(
                                    employeeCode,
                                    null,
                                    authoritiesForRole(role)
                            )
                    );
                }

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null
                && permitAllApi
                && request.getServletPath().startsWith("/api/")) {
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            swaggerDevEmployeeCode,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_ADMIN"),
                                    new SimpleGrantedAuthority("ROLE_HR"),
                                    new SimpleGrantedAuthority("ROLE_MANAGER"),
                                    new SimpleGrantedAuthority("ROLE_MANAGEMENT"),
                                    new SimpleGrantedAuthority("ROLE_EMPLOYEE")
                            )
                    )
            );
        }

        filterChain.doFilter(request, response);
    }

    private List<SimpleGrantedAuthority> authoritiesForRole(String role) {
        if (role == null || role.isBlank()) {
            return List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
        }

        String normalizedRole = role.trim().toUpperCase();
        if (normalizedRole.startsWith("ROLE_")) {
            normalizedRole = normalizedRole.substring(5);
        }

        if ("MANAGER".equals(normalizedRole) || "MANAGEMENT".equals(normalizedRole)) {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_MANAGER"),
                    new SimpleGrantedAuthority("ROLE_MANAGEMENT")
            );
        }

        return List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole));
    }
}
