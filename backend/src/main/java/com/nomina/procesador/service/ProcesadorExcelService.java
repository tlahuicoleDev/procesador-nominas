package com.nomina.procesador.service;

import com.nomina.procesador.dto.RegistroNominaDTO;
import com.nomina.procesador.model.TipoNomina;
import com.nomina.procesador.repository.TipoNominaRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ProcesadorExcelService {

    @Autowired
    private TipoNominaRepository tipoNominaRepository;

    public List<RegistroNominaDTO> procesarArchivo(MultipartFile file) throws Exception {
        List<RegistroNominaDTO> registros = new ArrayList<>();
        
        // 1. Cargar el mapeo desde la base de datos (PostgreSQL) una sola vez
        Map<String, String> mapTipo = new HashMap<>();
        List<TipoNomina> tipos = tipoNominaRepository.findAll();
        for (TipoNomina tipo : tipos) {
            // Mapeamos: cod_pago_excel (String) -> cve_tipo (String)
            // Ejemplo: "90" -> "S"
            mapTipo.put(tipo.getCodPagoExcel(), tipo.getCveTipo());
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            // 2. Obtenemos la hoja "ESTADO ORD" (es la que tiene los datos)
            Sheet sheet = workbook.getSheet("ESTADO ORD");
            if (sheet == null) {
                throw new RuntimeException("No se encontró la hoja 'ESTADO ORD' en el archivo.");
            }

            // 3. Iteramos sobre las filas de datos de ESTADO ORD
            Iterator<Row> rowIterator = sheet.rowIterator();
            // Saltamos el encabezado (fila 0)
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                RegistroNominaDTO dto = new RegistroNominaDTO();

                dto.setTipoNomina(getCellValue(row.getCell(3)));  // es tipo nomina (O/E)
                
                String tipoConcepto = getCellValue(row.getCell(6));
                String codConcepto = getCellValue(row.getCell(7));
                double importe = getNumericCellValue(row.getCell(9));
                String codPagoStr = getCellValue(row.getCell(14));
                String pagaFone = getCellValue(row.getCell(15));
                String cct = getCellValue(row.getCell(16));

                // 4. APLICAR REGLAS DE NEGOCIO EN MEMORIA -------------------------------------------------
                
                // Obtenemos la letra desde el mapa de BD. Si no existe, ponemos "X" (desconocido)
                String tipoLetra = mapTipo.getOrDefault(codPagoStr, "X");
                
                // REGLA 1: Si tipo es "H", tipo_concepto es "D" y cod_concepto es "001" -> cambiarlo a "001c"
                if ("H".equals(tipoLetra) && "D".equals(tipoConcepto) && "001".equals(codConcepto)) {
                    codConcepto = "001c";
                }

                // REGLA 2: Si cod_concepto es "17A" -> cambiarlo a "18B", tipo_concepto a "P", importe a positivo
                if ("17A".equals(codConcepto)) {
                    codConcepto = "18B";
                    tipoConcepto = "P";
                    if (importe < 0) {
                        importe = importe * -1;
                    }
                }

                // REGLA 3: Si tipo es "F" y cod_concepto es "26" -> cambiarlo a "001"
                if ("F".equals(tipoLetra) && "26".equals(codConcepto)) {
                    codConcepto = "001";
                }

                // 5. ARMAR EL DTO FINAL (Solo las columnas que necesitamos)
                dto.setTipoConceptoFinal(tipoConcepto);
                dto.setCvecon(codConcepto);
                dto.setImporteFinal(importe);
                dto.setCct(cct);
                dto.setCvetpl(tipoLetra); // La letra del mapa de BD
                dto.setPagaFone(pagaFone);

                // Solo agregamos a la lista si tiene información válida (evitamos basura)
                if (dto.getCct() != null && !dto.getCct().isEmpty()) {
                    registros.add(dto);
                }
            }
        }
        return registros;
    }

    // Métodos auxiliares para leer celdas de Excel de forma segura
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((long) cell.getNumericCellValue());
            default: return "";
        }
    }

    private double getNumericCellValue(Cell cell) {
        if (cell == null) return 0.0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        return 0.0;
    }
}