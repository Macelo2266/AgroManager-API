package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.PropriedadeRequest;
import com.maceloaraujo.AgroManager.API.dto.response.PropriedadeResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.model.entity.Propriedade;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
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
@RequestMapping("/api/propriedades")
@RequiredArgsConstructor
public class PropriedadeController {

    private final PropriedadeRepository propriedadeRepository;

    @GetMapping
    public ResponseEntity<Page<PropriedadeResponse>> listar(
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(
                propriedadeRepository.findAllByAtivoTrue(pageable)
                        .map(this::toResponse)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropriedadeResponse> buscarPorId(@PathVariable Long id) {
        Propriedade p = propriedadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", id));

        return ResponseEntity.ok(toResponse(p));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropriedadeResponse> criar(@Valid @RequestBody PropriedadeRequest request) {

        Propriedade p = Propriedade.builder()
                .nome(request.getNome())
                .areaTotalHectares(request.getAreaTotalHectares())
                .localizacao(request.getLocalizacao())
                .municipio(request.getMunicipio())
                .estado(request.getEstado())
                .cep(request.getCep())
                .cpfCnpjProprietario(request.getCpfCnpjProprietario())
                .build();

        p = propriedadeRepository.save(p);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(p));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropriedadeResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PropriedadeRequest request) {

        Propriedade p = propriedadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", id));

        p.setNome(request.getNome());
        p.setAreaTotalHectares(request.getAreaTotalHectares());
        p.setLocalizacao(request.getLocalizacao());
        p.setMunicipio(request.getMunicipio());
        p.setEstado(request.getEstado());

        return ResponseEntity.ok(toResponse(propriedadeRepository.save(p)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        Propriedade p = propriedadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", id));

        p.setAtivo(false);
        propriedadeRepository.save(p);

        return ResponseEntity.noContent().build();
    }

    private PropriedadeResponse toResponse(Propriedade p) {
        return PropriedadeResponse.builder()
                .id(p.getId())
                .nome(p.getNome())
                .areaTotalHectares(p.getAreaTotalHectares())
                .localizacao(p.getLocalizacao())
                .municipio(p.getMunicipio())
                .estado(p.getEstado())
                .quantidadeTalhoes(p.getTalhoes() != null ? p.getTalhoes().size() : 0)
                .criadoEm(p.getCriadoEm())
                .build();
    }
}
