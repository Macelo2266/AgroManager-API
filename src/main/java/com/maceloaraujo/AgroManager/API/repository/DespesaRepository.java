package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Despesa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    Page<Despesa> findByPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM Despesa d WHERE d.propriedade.id = :propriedadeId AND d.dataDespesa BETWEEN :inicio AND :fim")
    BigDecimal sumByPropriedadeAndPeriodo(Long propriedadeId, LocalDate inicio, LocalDate fim);

    @Query("""
        SELECT d.categoria, COALESCE(SUM(d.valor), 0)
        FROM Despesa d
        WHERE d.propriedade.id = :propriedadeId
          AND d.dataDespesa BETWEEN :inicio AND :fim
        GROUP BY d.categoria
        ORDER BY SUM(d.valor) DESC
    """)
    List<Object[]> sumPorCategoriaAndPeriodo(Long propriedadeId, LocalDate inicio, LocalDate fim);

    List<Despesa> findByPropriedadeIdAndDataDespesaBetweenAndAtivoTrue(Long propriedadeId, LocalDate inicio, LocalDate fim);
}
