package com.nomina.procesador.controller;

import com.nomina.procesador.dto.RegistroNominaDTO;
import com.nomina.procesador.dto.ResumenNominaDTO;
import com.nomina.procesador.service.ProcesadorExcelService;
import com.nomina.procesador.service.ProcesamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/nomina")
@CrossOrigin(origins = "*")
public class NominaController {

    @Autowired
    private ProcesadorExcelService procesadorExcelService;

    @Autowired
    private ProcesamientoService procesamientoService;

    @PostMapping("/procesar")
    public ResponseEntity<List<ResumenNominaDTO>> procesarArchivo(@RequestParam("file") MultipartFile file) {
        try {
            // Paso 1: Leer y transformar el Excel
            List<RegistroNominaDTO> registros = procesadorExcelService.procesarArchivo(file);
            
            // Paso 2: Procesar los registros (agrupar y calcular totales)
            List<ResumenNominaDTO> resumen = procesamientoService.procesarRegistros(registros);
            
            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}