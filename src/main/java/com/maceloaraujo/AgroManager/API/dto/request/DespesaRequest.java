package com.maceloaraujo.AgroManager.API.dto.request;

import com.maceloaraujo.AgroManager.API.model.CategoriaDespesa;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DespesaRequest {

    @NotNull(message = "Propriedade é obrigatória")
    private Long propriedadeId;

    @NotNull(message = "Categoria é obrigatória")
    private CategoriaDespesa categoria;

    @NotNull(message = "Valor é obrigatório")
    private BigDecimal valor;

    @NotNull(message = "Data é obrigatória")
    private LocalDate dataDespesa;

    @NotNull(message = "Fornecedor é obrigatória")
    private String fornecedor;

    private String notaFiscal;

    private String observacoes;
    private String descricao;
}
