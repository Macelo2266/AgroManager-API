package com.maceloaraujo.AgroManager.API.service;

import com.maceloaraujo.AgroManager.API.dto.response.AlertaResponse;
import com.maceloaraujo.AgroManager.API.dto.response.EstoqueResponse;
import com.maceloaraujo.AgroManager.API.repository.MaquinaRepository;
import com.maceloaraujo.AgroManager.API.repository.PropriedadeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaService {

    private final EstoqueService estoqueService;
    private final FinanceiroService financeiroService;
    private final MaquinaRepository maquinaRepository;
    private final PropriedadeRepository propriedadeRepository;


    @Transactional(readOnly = true)
    public AlertaResponse buscarAlertas(Long propriedadeId) {

        List<String> alertasEstoque    = verificarEstoqueBaixo(propriedadeId);
        List<String> alertasManutencao = verificarManutencaoPendente(propriedadeId);
        List<String> alertasGastos     = verificarGastosElevados(propriedadeId);

        int total = alertasEstoque.size()
                + alertasManutencao.size()
                + alertasGastos.size();

        return AlertaResponse.builder()
                .alertasEstoqueBaixo(alertasEstoque)
                .alertasManutencaoPendente(alertasManutencao)
                .alertasGastosElevados(alertasGastos)
                .totalAlertas(total)
                .build();
    }


    /**
     * Busca produtos com estoque abaixo do mínimo configurado.
     * EstoqueService já faz a query — aqui só convertemos para mensagem legível.
     */
    private List<String> verificarEstoqueBaixo(Long propriedadeId) {
        List<EstoqueResponse> produtosBaixos =
                estoqueService.verificarEstoqueBaixo(propriedadeId);

        return produtosBaixos.stream()
                .map(e -> String.format(
                        "Estoque baixo: %s — disponível: %.3f %s | mínimo: %.3f %s",
                        e.getNomeProduto(),
                        e.getQuantidade(),
                        e.getUnidadeMedida(),
                        e.getEstoqueMinimo(),
                        e.getUnidadeMedida()
                ))
                .toList();
    }


    private List<String> verificarManutencaoPendente(Long propriedadeId) {
        LocalDate hoje      = LocalDate.now();
        LocalDate alertaAte = hoje.plusDays(7); // alerta com 7 dias de antecedência

        return maquinaRepository
                .findMaquinasComManutencaoPendente(propriedadeId, alertaAte)
                .stream()
                .map(m -> {
                    boolean atrasada = m.getProximaManutencaoPrevista().isBefore(hoje);
                    return String.format(
                            "Manutenção %s — máquina: '%s' | prevista para: %s",
                            atrasada ? "ATRASADA" : "próxima do vencimento",
                            m.getNome(),
                            m.getProximaManutencaoPrevista()
                    );
                })
                .toList();
    }


    private List<String> verificarGastosElevados(Long propriedadeId) {
        boolean foraDoPadrao =
                financeiroService.gastoForaDoPadrao(propriedadeId, LocalDate.now());

        if (foraDoPadrao) {
            return List.of(
                    "⚠️ Gastos do mês atual estão mais de 30% acima da média dos últimos 3 meses"
            );
        }

        return List.of();
    }


    /**
     * Verifica alertas de todas as propriedades automaticamente.
     * Cron: "seg min hora dia mês dia-semana"
     *
     * Em produção, aqui você enviaria e-mail, push ou WhatsApp.
     * Por enquanto apenas loga os alertas no console/arquivo.
     */
    @Scheduled(cron = "0 0 7 * * *")
    @Transactional(readOnly = true)
    public void verificarAlertasAutomaticos() {
        log.info("=== Iniciando verificação automática de alertas ===");

        propriedadeRepository.findAll().forEach(propriedade -> {
            AlertaResponse alertas = buscarAlertas(propriedade.getId());

            if (alertas.getTotalAlertas() == 0) {
                log.info("Propriedade '{}' — nenhum alerta.", propriedade.getNome());
                return;
            }

            log.warn("Propriedade '{}' — {} alerta(s) encontrado(s):",
                    propriedade.getNome(), alertas.getTotalAlertas());

            alertas.getAlertasEstoqueBaixo()
                    .forEach(a -> log.warn("  [ESTOQUE]    {}", a));

            alertas.getAlertasManutencaoPendente()
                    .forEach(a -> log.warn("  [MANUTENÇÃO] {}", a));

            alertas.getAlertasGastosElevados()
                    .forEach(a -> log.warn("  [FINANCEIRO] {}", a));

            // TODO: integrar com serviço de notificação (e-mail, WhatsApp, push)
        });

        log.info("=== Verificação automática finalizada ===");
    }
}
