package com.maceloaraujo.AgroManager.API.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    private List<T> conteudo;

    private int paginaAtual;
    private int tamanhoPagina;

    private long totalElementos;
    private int totalPaginas;

    private boolean ultima;
    private boolean primeira;
}
