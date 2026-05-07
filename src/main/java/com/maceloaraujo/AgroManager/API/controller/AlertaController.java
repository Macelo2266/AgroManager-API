package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.response.AlertaResponse;
import com.maceloaraujo.AgroManager.API.service.AlertaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    @GetMapping("/{propriedadeId}")
    public ResponseEntity<AlertaResponse> buscar(@PathVariable Long propriedadeId) {
        return ResponseEntity.ok(alertaService.buscarAlertas(propriedadeId));
    }

}
