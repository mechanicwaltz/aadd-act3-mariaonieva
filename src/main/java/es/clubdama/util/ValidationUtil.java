package es.clubdama.util;

import java.util.regex.Pattern;

/**
 * Utilidad estática con validadores simples usados por la UI y el servicio.
 * Contiene validación de email y formato de hora (HH:mm).
 */
public final class ValidationUtil {
    private ValidationUtil() {}

    private static final Pattern EMAIL_RE = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern HORA_RE = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");

    /**
     * Valida formato básico de email.
     * @param email cadena a validar
     * @return true si el email tiene formato válido
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_RE.matcher(email).matches();
    }

    /**
     * Valida formato de hora HH:mm (24h).
     * @param hora cadena a validar
     * @return true si la hora tiene formato válido
     */
    public static boolean isValidHora(String hora) {
        if (hora == null) return false;
        return HORA_RE.matcher(hora).matches();
    }
}
