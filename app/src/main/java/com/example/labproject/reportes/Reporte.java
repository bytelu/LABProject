package com.example.labproject.reportes;

public class Reporte {
    private String ID;
    private String TITULO;
    private String FECHA;
    private String HORA;
    private String DESCRIPCION;
    private String NOMBRE;
    private String APELLIDOPAT;
    private String APELLIDOMAT;
    private String COMPU;

    private boolean expanded = false;

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void toggleExpansion() {
        expanded = !expanded;
    }

    public String getTITULO() {
        return TITULO;
    }

    public void setTITULO(String TITULO) {
        this.TITULO = TITULO;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public String getHORA() {
        return HORA;
    }

    public void setHORA(String HORA) {
        this.HORA = HORA;
    }

    public String getDESCRIPCION() {
        return DESCRIPCION;
    }

    public void setDESCRIPCION(String DESCRIPCION) {
        this.DESCRIPCION = DESCRIPCION;
    }

    public String getNOMBRE() {
        return NOMBRE;
    }

    public void setNOMBRE(String NOMBRE) {
        this.NOMBRE = NOMBRE;
    }

    public String getAPELLIDOPAT() {
        return APELLIDOPAT;
    }

    public void setAPELLIDOPAT(String APELLIDOPAT) {
        this.APELLIDOPAT = APELLIDOPAT;
    }

    public String getAPELLIDOMAT() {
        return APELLIDOMAT;
    }

    public void setAPELLIDOMAT(String APELLIDOMAT) {
        this.APELLIDOMAT = APELLIDOMAT;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCOMPU() {
        return COMPU;
    }

    public void setCOMPU(String COMPU) {
        this.COMPU = COMPU;
    }
}

