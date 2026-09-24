package com.elahorro.sgpi.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidades para leer y escribir archivos CSV usando FileWriter y
 * BufferedReader.
 */
public final class CsvUtil {

    public static final String SEPARADOR = ";";

    private CsvUtil() {
    }

    public static List<String[]> leer(String ruta) {
        List<String[]> filas = new ArrayList<String[]>();
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            return filas;
        }

        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(archivo));
            String linea;
            boolean primera = true;
            while ((linea = lector.readLine()) != null) {
                if (primera) {
                    primera = false;
                    continue;
                }
                if (linea.trim().isEmpty()) {
                    continue;
                }
                filas.add(linea.split(SEPARADOR, -1));
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
        } finally {
            if (lector != null) {
                try {
                    lector.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar " + ruta + ": " + e.getMessage());
                }
            }
        }
        return filas;
    }

    public static void escribir(String ruta, String cabecera, List<String[]> filas) {
        File archivo = new File(ruta);
        File carpeta = archivo.getParentFile();
        if (carpeta != null && !carpeta.exists()) {
            carpeta.mkdirs();
        }

        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(archivo));
            escritor.write(cabecera);
            escritor.newLine();
            for (int i = 0; i < filas.size(); i++) {
                escritor.write(unir(filas.get(i)));
                escritor.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir " + ruta + ": " + e.getMessage());
        } finally {
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar " + ruta + ": " + e.getMessage());
                }
            }
        }
    }

    private static String unir(String[] fila) {
        String resultado = "";
        for (int i = 0; i < fila.length; i++) {
            if (i > 0) {
                resultado = resultado + SEPARADOR;
            }
            resultado = resultado + limpiar(fila[i]);
        }
        return resultado;
    }

    private static String limpiar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.replace(SEPARADOR, ",").replace("\n", " ").replace("\r", " ");
    }
}
