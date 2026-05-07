package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.CulturaRequest;  // ← import do DTO
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.exception.RegraDeNegocioException;
import com.maceloaraujo.AgroManager.API.model.entity.Cultura;
import com.maceloaraujo.AgroManager.API.repository.CulturaRepository;
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
@RequestMapping("/api/culturas")
@RequiredArgsConstructor
public class CulturaController {

    private final CulturaRepository culturaRepository;

    @GetMapping
    public ResponseEntity<Page<Cultura>> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(culturaRepository.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cultura> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                culturaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura", id))
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cultura> criar(@Valid @RequestBody CulturaRequest request) {

        if (culturaRepository.existsByNome(request.getNome())) {
            throw new RegraDeNegocioException(
                    "Cultura '" + request.getNome() + "' já cadastrada"
            );
        }

        Cultura cultura = Cultura.builder()
                .nome(request.getNome())
                .nomeCientifico(request.getNomeCientifico())
                .cicloDias(request.getCicloDias())
                .produtividadeMediaTonHa(request.getProdutividadeMediaTonHa())
                .descricao(request.getDescricao())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(culturaRepository.save(cultura));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cultura> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody CulturaRequest request) {

        Cultura cultura = culturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura", id));

        cultura.setNome(request.getNome());
        cultura.setNomeCientifico(request.getNomeCientifico());
        cultura.setCicloDias(request.getCicloDias());
        cultura.setProdutividadeMediaTonHa(request.getProdutividadeMediaTonHa());
        cultura.setDescricao(request.getDescricao());

        return ResponseEntity.ok(culturaRepository.save(cultura));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Cultura cultura = culturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura", id));
        cultura.setAtivo(false);
        culturaRepository.save(cultura);
        return ResponseEntity.noContent().build();
    }
}
