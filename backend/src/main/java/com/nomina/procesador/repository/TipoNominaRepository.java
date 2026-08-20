package com.nomina.procesador.repository;

import com.nomina.procesador.model.TipoNomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TipoNominaRepository extends JpaRepository<TipoNomina, Long> {
    Optional<TipoNomina> findByCveTipo(String cveTipo);
}