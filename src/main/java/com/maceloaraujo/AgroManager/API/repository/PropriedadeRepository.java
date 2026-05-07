package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Propriedade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {

    Page<Propriedade> findAllByAtivoTrue(Pageable pageable);
}
