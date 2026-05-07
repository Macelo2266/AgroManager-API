package com.maceloaraujo.AgroManager.API.dto.response;

import com.maceloaraujo.AgroManager.API.model.StatusPlantio;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class PlantioResponse {
    private Long id;

    private Long talhaoId;
    private String nomeTalhao;

    private Long culturaId;
    private String nomeCultura;

    private LocalDate dataPlantio;
    private LocalDate dataPrevistaColheita;

    private BigDecimal areaPlantadaHectares;

    private StatusPlantio status;

    private BigDecimal produtividadeAtualTonHa;

    private String observacoes;
}
