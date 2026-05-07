package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.MovimentacaoEstoqueRequest;
import com.maceloaraujo.AgroManager.API.dto.request.ProdutoRequest;
import com.maceloaraujo.AgroManager.API.dto.response.EstoqueResponse;
import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import com.maceloaraujo.AgroManager.API.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    @GetMapping
    public ResponseEntity<Page<EstoqueResponse>> listar(
            @RequestParam Long propriedadeId,
            @RequestParam(required = false) TipoProduto tipo,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(estoqueService.listarPorPropriedade(propriedadeId, tipo, pageable));
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<EstoqueResponse> buscarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(estoqueService.buscarPorProduto(produtoId));
    }

    @GetMapping("/alertas")
    public ResponseEntity<List<EstoqueResponse>> alertas(@RequestParam Long propriedadeId) {
        return ResponseEntity.ok(estoqueService.verificarEstoqueBaixo(propriedadeId));
    }

    @PostMapping("/produtos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstoqueResponse> cadastrar(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estoqueService.cadastrarProduto(request));
    }

    @PostMapping("/movimentacoes")
    public ResponseEntity<EstoqueResponse> movimentar(@Valid @RequestBody MovimentacaoEstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.movimentar(request));
    }
}
