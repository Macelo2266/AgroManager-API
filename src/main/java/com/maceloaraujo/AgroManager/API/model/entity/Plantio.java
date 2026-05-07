package com.maceloaraujo.AgroManager.API.model.entity;

import jakarta.persistence.*;
import lombok.*;
import com.maceloaraujo.AgroManager.API.model.StatusPlantio;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "plantios")
public class Plantio extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talhao_id", nullable = false)
    private Talhao talhao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cultura_id", nullable = false)
    private Cultura cultura;

    @Column(name = "data_platio", nullable = false)
    private LocalDate dataPlantio;

    @Column(name = "data_prevista_colheita")
    private LocalDate dataPrevistaColheita;

    @Column(name = "area_plantada_hectares", nullable = false,precision = 10, scale = 2)
    private BigDecimal areaPlantadaHectares;

    @Column(name = "quantidade_sementes_kg")
    private BigDecimal quantidadeSementesKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StatusPlantio status = StatusPlantio.EM_ANDAMENTO;

    @Column(name = "observacoes")
    private String observacoes;

    @OneToMany(mappedBy = "plantio", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Colheita> colheitas = new ArrayList<>();

    public BigDecimal calcularProdutividadeTotal(){
        if (colheitas.isEmpty() || areaPlantadaHectares.compareTo(BigDecimal.ZERO) == 0){
            return BigDecimal.ZERO;
        }
        BigDecimal totalColhido = colheitas.stream()
                .map(Colheita:: getQuantidadeToneladas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return totalColhido.divide(areaPlantadaHectares, 4, java.math.RoundingMode.HALF_EVEN);
    }
}
