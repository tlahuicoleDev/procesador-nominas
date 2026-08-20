package com.nomina.procesador.service;

import com.nomina.procesador.dto.RegistroNominaDTO;
import com.nomina.procesador.dto.ResumenNominaDTO;
import com.nomina.procesador.model.TipoNomina;
import com.nomina.procesador.repository.TipoNominaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProcesamientoService {

    @Autowired
    private TipoNominaRepository tipoNominaRepository;

    public List<ResumenNominaDTO> procesarRegistros(List<RegistroNominaDTO> registros) {
        // Mapa para agrupar por cod_pago_excel (el número 91, 92, 93, etc.)
        Map<String, ResumenNominaDTO> mapaResumen = new HashMap<>();

        // Cargamos todos los tipos de nómina de la BD una sola vez
        List<TipoNomina> tipos = tipoNominaRepository.findAll();
        // Creamos un mapa auxiliar: cod_pago_excel -> TipoNomina
        Map<String, TipoNomina> mapaTipos = new HashMap<>();
        for (TipoNomina tipo : tipos) {
            mapaTipos.put(tipo.getCodPagoExcel(), tipo);
        }

        for (RegistroNominaDTO registro : registros) {
            // Obtenemos el código de pago que viene del Excel (90, 91, 92...)
            // NOTA: Como en tu DTO solo guardamos la letra (cvetpl), necesitamos saber el código original.
            // Para esto, haremos un pequeño truco: usaremos el cod_pago_excel como llave.
            
            // Pero como en el DTO no guardamos el cod_pago, lo inferimos del mapa inverso.
            // Esto es más seguro: buscamos en el mapa de tipos cuál tiene la letra que coincida.
            TipoNomina tipoEncontrado = null;
            for (TipoNomina t : tipos) {
                if (t.getCveTipo().equals(registro.getCvetpl())) {
                    tipoEncontrado = t;
                    break; // Tomamos el primero que coincida (porque ambos 'F' tienen la misma letra)
                }
            }

            // Si no encontramos ningún tipo, usamos un valor por defecto
            String codPagoKey = (tipoEncontrado != null) ? tipoEncontrado.getCodPagoExcel() : "00";
            
            // Si no existe el tipo en el mapa, lo creamos
            ResumenNominaDTO resumen = mapaResumen.get(codPagoKey);
            if (resumen == null) {
                resumen = new ResumenNominaDTO();
                
                if (tipoEncontrado != null) {
                    resumen.setTipoNomina(tipoEncontrado.getDescTipo());
                    resumen.setCveTipo(tipoEncontrado.getCveTipo());
                } else {
                    resumen.setTipoNomina("DESCONOCIDO (" + registro.getCvetpl() + ")");
                    resumen.setCveTipo("X");
                }
                
                resumen.setTotalPercepciones(0.0);
                resumen.setTotalDeducciones(0.0);
                resumen.setDetalle(new ArrayList<>());
                mapaResumen.put(codPagoKey, resumen);
            }

            // Acumulamos importes según sea Percepción (P) o Deducción (D)
            if ("P".equals(registro.getTipoConceptoFinal())) {
                resumen.setTotalPercepciones(resumen.getTotalPercepciones() + registro.getImporteFinal());
            } else if ("D".equals(registro.getTipoConceptoFinal())) {
                resumen.setTotalDeducciones(resumen.getTotalDeducciones() + registro.getImporteFinal());
            }

            // (Opcional) Agregamos el registro al detalle
            resumen.getDetalle().add(registro);
        }

        // Calculamos el neto para cada tipo
        for (ResumenNominaDTO resumen : mapaResumen.values()) {
            resumen.setNeto(resumen.getTotalPercepciones() - resumen.getTotalDeducciones());
        }

        return new ArrayList<>(mapaResumen.values());
    }
}