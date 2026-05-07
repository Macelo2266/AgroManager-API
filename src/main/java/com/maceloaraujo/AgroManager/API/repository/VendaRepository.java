package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Venda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    Page<Venda> findByPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(v.valorTotal), 0) " +
            "FROM Venda v " +
            "WHERE v.propriedade.id = :propriedadeId " +
            "AND v.dataVenda " +
            "BETWEEN :inicio AND :fim")
    BigDecimal sumByPropriedadeAndPeriodo(Long propriedadeId, LocalDate inicio, LocalDate fim);

    List<Venda> findByPropriedadeIdAndDataVendaBetweenAndAtivoTrue(Long propriedadeId, LocalDate inicio, LocalDate fim);
}
