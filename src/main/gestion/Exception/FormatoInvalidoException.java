package com.gestion.Exception;

/**
 * Excepción lanzada cuando un campo (cédula, teléfono, nombre)
 * no cumple con el formato o caracteres permitidos.
 */
public class FormatoInvalidoException extends RuntimeException {
    public FormatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}