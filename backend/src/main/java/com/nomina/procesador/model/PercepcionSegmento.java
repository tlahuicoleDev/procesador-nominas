package com.nomina.procesador.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "no_nom_percepciones_seg")
@Data
public class PercepcionSegmento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cve_concepto")
    private String cveConcepto;

    @Column(name = "nue")
    private String nue;

    @Column(name = "nup")
    private String nup;

    @Column(name = "seg01")
    private String seg01;
    @Column(name = "seg02")
    private String seg02;
    @Column(name = "seg03")
    private String seg03;
    @Column(name = "seg04")
    private String seg04;
    @Column(name = "seg05")
    private String seg05;
    @Column(name = "seg06")
    private String seg06;
    @Column(name = "seg07")
    private String seg07;
    @Column(name = "seg08")
    private String seg08;
}