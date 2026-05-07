package com.maceloaraujo.AgroManager.API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
public class RelatorioProducaoResponse {

    private Map<String, BigDecimal> producaoPorCultura;
    private BigDecimal totalProducaoToneladas;
    private BigDecimal produtividadeMediaTonHa;

    private int totalPlantios;
    private int totalColheitas;
}

