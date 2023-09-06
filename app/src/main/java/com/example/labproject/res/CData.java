package com.example.labproject.res;

public class CData {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String SERVER_IP = "localhost";
    private static final String PORT = "3306"; // Puerto MySQL por defecto
    private static final String DATABASE_NAME = "db_laboratorio";
    private static final String USERNAME = "Encargado";
    private static final String PASSWORD = "ENCARGADO";
    private static final String URL = "jdbc:mysql://" + SERVER_IP + ":" + PORT + "/" + DATABASE_NAME;
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
