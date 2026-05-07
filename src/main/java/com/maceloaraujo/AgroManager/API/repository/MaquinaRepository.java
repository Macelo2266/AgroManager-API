package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Maquina;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Long> {

    Page<Maquina> findByPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    @Query("""
        SELECT m FROM Maquina m
        WHERE m.propriedade.id = :propriedadeId
          AND m.ativo = true
          AND m.proximaManutencaoPrevista IS NOT NULL
          AND m.proximaManutencaoPrevista <= :dataLimite
          AND m.status = 'ATIVA'
    """)
    List<Maquina> findMaquinasComManutencaoPendente(Long propriedadeId, LocalDate dataLimite);
}
