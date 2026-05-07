package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.MovimentacaoEstoqueRequest;
import com.maceloaraujo.AgroManager.API.dto.request.ProdutoRequest;
import com.maceloaraujo.AgroManager.API.dto.response.EstoqueResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import com.maceloaraujo.AgroManager.API.model.entity.Produto;
import com.maceloaraujo.AgroManager.API.repository.ProdutoRepository;
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
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final EstoqueService estoqueService;
    private final ProdutoRepository produtoRepository;

    // ===== PRODUTOS =====

    @GetMapping
    public ResponseEntity<Page<EstoqueResponse>> listar(
            @RequestParam Long propriedadeId,
            @RequestParam(required = false) TipoProduto tipo,
            @PageableDefault(size = 20, sort = "nomeProduto") Pageable pageable) {
        return ResponseEntity.ok(
                estoqueService.listarPorPropriedade(propriedadeId, tipo, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstoqueResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(estoqueService.buscarPorProduto(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstoqueResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(estoqueService.cadastrarProduto(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequest request) {

        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));

        produto.setNome(request.getNome());
        produto.setDescricao(request.getDescricao());
        produto.setUnidadeMedida(request.getUnidadeMedida());
        produto.setEstoqueMinimo(request.getEstoqueMinimo());

        return ResponseEntity.ok(produtoRepository.save(produto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto", id));
        produto.setAtivo(false);
        produtoRepository.save(produto);
        return ResponseEntity.noContent().build();
    }

    // ===== MOVIMENTAÇÕES =====

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<EstoqueResponse> movimentar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoEstoqueRequest request) {
        request.setProdutoId(id);
        return ResponseEntity.ok(estoqueService.movimentar(request));
    }

    // ===== ALERTAS DE ESTOQUE BAIXO =====

    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<EstoqueResponse>> estoqueBaixo(
            @RequestParam Long propriedadeId) {
        return ResponseEntity.ok(estoqueService.verificarEstoqueBaixo(propriedadeId));
    }
}