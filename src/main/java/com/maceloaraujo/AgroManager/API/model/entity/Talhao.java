package com.maceloaraujo.AgroManager.API.model.entity;


import com.maceloaraujo.AgroManager.API.model.TipoSolo;
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
@Table(name = "talhoes")
public class Talhao  extends BaseEntity{

    @Column(name = "nome", nullable = false)
    private String nome;


    @Column(name = "area_hectares", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaHectares;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solo")
    private TipoSolo tipoSolo;

    @Column(name = "descricao")
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propriedade_id", nullable = false)
    private Propriedade propriedade;

    @OneToMany(mappedBy = "talhao")
    @Builder.Default
    private List<Plantio> plantios = new ArrayList<>();
}
