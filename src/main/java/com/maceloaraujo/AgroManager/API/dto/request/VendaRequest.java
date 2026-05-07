package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class VendaRequest {

    @NotNull(message = "Propriedade é obrigatório")
    private Long propriedadeId;

    private Long colheitaId;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    private String comprador;

    @NotNull(message = "Data da venda é obrigatório")
    private LocalDate dataVenda;

    @DecimalMin(value = "0")
    private BigDecimal quantidade;   // opcional

    @DecimalMin(value = "0")
    private BigDecimal precoUnitario; // opcional

    @NotNull(message = "Valor total é obrigatório")
    @DecimalMin(value = "0.01")
    private BigDecimal valorTotal;


    private String notaFiscal;


}
