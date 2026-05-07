package com.maceloaraujo.AgroManager.API.model.entity;

import com.maceloaraujo.AgroManager.API.model.StatusMaquina;
import com.maceloaraujo.AgroManager.API.model.TipoManutencao;
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
@Table(name = "manutencoes")
public class Manutencao extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;


    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoManutencao tipo;

    @Column(name = "descricao", nullable = false)
    private String descricao;

    @Column(name = "data_manutencao", nullable = false)
    private LocalDate dataManutencao;

    @Column(name = "custo", precision = 15, scale = 2)
    private BigDecimal custo;

    @Column(name = "horas_maquina_no_momento")
    private BigDecimal horasMaquinaNoMomento;

    @Column(name = "fornecedor")
    private String fornecedor;

    @Column(name = "proxima_manutencao_prevista")
    private LocalDate proximaManutencaoPrevista;
}
