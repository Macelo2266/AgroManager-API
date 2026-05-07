package com.maceloaraujo.AgroManager.API.model.entity;


import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "produtos")
public class Produto extends BaseEntity{

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "descricao")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoProduto tipo;

    @Column(name = "unidade_medida", nullable = false)  // kg, litro, saca, unidade
    private String unidadeMedida;

    @Column(name = "estoque_minimo", precision = 10, scale = 3)
    private BigDecimal estoqueMinimo;    // Para alertas de estoque baixo

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @OneToOne(mappedBy = "produto", cascade = CascadeType.ALL)
    private Estoque estoque;

    @OneToMany(mappedBy = "produto")
    @Builder.Default
    private List<MovimentacaoEstoque> movimentacoes = new ArrayList<>();




}
