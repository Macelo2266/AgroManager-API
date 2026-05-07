package com.maceloaraujo.AgroManager.API.dto.response;

import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class EstoqueResponse {
    private Long produtoId;
    private String nomeProduto;

    private TipoProduto tipoProduto;
    private String unidadeMedida;

    private BigDecimal quantidade;
    private BigDecimal custoMedio;
    private BigDecimal valorTotalEstoque;

    private BigDecimal estoqueMinimo;
    private boolean estoqueBaixo;
}
