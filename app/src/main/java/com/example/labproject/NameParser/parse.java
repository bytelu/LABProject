package com.example.labproject.NameParser;

import java.util.ArrayList;
import java.util.List;

public class parse {
    public static List<String> nameParser(String fullname){
        String[] palabras = fullname.split(" ");
        List<String> elementos = new ArrayList<>();

        StringBuilder resto = new StringBuilder();
        for (int i = 0; i < palabras.length - 2; i++){
            resto.append(palabras[i]).append(" ");
        }
        elementos.add(resto.toString().trim());
        elementos.add(palabras[palabras.length - 2]);
        elementos.add(palabras[palabras.length - 1]);

        return elementos;
    }
}
