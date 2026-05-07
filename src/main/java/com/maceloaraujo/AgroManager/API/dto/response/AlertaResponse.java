package com.maceloaraujo.AgroManager.API.dto.response;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AlertaResponse {
    private List<String> alertasEstoqueBaixo;
    private List<String> alertasManutencaoPendente;
    private List<String> alertasGastosElevados;

    private int totalAlertas;
}
