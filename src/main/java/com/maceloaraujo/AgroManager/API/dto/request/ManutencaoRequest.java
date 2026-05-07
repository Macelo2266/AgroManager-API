package com.maceloaraujo.AgroManager.API.dto.request;

import com.maceloaraujo.AgroManager.API.model.TipoManutencao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ManutencaoRequest {

    @NotNull(message = "Máquina é obrigatória")
    private Long maquinaId;

    @NotNull(message = "Data é obrigatória")
    private LocalDate dataManutencao;

    @NotNull(message = "Tipo é obrigatório")
    private TipoManutencao tipo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private BigDecimal custo;


    @DecimalMin(value = "0")
    private BigDecimal horasMaquinaNoMomento;

    private String fornecedor;

    private LocalDate proximaManutencaoPrevista;

}