package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.VendaRequest;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.model.entity.Venda;
import com.maceloaraujo.AgroManager.API.service.FinanceiroService;
import com.maceloaraujo.AgroManager.API.repository.VendaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final FinanceiroService financeiroService;
    private final VendaRepository vendaRepository;

    @GetMapping
    public ResponseEntity<Page<Venda>> listar(
            @RequestParam Long propriedadeId,
            @PageableDefault(size = 20, sort = "dataVenda") Pageable pageable) {
        return ResponseEntity.ok(
                vendaRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                vendaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Venda", id))
        );
    }

    @PostMapping
    public ResponseEntity<Venda> registrar(@Valid @RequestBody VendaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(financeiroService.registrarVenda(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda", id));
        venda.setAtivo(false);
        vendaRepository.save(venda);
        return ResponseEntity.noContent().build();
    }
}