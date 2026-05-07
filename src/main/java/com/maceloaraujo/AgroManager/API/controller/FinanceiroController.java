package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.FiltroRelatorioRequest;
import com.maceloaraujo.AgroManager.API.dto.request.DespesaRequest;
import com.maceloaraujo.AgroManager.API.dto.request.VendaRequest;
import com.maceloaraujo.AgroManager.API.dto.response.FluxoCaixaResponse;
import com.maceloaraujo.AgroManager.API.model.entity.Despesa;
import com.maceloaraujo.AgroManager.API.model.entity.Venda;
import com.maceloaraujo.AgroManager.API.service.FinanceiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/financeiro")
@RequiredArgsConstructor
public class FinanceiroController {

    private final FinanceiroService financeiroService;

    @GetMapping("/vendas")
    public ResponseEntity<Page<Venda>> listarVendas(@RequestParam Long propriedadeId, Pageable pageable) {
        return ResponseEntity.ok(financeiroService.listarVendas(propriedadeId, pageable));
    }

    @PostMapping("/vendas")
    public ResponseEntity<Venda> registrarVenda(@Valid @RequestBody VendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeiroService.registrarVenda(request));
    }

    @GetMapping("/despesas")
    public ResponseEntity<Page<Despesa>> listarDespesas(@RequestParam Long propriedadeId, Pageable pageable) {
        return ResponseEntity.ok(financeiroService.listarDespesas(propriedadeId, pageable));
    }

    @PostMapping("/despesas")
    public ResponseEntity<Despesa> registrarDespesa(@Valid @RequestBody DespesaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(financeiroService.registrarDespesa(request));
    }

    @PostMapping("/fluxo-caixa")                          // ← POST
    public ResponseEntity<FluxoCaixaResponse> fluxoCaixa(
            @Valid @RequestBody FiltroRelatorioRequest filtro) {  // ← @RequestBody
        return ResponseEntity.ok(financeiroService.calcularFluxoCaixa(filtro));
    }

    @PostMapping("/despesas-por-categoria")               // ← POST
    public ResponseEntity<Map<String, BigDecimal>> despesasPorCategoria(
            @Valid @RequestBody FiltroRelatorioRequest filtro) {  // ← @RequestBody
        return ResponseEntity.ok(financeiroService.despesasPorCategoria(filtro));
    }
}
