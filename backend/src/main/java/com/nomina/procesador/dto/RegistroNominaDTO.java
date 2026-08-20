package com.nomina.procesador.dto;

import lombok.Data;

@Data
public class RegistroNominaDTO {

    private String tipoNomina;        // Ordinaria (O) o Extraordinaria (E)
    private String tipoConceptoFinal; // P o D
    private String cvecon;            // Codigo del concepto (001, 001c, etc.)
    private double importeFinal;
    private String pagaFone;
    private String cct;               // Centro de trabajo
    private String cvetpl;            // La letra (F, S, E, A, etc.)   
}