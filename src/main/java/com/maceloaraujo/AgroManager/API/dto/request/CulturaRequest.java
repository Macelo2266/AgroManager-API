package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CulturaRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String nomeCientifico;

    @Positive(message = "Ciclo deve ser positivo")
    private Integer cicloDias;

    @DecimalMin(value = "0.01", message = "Produtividade deve ser positiva")
    private Double produtividadeMediaTonHa;

    private String descricao;
}