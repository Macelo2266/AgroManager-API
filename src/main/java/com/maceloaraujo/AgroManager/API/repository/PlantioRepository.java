package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Plantio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlantioRepository extends JpaRepository<Plantio, Long> {

    // Usado pelo listarPlantios()
    Page<Plantio> findByTalhaoPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    // Usado pelo gerarRelatorio()
    @Query("""
        SELECT p FROM Plantio p
        WHERE p.talhao.propriedade.id = :propriedadeId
          AND p.dataPlantio BETWEEN :inicio AND :fim
          AND p.ativo = true
    """)
    List<Plantio> findByPropriedadeAndPeriodo(
            Long propriedadeId, LocalDate inicio, LocalDate fim
    );
}
