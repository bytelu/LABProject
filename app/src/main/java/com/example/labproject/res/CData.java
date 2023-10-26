package com.example.labproject.res;

public class CData {

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    private static final String USERNAME = "Encargado";
    private static final String PASSWORD = "ENCARGADO";
    private static final String URL = "jdbc:mysql://192.168.3.29/db_laboratorio";
    public static String getDriver(){
        return DRIVER;
    }

    public static String getUrl() {
        return URL;
    }

    public static String getUsername() {
        return USERNAME;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
