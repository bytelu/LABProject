package com.example.labproject.res;

public class CData {
    private static final String DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@10.110.129.175:1521/XEPDB1";
    private static final String USERNAME = "ENCARGADO";
    private static final String PASSWORD = "ENCARGADO";

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
