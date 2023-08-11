package com.example.labproject.alumnos;

import java.util.Date;
public class Alumno {
    private String NOMBRE;
    private String APELLIDO_P;
    private String APELLIDO_M;
    private String CARRERA;
    private int BOLETA;
    private int SEMESTRE;

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getAPELLIDO_P() {
        return APELLIDO_P;
    }

    public void setAPELLIDO_P(String APELLIDO_P) {
        this.APELLIDO_P = APELLIDO_P;
    }

    public String getAPELLIDO_M() {
        return APELLIDO_M;
    }

    public void setAPELLIDO_M(String APELLIDO_M) {
        this.APELLIDO_M = APELLIDO_M;
    }

    public String getCARRERA() {
        return CARRERA;
    }

    public void setCARRERA(String CARRERA) {
        this.CARRERA = CARRERA;
    }

    public int getBOLETA() {
        return BOLETA;
    }

    public void setBOLETA(int BOLETA) {
        this.BOLETA = BOLETA;
    }

    public int getSEMESTRE() {
        return SEMESTRE;
    }

    public void setSEMESTRE(int SEMESTRE) {
        this.SEMESTRE = SEMESTRE;
    }
}
