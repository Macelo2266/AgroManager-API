package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.FiltroRelatorioRequest;
import com.maceloaraujo.AgroManager.API.dto.request.ColheitaRequest;
import com.maceloaraujo.AgroManager.API.dto.request.PlantioRequest;
import com.maceloaraujo.AgroManager.API.dto.response.PlantioResponse;
import com.maceloaraujo.AgroManager.API.dto.response.RelatorioProducaoResponse;
import com.maceloaraujo.AgroManager.API.model.entity.Colheita;
import com.maceloaraujo.AgroManager.API.service.ProducaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/producao")
@RequiredArgsConstructor
public class ProducaoController {

    private final ProducaoService producaoService;

    @GetMapping("/plantios")
    public ResponseEntity<Page<PlantioResponse>> listarPlantios(
            @RequestParam Long propriedadeId,
            @PageableDefault(size = 20, sort = "dataPlantio") Pageable pageable) {
        return ResponseEntity.ok(producaoService.listarPlantios(propriedadeId, pageable));
    }

    @PostMapping("/plantios")
    public ResponseEntity<PlantioResponse> registrarPlantio(@Valid @RequestBody PlantioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(producaoService.registrarPlantio(request));
    }

    @PostMapping("/colheitas")
    public ResponseEntity<Colheita> registrarColheita(@Valid @RequestBody ColheitaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(producaoService.registrarColheita(request));
    }

    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioProducaoResponse> relatorio(@Valid FiltroRelatorioRequest filtro) {
        return ResponseEntity.ok(producaoService.gerarRelatorio(filtro));
    }
}
