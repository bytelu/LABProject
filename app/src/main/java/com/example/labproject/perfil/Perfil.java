package com.example.labproject.perfil;

public class Perfil {
    private String name;
    private String hora_entrada;
    private String hora_salida;
    private String usuario;
    private String contrasenia;

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getName(){
        return name;
    }

    public void setName(String nombre, String apellido_m, String apellido_p){
        this.name = nombre + " " + apellido_p + " " + apellido_m;
    }

    public String getHora_entrada() {
        return hora_entrada;
    }

    public void setHora_entrada(String hora_entrada) {
        this.hora_entrada = hora_entrada;
    }

    public String getHora_salida() {
        return hora_salida;
    }

    public void setHora_salida(String hora_salida) {
        this.hora_salida = hora_salida;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}
