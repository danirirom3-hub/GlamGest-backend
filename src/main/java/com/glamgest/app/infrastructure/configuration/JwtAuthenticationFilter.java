package com.glamgest.app.infrastructure.configuration;

import com.glamgest.app.application.service.auth.CustomUserDetailsService;
import com.glamgest.app.application.service.auth.JwtService;
import com.glamgest.app.application.service.auth.PolicyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.JwtException;
import com.glamgest.app.domain.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final PolicyService policyService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService,
            PolicyService policyService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.policyService = policyService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        try {
            userEmail = jwtService.extractUsername(jwt);
            String sessionId = jwtService.extractSessionId(jwt);
            boolean activeSession = sessionId != null && userRepository.findByEmail(userEmail)
                    .map(user -> sessionId.equals(user.getActiveSessionId()))
                    .orElse(false);
            if (!activeSession) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":\"SESSION_REPLACED\",\"message\":\"La sesión ya no está activa.\"}");
                return;
            }
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

                if (!request.getRequestURI().equals("/api/auth/policy")
                        && !request.getRequestURI().startsWith("/api/auth/")
                        && !policyService.isAccepted(userEmail)) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                            "Debe aceptar la política de tratamiento de datos");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
