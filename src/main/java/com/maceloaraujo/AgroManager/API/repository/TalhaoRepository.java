package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Talhao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalhaoRepository extends JpaRepository<Talhao, Long> {

    Page<Talhao> findByPropriedadeIdAndAtivoTrue(Long propriedadeId, Pageable pageable);

    // ← era "findByPropriedadeAndAtivoTrue" — faltava o "Id"
    List<Talhao> findByPropriedadeIdAndAtivoTrue(Long propriedadeId);
}