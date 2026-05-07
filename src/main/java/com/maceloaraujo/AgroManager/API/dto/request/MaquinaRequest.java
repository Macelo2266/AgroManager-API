package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MaquinaRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Propriedade é obrigatória")
    private Long propriedadeId;

    private String modelo;
    private String fabricante;

    private String numeroSerie;

    @Min(value = 1900, message = "Ano inválido")
    private Integer anoFabricacao;

    private Integer intervaloManutencaoHoras;
    private LocalDate proximaManutencaoPrevista;
}
