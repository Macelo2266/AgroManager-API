package com.maceloaraujo.AgroManager.API.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String token;
    private String tipo;  // Bearer
    private Long expiracaoMs;
    private String email;
    private String perfil;
}

