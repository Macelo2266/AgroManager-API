package com.maceloaraujo.AgroManager.API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class FluxoCaixaResponse {

    private LocalDate dataInicio;
    private LocalDate dataFim;

    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;

    private BigDecimal lucroLiquido;
    private BigDecimal margemLucroPercentual;
}
