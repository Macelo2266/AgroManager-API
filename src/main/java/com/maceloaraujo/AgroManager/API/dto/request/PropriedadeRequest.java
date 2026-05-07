package com.maceloaraujo.AgroManager.API.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropriedadeRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotNull(message = "Área total é obrigatória")
    @DecimalMin(value = "0.01", message = "Área deve ser positiva")
    private BigDecimal areaTotalHectares;

    private String localizacao;

    private String municipio;

    @Size(max = 2)
    private String estado;

    @Size(max = 8)
    private String cep;

    @Size(max = 14)
    private String cpfCnpjProprietario;
    
}
