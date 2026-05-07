package com.maceloaraujo.AgroManager.API.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Erros de validação do Bean Validation (@NotNull, @NotBlank, etc.)
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)    // ← era ResourceNotFoundException (errado)
    public ResponseEntity<ErroResponse> handleValidationErros(  // ← era ErrorResponse (interface do Spring)
                                                                MethodArgumentNotValidException ex,
                                                                HttpServletRequest request) {

        Map<String, String> errosCampos = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String campo = error.getField();                    // ← cast desnecessário removido
            String mensagem = error.getDefaultMessage();
            errosCampos.put(campo, mensagem);
        });

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .erro("Erro de Validação")
                .mensagem("Campos inválidos na requisição")
                .caminho(request.getRequestURI())
                .errosCampos(errosCampos)
                .build();

        log.warn("Erro de validação em {}: {}", request.getRequestURI(), errosCampos);
        return ResponseEntity.badRequest().body(erro);
    }

    /**
     * Recurso não encontrado
     * HTTP 404 - Not Found
     */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> handleRecursoNaoEncontrado( // ← era ErrorResponse (interface do Spring)
                                                                    RecursoNaoEncontradoException ex,
                                                                    HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .erro("Recurso Não Encontrado")
                .mensagem(ex.getMessage())
                .caminho(request.getRequestURI())
                .build();

        log.warn("Recurso não encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
    }

    /**
     * Regras de negócio violadas
     * HTTP 422 - Unprocessable Entity
     */
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResponse> handleRegraDeNegocio(
            RegraDeNegocioException ex,
            HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .erro("Regra de Negócio")
                .mensagem(ex.getMessage())
                .caminho(request.getRequestURI())
                .build();

        log.warn("Regra de negócio violada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
    }

    /**
     * Credenciais inválidas no login
     * HTTP 401 - Unauthorized
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResponse> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .erro("Não Autorizado")
                .mensagem("Email ou senha inválidos")
                .caminho(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(erro);
    }

    /**
     * Acesso negado (sem permissão de perfil)
     * HTTP 403 - Forbidden
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .erro("Acesso Negado")
                .mensagem("Você não tem permissão para acessar este recurso")
                .caminho(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(erro);
    }

    /**
     * Estoque insuficiente e outros conflitos de estado
     * HTTP 409 - Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErroResponse> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .erro("Conflito de Estado")
                .mensagem(ex.getMessage())
                .caminho(request.getRequestURI())
                .build();

        log.warn("Estado ilegal: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
    }

    /**
     * Catch-all para exceções não esperadas
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        ErroResponse erro = ErroResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .erro("Erro Interno")
                .mensagem("Ocorreu um erro inesperado. Por favor, tente novamente.")
                .caminho(request.getRequestURI())
                .build();

        log.error("Erro inesperado em {}: ", request.getRequestURI(), ex);
        return ResponseEntity.internalServerError().body(erro);
    }
}
