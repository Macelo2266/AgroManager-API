package com.maceloaraujo.AgroManager.API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PropriedadeResponse {
    private Long id;
    private String nome;
    private BigDecimal areaTotalHectares;

    private String localizacao;
    private String municipio;
    private String estado;

    private Integer quantidadeTalhoes;

    private LocalDateTime criadoEm;
}
