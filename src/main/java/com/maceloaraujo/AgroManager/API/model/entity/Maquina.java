package com.maceloaraujo.AgroManager.API.model.entity;


import com.maceloaraujo.AgroManager.API.model.StatusMaquina;
import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "maquinas")
public class Maquina extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;


    @Column(name = "nome")
    private String nome;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "fabricante")
    private String fabricante;

    @Column(name = "ano_fabricante")
    private Integer anoFabricante;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "horas_uso_total")
    @Builder.Default
    private BigDecimal horasUsoTotal = BigDecimal.ZERO;

    @Column(name = "intervalo_manutencao_horas")
    private Integer intervaloManutencaoHoras; // ex: a cada 250h


    @Column(name = "proxima_manutencao_prevista")
    private LocalDate proximaManutencaoPrevista;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private StatusMaquina status = StatusMaquina.ATIVA;

    @OneToMany(mappedBy = "maquina", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Manutencao> manutencoes = new ArrayList<>();

    public void adicionarHorasUso(BigDecimal horas) {
        this.horasUsoTotal = this.horasUsoTotal.add(horas);
    }

    public boolean precisaManutencao(){
        if(proximaManutencaoPrevista == null) return false;
        return !LocalDate.now().isBefore(proximaManutencaoPrevista);
    }


}
