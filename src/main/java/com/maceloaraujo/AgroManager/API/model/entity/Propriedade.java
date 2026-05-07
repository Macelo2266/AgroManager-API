package com.maceloaraujo.AgroManager.API.model.entity;


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
@Table(name = "propriedades")
public class Propriedade extends BaseEntity{

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "area_total_hectares", nullable = false, precision = 10, scale = 2)
    private BigDecimal areaTotalHectares;

    @Column(name = "localizacao")
    private String localizacao;

    @Column(name = "municipio")
    private String municipio;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "cpf_cnpj_proprietario", length = 14)
    private String cpfCnpjProprietario;

    // Talhões são partes da propriedade - cascade faz sentido aqui

    @OneToMany(mappedBy = "propriedade", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Talhao> talhoes = new ArrayList<>();

    @OneToMany(mappedBy = "propriedade")
    @Builder.Default
    private List<Usuario> usuarios = new ArrayList<>();

}
