package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.TalhaoRequest;
import com.maceloaraujo.AgroManager.API.dto.response.TalhaoResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.exception.RegraDeNegocioException;
import com.maceloaraujo.AgroManager.API.model.entity.Propriedade;
import com.maceloaraujo.AgroManager.API.model.entity.Talhao;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
import com.maceloaraujo.AgroManager.API.repository.TalhaoRepository;
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
@RequestMapping("/api/talhoes")
@RequiredArgsConstructor
public class TalhaoController {

    private final TalhaoRepository talhaoRepository;
    private final PropriedadeRepository propriedadeRepository;

    // ===== LISTAR (paginado) =====

    @GetMapping
    public ResponseEntity<Page<TalhaoResponse>> listar(
            @RequestParam Long propriedadeId,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {

        return ResponseEntity.ok(
                talhaoRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable)
                        .map(this::toResponse)
        );
    }

    // ===== BUSCAR POR ID =====

    @GetMapping("/{id}")
    public ResponseEntity<TalhaoResponse> buscarPorId(@PathVariable Long id) {
        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão", id));

        return ResponseEntity.ok(toResponse(talhao));
    }

    // ===== CRIAR =====

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TalhaoResponse> criar(@Valid @RequestBody TalhaoRequest request) {

        Propriedade propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", request.getPropriedadeId()));

        // Validação: soma das áreas dos talhões não pode exceder a área da propriedade
        double somaAreas = talhaoRepository
                .findByPropriedadeIdAndAtivoTrue(request.getPropriedadeId())
                .stream()
                .mapToDouble(t -> t.getAreaHectares().doubleValue())
                .sum();

        double novaArea = request.getAreaHectares().doubleValue();

        if (somaAreas + novaArea > propriedade.getAreaTotalHectares().doubleValue()) {
            throw new RegraDeNegocioException(
                    String.format(
                            "Área total dos talhões (%.2f ha) excederia a área da propriedade (%.2f ha)",
                            somaAreas + novaArea,
                            propriedade.getAreaTotalHectares()
                    )
            );
        }

        Talhao talhao = Talhao.builder()
                .nome(request.getNome())
                .areaHectares(request.getAreaHectares())
                .tipoSolo(request.getTipoSolo())
                .descricao(request.getDescricao())
                .propriedade(propriedade)
                .build();

        talhao = talhaoRepository.save(talhao);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(talhao));
    }

    // ===== ATUALIZAR =====

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TalhaoResponse> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody TalhaoRequest request) {

        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão", id));

        // Recalcula soma excluindo o próprio talhão sendo atualizado
        double somaAreas = talhaoRepository
                .findByPropriedadeIdAndAtivoTrue(talhao.getPropriedade().getId())
                .stream()
                .filter(t -> !t.getId().equals(id))   // exclui o atual
                .mapToDouble(t -> t.getAreaHectares().doubleValue())
                .sum();

        double novaArea = request.getAreaHectares().doubleValue();
        double areaPropriedade = talhao.getPropriedade().getAreaTotalHectares().doubleValue();

        if (somaAreas + novaArea > areaPropriedade) {
            throw new RegraDeNegocioException(
                    String.format(
                            "Área total dos talhões (%.2f ha) excederia a área da propriedade (%.2f ha)",
                            somaAreas + novaArea,
                            areaPropriedade
                    )
            );
        }

        talhao.setNome(request.getNome());
        talhao.setAreaHectares(request.getAreaHectares());
        talhao.setTipoSolo(request.getTipoSolo());
        talhao.setDescricao(request.getDescricao());

        return ResponseEntity.ok(toResponse(talhaoRepository.save(talhao)));
    }

    // ===== DELETAR (soft delete) =====

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {

        Talhao talhao = talhaoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão", id));

        talhao.setAtivo(false);
        talhaoRepository.save(talhao);

        return ResponseEntity.noContent().build();
    }

    // ===== MAPEAMENTO =====

    private TalhaoResponse toResponse(Talhao talhao) {
        return TalhaoResponse.builder()
                .id(talhao.getId())
                .nome(talhao.getNome())
                .areaHectares(talhao.getAreaHectares())
                .tipoSolo(talhao.getTipoSolo())
                .descricao(talhao.getDescricao())
                .propriedadeId(talhao.getPropriedade().getId())
                .nomePropriedade(talhao.getPropriedade().getNome())
                .build();
    }
}