package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.MovimentacaoEstoque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    Page<MovimentacaoEstoque> findByProdutoIdOrderByDataMovimentacaoDesc(
            Long produtoId, Pageable pageable
    );

    @Query("""
        SELECT m FROM MovimentacaoEstoque m
        WHERE m.produto.propriedade.id = :propriedadeId
          AND m.dataMovimentacao BETWEEN :inicio AND :fim
        ORDER BY m.dataMovimentacao DESC
    """)
    List<MovimentacaoEstoque> findByPropriedadeAndPeriodo(
            @Param("propriedadeId") Long propriedadeId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}