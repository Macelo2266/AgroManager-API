package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Colheita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ColheitaRepository extends JpaRepository<Colheita, Long> {
    List<Colheita> findByPlantioIdAndAtivoTrue(Long plantioId);

    @Query("""
        SELECT c FROM Colheita c
        WHERE c.plantio.talhao.propriedade.id = :propriedadeId
          AND c.dataColheita BETWEEN :inicio AND :fim
          AND c.ativo = true
    """)
    List<Colheita> findByPropriedadeAndPeriodo(Long propriedadeId, LocalDate inicio, LocalDate fim);

    @Query("""
        SELECT c.plantio.cultura.nome, SUM(c.quantidadeToneladas)
        FROM Colheita c
        WHERE c.plantio.talhao.propriedade.id = :propriedadeId
          AND c.dataColheita BETWEEN :inicio AND :fim
        GROUP BY c.plantio.cultura.nome
    """)
    List<Object[]> sumProducaoPorCultura(Long propriedadeId, LocalDate inicio, LocalDate fim);
}
