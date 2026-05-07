package com.maceloaraujo.AgroManager.API.controller;

import com.maceloaraujo.AgroManager.API.dto.request.ManutencaoRequest;
import com.maceloaraujo.AgroManager.API.dto.request.MaquinaRequest;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.model.entity.Manutencao;
import com.maceloaraujo.AgroManager.API.model.entity.Maquina;
import com.maceloaraujo.AgroManager.API.model.entity.Propriedade;
import com.maceloaraujo.AgroManager.API.repository.ManutencaoRepository;
import com.maceloaraujo.AgroManager.API.repository.MaquinaRepository;
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

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/maquinas")
@RequiredArgsConstructor
public class MaquinaController {

    private final MaquinaRepository maquinaRepository;
    private final ManutencaoRepository manutencaoRepository;
    private final PropriedadeRepository propriedadeRepository;

    @GetMapping
    public ResponseEntity<Page<Maquina>> listar(
            @RequestParam Long propriedadeId,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                maquinaRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maquina> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(
                maquinaRepository.findById(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Máquina", id))
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Maquina> criar(@Valid @RequestBody MaquinaRequest request) {

        Propriedade propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", request.getPropriedadeId()));

        Maquina maquina = Maquina.builder()
                .propriedade(propriedade)
                .nome(request.getNome())
                .modelo(request.getModelo())
                .fabricante(request.getFabricante())
                .anoFabricante(request.getAnoFabricacao())
                .numeroSerie(request.getNumeroSerie())
                .intervaloManutencaoHoras(request.getIntervaloManutencaoHoras())
                .proximaManutencaoPrevista(request.getProximaManutencaoPrevista())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(maquinaRepository.save(maquina));
    }

    @PatchMapping("/{id}/horas-uso")
    public ResponseEntity<Maquina> registrarHorasUso(
            @PathVariable Long id,
            @RequestParam BigDecimal horas) {

        Maquina maquina = maquinaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Máquina", id));

        maquina.adicionarHorasUso(horas);

        return ResponseEntity.ok(maquinaRepository.save(maquina));
    }

    @GetMapping("/{maquinaId}/manutencoes")
    public ResponseEntity<Page<Manutencao>> listarManutencoes(
            @PathVariable Long maquinaId,
            @PageableDefault(size = 20) Pageable pageable) {

        return ResponseEntity.ok(
                manutencaoRepository.findByMaquinaIdOrderByDataManutencaoDesc(maquinaId, pageable)
        );
    }

    @PostMapping("/manutencoes")
    public ResponseEntity<Manutencao> registrarManutencao(
            @Valid @RequestBody ManutencaoRequest request) {

        Maquina maquina = maquinaRepository.findById(request.getMaquinaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Máquina", request.getMaquinaId()));

        Manutencao manutencao = Manutencao.builder()
                .maquina(maquina)
                .tipo(request.getTipo())
                .descricao(request.getDescricao())
                .dataManutencao(request.getDataManutencao())
                .custo(request.getCusto())
                .horasMaquinaNoMomento(request.getHorasMaquinaNoMomento())
                .fornecedor(request.getFornecedor())
                .proximaManutencaoPrevista(request.getProximaManutencaoPrevista())
                .build();

        if (request.getProximaManutencaoPrevista() != null) {
            maquina.setProximaManutencaoPrevista(request.getProximaManutencaoPrevista());
            maquinaRepository.save(maquina);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(manutencaoRepository.save(manutencao));
    }
}
