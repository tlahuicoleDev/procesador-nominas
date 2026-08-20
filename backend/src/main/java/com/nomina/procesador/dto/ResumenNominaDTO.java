package com.nomina.procesador.dto;

import lombok.Data;
import java.util.List;

@Data
public class ResumenNominaDTO {
    private String tipoNomina;      // "NOMINA ESTATAL", "NOMINA IMSS", etc.
    private String cveTipo;         // "F", "E", "S"
    private double totalPercepciones;
    private double totalDeducciones;
    private double neto;
    private List<RegistroNominaDTO> detalle; // Los registros originales (opcional)
}