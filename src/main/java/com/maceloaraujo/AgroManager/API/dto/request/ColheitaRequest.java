package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ColheitaRequest {

    @NotNull(message = "Plantio é obrigatório")
    private Long plantioId;

    @NotNull(message = "Data de colheita é obrigatória")
    private LocalDate dataColheita;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.001")
    private BigDecimal quantidadeToneladas;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100")
    private BigDecimal umidadePercentual;       // opcional

    @DecimalMin(value = "0")
    private BigDecimal precoVendaTon;           // ← estava faltando

    private String observacoes;
}