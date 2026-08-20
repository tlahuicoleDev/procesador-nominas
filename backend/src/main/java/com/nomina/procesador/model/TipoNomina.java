package com.nomina.procesador.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "no_nom_tipos_nomina")
@Data
public class TipoNomina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tipo_nomina_key")
    private Long tipoNominaKey;

    @Column(name = "cve_tipo")
    private String cveTipo; // "F", "S", "E", "H"

    @Column(name = "cod_pago_excel")
    private String codPagoExcel; // 90 al 97
    
    @Column(name = "desc_tipo")
    private String descTipo;

    @Column(name = "cta_cxp")
    private String ctaCxp;

    @Column(name = "cve_beneficiario")
    private String cveBeneficiario;

    @Column(name = "cff")
    private String cff;

    @Column(name = "nue_elabora")
    private String nueElabora;

    @Column(name = "nue_autoriza")
    private String nueAutoriza;
}