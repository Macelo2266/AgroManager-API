package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.FiltroRelatorioRequest;
import com.maceloaraujo.AgroManager.API.dto.response.FluxoCaixaResponse;
import com.maceloaraujo.AgroManager.API.dto.response.RelatorioProducaoResponse;
import com.maceloaraujo.AgroManager.API.service.FinanceiroService;
import com.maceloaraujo.AgroManager.API.service.ProducaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")   // todos os relatórios só para ADMIN
public class RelatorioController {

    private final ProducaoService producaoService;
    private final FinanceiroService financeiroService;

    // ===== PRODUÇÃO =====

    @GetMapping("/producao")
    public ResponseEntity<RelatorioProducaoResponse> relatorioProducao(
            @Valid FiltroRelatorioRequest filtro) {
        return ResponseEntity.ok(producaoService.gerarRelatorio(filtro));
    }

    // ===== FINANCEIRO =====

    @GetMapping("/fluxo-caixa")
    public ResponseEntity<FluxoCaixaResponse> fluxoCaixa(
            @Valid FiltroRelatorioRequest filtro) {
        return ResponseEntity.ok(financeiroService.calcularFluxoCaixa(filtro));
    }

    @GetMapping("/despesas-por-categoria")
    public ResponseEntity<Map<String, BigDecimal>> despesasPorCategoria(
            @Valid FiltroRelatorioRequest filtro) {
        return ResponseEntity.ok(financeiroService.despesasPorCategoria(filtro));
    }

    // ===== INDICADORES GERAIS =====

    @GetMapping("/indicadores")
    public ResponseEntity<Map<String, Object>> indicadoresGerais(
            @Valid FiltroRelatorioRequest filtro) {

        RelatorioProducaoResponse producao = producaoService.gerarRelatorio(filtro);
        FluxoCaixaResponse financeiro = financeiroService.calcularFluxoCaixa(filtro);

        Map<String, Object> indicadores = Map.of(
                "totalProducaoToneladas", producao.getTotalProducaoToneladas(),
                "produtividadeMediaTonHa", producao.getProdutividadeMediaTonHa(),
                "producaoPorCultura", producao.getProducaoPorCultura(),
                "totalReceitas", financeiro.getTotalReceitas(),
                "totalDespesas", financeiro.getTotalDespesas(),
                "lucroLiquido", financeiro.getLucroLiquido(),
                "margemLucroPercentual", financeiro.getMargemLucroPercentual(),
                "totalPlantios", producao.getTotalPlantios(),
                "totalColheitas", producao.getTotalColheitas()
        );

        return ResponseEntity.ok(indicadores);
    }
}