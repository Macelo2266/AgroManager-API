package com.maceloaraujo.AgroManager.API.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "culturas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cultura extends BaseEntity {

    @Column(name = "nome", nullable = false, unique = true)  // ← @Column obrigatório
    private String nome;

    @Column(name = "nome_cientifico")
    private String nomeCientifico;

    @Column(name = "ciclo_dias")
    private Integer cicloDias;

    @Column(name = "produtividade_media_ton_ha")
    private Double produtividadeMediaTonHa;

    @Column(name = "descricao")
    private String descricao;
}