package com.example.labproject.entidades;

public class Profesor {

    private String NOMBRE;
    private String APELLIDO_P;
    private String APELLIDO_M;
    private String BOLETA;


    public Profesor(String nombre, String apellidoP, String apellidoM, String boleta) {
        this.NOMBRE = nombre;
        this.APELLIDO_P = apellidoP;
        this.APELLIDO_M = apellidoM;
        this.BOLETA = boleta;
    }



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

    public String getBOLETA() {
        return BOLETA;
    }

    public void setBOLETA(String BOLETA) {
        this.BOLETA = BOLETA;
    }
}
