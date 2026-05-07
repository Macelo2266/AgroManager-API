package com.maceloaraujo.AgroManager.API.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "estoques")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estoque extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)           // ← deve ser @OneToOne
    @JoinColumn(name = "produto_id", nullable = false, unique = true)
    private Produto produto;

    @Column(name = "quantidade", nullable = false, precision = 12, scale = 3)
    @Builder.Default
    private BigDecimal quantidade = BigDecimal.ZERO;

    @Column(name = "custo_medio", precision = 12, scale = 4)
    @Builder.Default
    private BigDecimal custoMedio = BigDecimal.ZERO;

    @Column(name = "valor_total_estoque", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal valorTotalEstoque = BigDecimal.ZERO;

    public void registrarEntrada(BigDecimal quantidadeEntrada, BigDecimal custoUnitarioEntrada) {
        if (quantidadeEntrada.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade de entrada deve ser positiva");
        }
        BigDecimal novoSaldo = this.quantidade.add(quantidadeEntrada);
        BigDecimal valorEntrada = quantidadeEntrada.multiply(custoUnitarioEntrada);
        BigDecimal valorAtual = this.quantidade.multiply(this.custoMedio);

        this.custoMedio = (valorAtual.add(valorEntrada))
                .divide(novoSaldo, 4, RoundingMode.HALF_UP);

        this.quantidade = novoSaldo;
        this.valorTotalEstoque = this.quantidade.multiply(this.custoMedio);
    }

    public void registrarSaida(BigDecimal quantidadeSaida) {
        if (quantidadeSaida.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantidade de saída deve ser positiva");
        }
        if (this.quantidade.compareTo(quantidadeSaida) < 0) {
            throw new IllegalStateException(
                    String.format("Estoque insuficiente. Disponível: %.3f, Solicitado: %.3f",
                            this.quantidade, quantidadeSaida)
            );
        }
        this.quantidade = this.quantidade.subtract(quantidadeSaida);
        this.valorTotalEstoque = this.quantidade.multiply(this.custoMedio);
    }

    public boolean isEstoqueBaixo() {
        if (produto.getEstoqueMinimo() == null) return false;
        return this.quantidade.compareTo(produto.getEstoqueMinimo()) < 0;
    }
}