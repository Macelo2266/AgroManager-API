package com.maceloaraujo.AgroManager.API.config;

import com.maceloaraujo.AgroManager.API.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        // Verifica se existe Bearer Token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // Remove "Bearer "
            final String token = authHeader.substring(7);

            // Extrai email do token
            final String email = jwtService.extrairEmail(token);

            // Verifica se usuário ainda não está autenticado
            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                var usuario = usuarioRepository.findByEmail(email)
                        .orElse(null);

                // Valida token
                if (usuario != null &&
                        jwtService.isTokenValido(token, usuario)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    // Autentica usuário no contexto do Spring
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);

                    log.info("Usuário autenticado: {}", email);
                }
            }

        } catch (Exception e) {

            log.error("Erro ao autenticar JWT: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}