package com.maceloaraujo.AgroManager.API.dto.response;

import com.maceloaraujo.AgroManager.API.model.PerfilUsuario;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private PerfilUsuario perfil;

    private Long propriedade;
    private String nomePropriedade;

    private LocalDateTime criadoEm;
}
