package com.elahorro.sgpi.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexion a la base de datos MySQL. Implementa el patron Singleton: una sola
 * instancia lee la configuracion y entrega conexiones JDBC.
 *
 * El orden de prioridad para la configuracion es:
 *   1. Variables de entorno (SGPI_DB_URL, SGPI_DB_USER, SGPI_DB_PASSWORD).
 *   2. Propiedades del sistema (sgpi.db.url, sgpi.db.user, sgpi.db.password).
 *   3. Archivo src/main/resources/db.properties.
 */
public final class Conexion {

    private static final String ARCHIVO = "db.properties";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    private static Conexion instancia;

    private final String url;
    private final String usuario;
    private final String password;

    private Conexion() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(ARCHIVO)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + ARCHIVO, e);
        }

        this.url = primero(
                System.getenv("SGPI_DB_URL"),
                System.getProperty("sgpi.db.url"),
                props.getProperty("db.url"));
        this.usuario = primero(
                System.getenv("SGPI_DB_USER"),
                System.getProperty("sgpi.db.user"),
                props.getProperty("db.user"));
        this.password = primero(
                System.getenv("SGPI_DB_PASSWORD"),
                System.getProperty("sgpi.db.password"),
                props.getProperty("db.password"));

        if (url == null) {
            throw new IllegalStateException(
                    "Configure db.url en src/main/resources/db.properties "
                            + "o la variable de entorno SGPI_DB_URL.");
        }
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "No se encontro el driver JDBC de MySQL (" + DRIVER + "). "
                            + "Verifique la dependencia mysql-connector-j.", e);
        }
    }

    private static String primero(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.isBlank()) {
                return valor.trim();
            }
        }
        return null;
    }

    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /** Abre una nueva conexion JDBC. El llamador debe cerrarla. */
    public Connection abrir() throws SQLException {
        return DriverManager.getConnection(url, usuario, password);
    }

    public static Connection getConnection() throws SQLException {
        return getInstancia().abrir();
    }

    public String getUrl() {
        return url;
    }

    /** Nombre de la base de datos extraido de la URL (util para pruebas). */
    public String getNombreBaseDatos() {
        String sinParametros = url.split("\\?")[0];
        int corte = sinParametros.lastIndexOf('/');
        return corte >= 0 ? sinParametros.substring(corte + 1) : sinParametros;
    }
}
