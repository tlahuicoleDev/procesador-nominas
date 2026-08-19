package com.nomina.procesador.repository;

import com.nomina.procesador.model.Cct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CctRepository extends JpaRepository<Cct, Long> {
    Optional<Cct> findByCveCct(String cveCct);
}