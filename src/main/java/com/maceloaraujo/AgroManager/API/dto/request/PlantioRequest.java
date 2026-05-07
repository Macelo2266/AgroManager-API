package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PlantioRequest {

    @NotNull(message = "Talhão é obrigatório")
    private Long talhaoId;

    @NotNull(message = "Cultura é obrigatória")
    private Long culturaId;

    @NotNull(message = "Data de plantio é obrigatória")
    private LocalDate dataPlantio;

    private LocalDate dataPrevistaColheita;     // ← LocalDate, não LocalDateTime

    @NotNull(message = "Área plantada é obrigatória")
    @DecimalMin(value = "0.01")
    private BigDecimal areaPlantadaHectares;

    @DecimalMin(value = "0")
    private BigDecimal quantidadeSementeKg;     // ← estava faltando

    private String observacoes;
}
