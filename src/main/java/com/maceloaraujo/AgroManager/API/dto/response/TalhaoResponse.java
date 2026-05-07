package com.maceloaraujo.AgroManager.API.dto.response;

import com.maceloaraujo.AgroManager.API.model.TipoSolo;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TalhaoResponse {

    private Long id;
    private String nome;
    private BigDecimal areaHectares;
    private TipoSolo tipoSolo;

    private String descricao;

    private Long propriedadeId;
    private String nomePropriedade;
}
