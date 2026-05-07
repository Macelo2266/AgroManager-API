package com.maceloaraujo.AgroManager.API.service;

import com.maceloaraujo.AgroManager.API.config.JwtService;
import com.maceloaraujo.AgroManager.API.dto.request.CadastroRequest;
import com.maceloaraujo.AgroManager.API.dto.request.LoginRequest;
import com.maceloaraujo.AgroManager.API.dto.response.TokenResponse;
import com.maceloaraujo.AgroManager.API.dto.response.UsuarioResponse;
import com.maceloaraujo.AgroManager.API.exception.RegraDeNegocioException;
import com.maceloaraujo.AgroManager.API.model.entity.Usuario;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
import com.maceloaraujo.AgroManager.API.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor

public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Realiza o login e retorna o JWT.
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        // AuthenticationManager lança BadCredentialsException se inválido
        // Que é capturada pelo GlobalExceptionHandler
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha())
        );

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        String token = jwtService.gerarToken(usuario);

        log.info("Login realizado para: {}", request.getEmail());

        return TokenResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiracaoMs(jwtService.getExpirationMs())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil().name())
                .build();
    }

    /**
     * Cadastra novo usuário. Apenas ADMIN pode cadastrar.
     */
    @Transactional
    public UsuarioResponse cadastrar(CadastroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new RegraDeNegocioException(
                    "Email '" + request.getEmail() + "' já cadastrado no sistema"
            );
        }

        var builder = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .perfil(request.getPerfil());

        if (request.getPropriedadeId() != null) {
            var propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                    .orElseThrow(() -> new RegraDeNegocioException("Propriedade não encontrada"));
            builder.propriedade(propriedade);
        }

        Usuario usuario = usuarioRepository.save(builder.build());

        log.info("Usuário cadastrado: {} ({})", usuario.getEmail(), usuario.getPerfil());

        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }

}
