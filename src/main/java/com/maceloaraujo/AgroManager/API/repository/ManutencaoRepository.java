package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Manutencao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManutencaoRepository extends JpaRepository<Manutencao, Long> {

    Page<Manutencao> findByMaquinaIdOrderByDataManutencaoDesc(Long maquinaId, Pageable pageable);


}
