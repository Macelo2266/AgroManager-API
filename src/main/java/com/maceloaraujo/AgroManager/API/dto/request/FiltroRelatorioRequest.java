package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FiltroRelatorioRequest {
    @NotNull(message = "Propriedade é obrigatória")
    private Long propriedadeId;

    @NotNull(message = "Data de início é obrigatória")
    private LocalDate dataInicio;

    @NotNull(message = "Data de fim é obrigatória")
    private LocalDate dataFim;
}
