package com.maceloaraujo.AgroManager.API.service;

import com.maceloaraujo.AgroManager.API.dto.request.FiltroRelatorioRequest;
import com.maceloaraujo.AgroManager.API.dto.request.DespesaRequest;
import com.maceloaraujo.AgroManager.API.dto.request.VendaRequest;
import com.maceloaraujo.AgroManager.API.dto.response.FluxoCaixaResponse;
import com.maceloaraujo.AgroManager.API.exception.RecursoNaoEncontradoException;
import com.maceloaraujo.AgroManager.API.model.CategoriaDespesa;
import com.maceloaraujo.AgroManager.API.model.entity.Colheita;
import com.maceloaraujo.AgroManager.API.model.entity.Despesa;
import com.maceloaraujo.AgroManager.API.model.entity.Propriedade;
import com.maceloaraujo.AgroManager.API.model.entity.Venda;
import com.maceloaraujo.AgroManager.API.repository.ColheitaRepository;
import com.maceloaraujo.AgroManager.API.repository.DespesaRepository;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
import com.maceloaraujo.AgroManager.API.repository.VendaRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceiroService {

    private final VendaRepository vendaRepository;
    private final DespesaRepository despesaRepository;
    private final PropriedadeRepository propriedadeRepository;
    private final ColheitaRepository colheitaRepository;

    // ===== VENDAS (RECEITAS) =====

    @Transactional
    public Venda registrarVenda(VendaRequest request) {
        Propriedade propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", request.getPropriedadeId()));

        Colheita colheita = null;
        if (request.getColheitaId() != null) {
            colheita = colheitaRepository.findById(request.getColheitaId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Colheita", request.getColheitaId()));
        }

        Venda venda = Venda.builder()
                .propriedade(propriedade)
                .colheita(colheita)
                .descricao(request.getDescricao())
                .comprador(request.getComprador())
                .quantidade(request.getQuantidade())
                .precoUnitario(request.getPrecoUnitario())
                .valorTotal(request.getValorTotal())
                .dataVenda(request.getDataVenda())
                .notaFiscal(request.getNotaFiscal())
                .build();

        venda = vendaRepository.save(venda);
        log.info("Venda registrada: R$ {} para propriedade {}", venda.getValorTotal(), propriedade.getNome());
        return venda;
    }

    @Transactional(readOnly = true)
    public Page<Venda> listarVendas(Long propriedadeId, Pageable pageable) {
        return vendaRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable);
    }

    // ===== DESPESAS =====

    @Transactional
    public Despesa registrarDespesa(DespesaRequest request) {
        Propriedade propriedade = propriedadeRepository.findById(request.getPropriedadeId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Propriedade", request.getPropriedadeId()));

        Despesa despesa = Despesa.builder()
                .propriedade(propriedade)
                .descricao(request.getDescricao())
                .categoria(request.getCategoria())
                .valor(request.getValor())
                .dataDespesa(request.getDataDespesa())
                .fornecedor(request.getFornecedor())
                .notaFiscal(request.getNotaFiscal())
                .observacoes(request.getObservacoes())
                .build();

        despesa = despesaRepository.save(despesa);
        log.info("Despesa registrada: {} - R$ {}", despesa.getCategoria(), despesa.getValor());
        return despesa;
    }

    @Transactional(readOnly = true)
    public Page<Despesa> listarDespesas(Long propriedadeId, Pageable pageable) {
        return despesaRepository.findByPropriedadeIdAndAtivoTrue(propriedadeId, pageable);
    }

    // ===== FLUXO DE CAIXA =====

    /**
     * Calcula o fluxo de caixa (receitas - despesas) em um período.
     *
     * FÓRMULA:
     * - Total Receitas = soma de todas as vendas no período
     * - Total Despesas = soma de todas as despesas no período
     * - Lucro Líquido = Receitas - Despesas
     * - Margem = (Lucro / Receitas) * 100
     */
    @Transactional(readOnly = true)
    public FluxoCaixaResponse calcularFluxoCaixa(FiltroRelatorioRequest filtro) {
        BigDecimal totalReceitas = vendaRepository.sumByPropriedadeAndPeriodo(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );

        BigDecimal totalDespesas = despesaRepository.sumByPropriedadeAndPeriodo(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );

        BigDecimal lucroLiquido = totalReceitas.subtract(totalDespesas);

        BigDecimal margem = BigDecimal.ZERO;
        if (totalReceitas.compareTo(BigDecimal.ZERO) > 0) {
            margem = lucroLiquido
                    .divide(totalReceitas, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return FluxoCaixaResponse.builder()
                .dataInicio(filtro.getDataInicio())
                .dataFim(filtro.getDataFim())
                .totalReceitas(totalReceitas)
                .totalDespesas(totalDespesas)
                .lucroLiquido(lucroLiquido)
                .margemLucroPercentual(margem)
                .build();
    }

    /**
     * Relatório de despesas por categoria no período.
     * Útil para identificar onde o produtor está gastando mais.
     */
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> despesasPorCategoria(FiltroRelatorioRequest filtro) {
        List<Object[]> resultados = despesaRepository.sumPorCategoriaAndPeriodo(
                filtro.getPropriedadeId(), filtro.getDataInicio(), filtro.getDataFim()
        );

        Map<String, BigDecimal> despesasPorCategoria = new LinkedHashMap<>();
        for (Object[] row : resultados) {
            CategoriaDespesa categoria = (CategoriaDespesa) row[0];
            BigDecimal total = (BigDecimal) row[1];
            despesasPorCategoria.put(categoria.name(), total);
        }

        return despesasPorCategoria;
    }

    /**
     * Verifica se os gastos de um período estão fora do padrão.
     * Compara com a média dos 3 meses anteriores.
     * Retorna alerta se despesa atual > 30% acima da média histórica.
     */
    @Transactional(readOnly = true)
    public boolean gastoForaDoPadrao(Long propriedadeId, LocalDate mesReferencia) {
        // Mês atual
        LocalDate inicioMesAtual = mesReferencia.withDayOfMonth(1);
        LocalDate fimMesAtual = mesReferencia.withDayOfMonth(mesReferencia.lengthOfMonth());

        BigDecimal gastosAtuais = despesaRepository.sumByPropriedadeAndPeriodo(
                propriedadeId, inicioMesAtual, fimMesAtual
        );

        // Média dos 3 meses anteriores
        LocalDate inicioHistorico = inicioMesAtual.minusMonths(3);
        LocalDate fimHistorico = inicioMesAtual.minusDays(1);

        BigDecimal gastosHistorico = despesaRepository.sumByPropriedadeAndPeriodo(
                propriedadeId, inicioHistorico, fimHistorico
        );

        if (gastosHistorico.compareTo(BigDecimal.ZERO) == 0) {
            return false; // Sem histórico para comparar
        }

        BigDecimal mediaHistorica = gastosHistorico.divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);
        BigDecimal limiteAlerta = mediaHistorica.multiply(BigDecimal.valueOf(1.30)); // 30% acima

        return gastosAtuais.compareTo(limiteAlerta) > 0;
    }
}

