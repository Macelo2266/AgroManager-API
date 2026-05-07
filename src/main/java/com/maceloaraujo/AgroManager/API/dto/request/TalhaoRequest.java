package com.maceloaraujo.AgroManager.API.dto.request;

import com.maceloaraujo.AgroManager.API.model.TipoSolo;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TalhaoRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Área é obrigatória")
    @DecimalMin(value = "0.01", message = "Área deve ser positiva")
    private BigDecimal areaHectares;            // ← getter gerado pelo @Data

    private TipoSolo tipoSolo;

    private String descricao;

    @NotNull(message = "Propriedade é obrigatória")
    private Long propriedadeId;
}
