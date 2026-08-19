package com.nomina.procesador.repository;

import com.nomina.procesador.model.PercepcionSegmento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PercepcionSegmentoRepository extends JpaRepository<PercepcionSegmento, Long> {
    Optional<PercepcionSegmento> findByCveConcepto(String cveConcepto);
}