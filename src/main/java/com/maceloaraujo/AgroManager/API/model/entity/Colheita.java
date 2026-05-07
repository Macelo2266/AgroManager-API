package com.maceloaraujo.AgroManager.API.model.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "colheitas")
public class Colheita extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plantio_id", nullable = false)
    private Plantio plantio;

    @Column(name = "data_colheita", nullable = false)
    private LocalDate dataColheita;

    @Column(name = "quantidade_toneladas", nullable = false, precision = 10, scale = 3)
    private BigDecimal quantidadeToneladas;

    @Column(name = "umidade_percentual")
    private BigDecimal umidadePercentual;

    @Column(name = "preco_venda_ton")
    private BigDecimal precoVendaTon;


    @Column(name = "observacoes")
    private String observacoes;
}
