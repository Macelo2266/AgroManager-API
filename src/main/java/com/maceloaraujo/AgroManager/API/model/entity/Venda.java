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
@Table(name = "vendas")
public class Venda extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "colheita_id")
    private Colheita colheita;

    @Column(name = "descricao", nullable = false)
    private String descricao;


    @Column(name = "comprador")
    private String comprador;

    @Column(name = "quantidade")
    private BigDecimal quantidade;

    @Column(name = "preco_unitario")
    private BigDecimal precoUnitario;

    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_venda", nullable = false)
    private LocalDate dataVenda;

    @Column(name = "nota_fiscal")
    private String notaFiscal ;
}
