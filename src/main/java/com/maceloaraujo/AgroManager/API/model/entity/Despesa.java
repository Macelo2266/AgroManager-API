package com.maceloaraujo.AgroManager.API.model.entity;

import com.maceloaraujo.AgroManager.API.model.CategoriaDespesa;
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
@Table(name = "despesa")
public class Despesa extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;


    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaDespesa categoria;

    @Column(name = "valor",nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;


    @Column(name = "data_despesa", nullable = false)
    private LocalDate dataDespesa;

    @Column(name = "fornecedor")
    private String fornecedor;

    @Column(name = "nota_fiscal")
    private String notaFiscal;

    @Column(name = "observacoes")
    private String observacoes;

}
