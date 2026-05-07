package com.maceloaraujo.AgroManager.API.repository;

import com.maceloaraujo.AgroManager.API.model.entity.Cultura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CulturaRepository extends JpaRepository<Cultura, Long> {

    Optional<Cultura> findByNome(String nome);
    boolean existsByNome(String nome);
}
