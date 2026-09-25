package com.elahorro.sgpi.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Cifrador {

    private Cifrador() {
    }

    public static String hash(String textoPlano) {
        if (textoPlano == null) {
            throw new IllegalArgumentException("El texto a cifrar no puede ser nulo.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(textoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No se encontro el algoritmo SHA-256.", e);
        }
    }
}
