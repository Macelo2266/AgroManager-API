package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.TipoProduto;
import com.maceloaraujo.AgroManager.API.model.entity.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {


    Page<Produto> findByPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    Page<Produto> findByPropriedadeIdAndTipoAndAtivoTrue(
            Long propriedadeId, TipoProduto tipo, Pageable pageable
    );

    boolean existsByNomeAndPropriedadeId(String nome, Long propriedadeId);

    // ↓ Adicione este método
    @Query("""
        SELECT p FROM Produto p
        JOIN p.estoque e
        WHERE p.propriedade.id = :propriedadeId
          AND p.ativo = true
          AND p.estoqueMinimo IS NOT NULL
          AND e.quantidade < p.estoqueMinimo
    """)
    List<Produto> findProdutosComEstoqueBaixo(Long propriedadeId);

}
