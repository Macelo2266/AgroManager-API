package com.maceloaraujo.AgroManager.API.service;

import com.maceloaraujo.AgroManager.API.dto.request.ColheitaRequest;
import com.maceloaraujo.AgroManager.API.dto.request.FiltroRelatorioRequest;
import com.maceloaraujo.AgroManager.API.dto.request.PlantioRequest;
import com.maceloaraujo.AgroManager.API.dto.response.PlantioResponse;
import com.maceloaraujo.AgroManager.API.dto.response.RelatorioProducaoResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.exception.RegraDeNegocioException;
import com.maceloaraujo.AgroManager.API.model.StatusPlantio;
import com.maceloaraujo.AgroManager.API.model.entity.Colheita;
import com.maceloaraujo.AgroManager.API.model.entity.Cultura;
import com.maceloaraujo.AgroManager.API.model.entity.Plantio;
import com.maceloaraujo.AgroManager.API.model.entity.Talhao;
import com.maceloaraujo.AgroManager.API.repository.ColheitaRepository;
import com.maceloaraujo.AgroManager.API.repository.CulturaRepository;
import com.maceloaraujo.AgroManager.API.repository.PlantioRepository;
import com.maceloaraujo.AgroManager.API.repository.TalhaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducaoService {

    private final PlantioRepository plantioRepository;
    private final ColheitaRepository colheitaRepository;
    private final TalhaoRepository talhaoRepository;
    private final CulturaRepository culturaRepository;

    // ===== PLANTIO =====

    @Transactional
    public PlantioResponse registrarPlantio(PlantioRequest request) {
        Talhao talhao = talhaoRepository.findById(request.getTalhaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Talhão", request.getTalhaoId()));

        Cultura cultura = culturaRepository.findById(request.getCulturaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cultura", request.getCulturaId()));

        if (request.getAreaPlantadaHectares().compareTo(talhao.getAreaHectares()) > 0) {
            throw new RegraDeNegocioException(
                    String.format("Área plantada (%.2f ha) não pode ser maior que o talhão (%.2f ha)",
                            request.getAreaPlantadaHectares(), talhao.getAreaHectares())
            );
        }

        Plantio plantio = Plantio.builder()
                .talhao(talhao)
                .cultura(cultura)
                .dataPlantio(request.getDataPlantio())          // ← nome correto
                .dataPrevistaColheita(request.getDataPrevistaColheita())
                .areaPlantadaHectares(request.getAreaPlantadaHectares())
                .quantidadeSementesKg(request.getQuantidadeSementeKg())
                .status(StatusPlantio.EM_ANDAMENTO)
                .observacoes(request.getObservacoes())
                .build();

        plantio = plantioRepository.save(plantio);
        log.info("Plantio registrado: {} no talhão {}", cultura.getNome(), talhao.getNome());
        return toResponse(plantio);
    }

    @Transactional(readOnly = true)
    public Page<PlantioResponse> listarPlantios(Long propriedadeId, Pageable pageable) {
        return plantioRepository
                .findByTalhaoPropriedadeIdAndAtivoTrue(propriedadeId, pageable)
                .map(this::toResponse);
    }

    // ===== COLHEITA =====

    @Transactional
    public Colheita registrarColheita(ColheitaRequest request) {
        Plantio plantio = plantioRepository.findById(request.getPlantioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Plantio", request.getPlantioId()));

        if (plantio.getStatus() == StatusPlantio.PERDIDO) {
            throw new RegraDeNegocioException("Não é possível registrar colheita em plantio perdido");
        }

        Colheita colheita = Colheita.builder()
                .plantio(plantio)
                .dataColheita(request.getDataColheita())
                .quantidadeToneladas(request.getQuantidadeToneladas())
                .umidadePercentual(request.getUmidadePercentual())
                .precoVendaTon(request.getPrecoVendaTon())
                .observacoes(request.getObservacoes())
                .build();

        colheita = colheitaRepository.save(colheita);
        plantio.setStatus(StatusPlantio.COLHIDO);
        plantioRepository.save(plantio);

        log.info("Colheita registrada: {} ton", colheita.getQuantidadeToneladas());
        return colheita;
    }

    // ===== RELATÓRIO =====

    @Transactional(readOnly = true)
    public RelatorioProducaoResponse gerarRelatorio(FiltroRelatorioRequest filtro) {
        List<Plantio> plantios = plantioRepository.findByPropriedadeAndPeriodo(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );

        List<Colheita> colheitas = colheitaRepository.findByPropriedadeAndPeriodo(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );

        // Produção agrupada por cultura
        Map<String, BigDecimal> producaoPorCultura = new LinkedHashMap<>();
        List<Object[]> rawData = colheitaRepository.sumProducaoPorCultura(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );
        for (Object[] row : rawData) {
            producaoPorCultura.put((String) row[0], (BigDecimal) row[1]);
        }

        BigDecimal totalProducao = producaoPorCultura.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal areaTotal = plantios.stream()
                .map(Plantio::getAreaPlantadaHectares)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal produtividadeMedia = areaTotal.compareTo(BigDecimal.ZERO) > 0
                ? totalProducao.divide(areaTotal, 3, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return RelatorioProducaoResponse.builder()
                .producaoPorCultura(producaoPorCultura)
                .totalProducaoToneladas(totalProducao)
                .produtividadeMediaTonHa(produtividadeMedia)
                .totalPlantios(plantios.size())
                .totalColheitas(colheitas.size())
                .build();
    }

    // ===== MAPEAMENTO =====

    private PlantioResponse toResponse(Plantio p) {
        return PlantioResponse.builder()
                .id(p.getId())
                .talhaoId(p.getTalhao().getId())
                .nomeTalhao(p.getTalhao().getNome())
                .culturaId(p.getCultura().getId())
                .nomeCultura(p.getCultura().getNome())
                .dataPlantio(p.getDataPlantio())
                .dataPrevistaColheita(p.getDataPrevistaColheita())
                .areaPlantadaHectares(p.getAreaPlantadaHectares())
                .status(p.getStatus())
                .produtividadeAtualTonHa(p.calcularProdutividadeTotal())
                .observacoes(p.getObservacoes())
                .build();
    }
}
