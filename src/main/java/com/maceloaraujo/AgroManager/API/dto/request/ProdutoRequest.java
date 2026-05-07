package com.maceloaraujo.AgroManager.API.dto.request;

import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdutoRequest {

    @NotBlank(message = "Nome é obrigatório")   // ← String = @NotBlank ✅
    private String nome;

    private String descricao;

    @NotNull(message = "Tipo é obrigatório")    // ← Enum = @NotNull ✅
    private TipoProduto tipo;

    @NotBlank(message = "Unidade é obrigatória") // ← String = @NotBlank ✅
    private String unidadeMedida;

    @DecimalMin(value = "0")
    private BigDecimal estoqueMinimo;            // ← opcional, sem @Not

    @NotNull(message = "Propriedade é obrigatória") // ← Long = @NotNull ✅
    private Long propriedadeId;
}