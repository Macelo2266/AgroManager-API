package com.maceloaraujo.AgroManager.API.dto.request;

import com.maceloaraujo.AgroManager.API.model.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MovimentacaoEstoqueRequest {


    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    @NotNull(message = "Tipo é obrigatório")
    private TipoMovimentacao tipo;

    @NotNull(message = "Quantidade é obrigatória")
    @DecimalMin(value = "0.001", message = "Quantidade deve ser positiva")
    private BigDecimal quantidade;

    @DecimalMin(value = "0")
    private BigDecimal custoUnitario;

    @NotNull(message = "Data é obrigatória")
    private LocalDate dataMovimentacao;

    private String motivo;
    private String documentoReferencia;
}
