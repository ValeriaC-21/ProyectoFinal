package com.gestion.Exception;

/**
 * Excepción personalizada para controlar datos que violan las reglas del negocio.
 * Se lanza cuando un objeto es nulo, cadenas obligatorias se quedan vacías,
 * o se ingresan valores que no tienen sentido lógico en el sistema.
 * * Hereda de RuntimeException para poder ser usada de forma dinámica en tiempo
 * de ejecución sin obligar a llenar todo el código de cláusulas 'throws'.
 */
public class DatoInvalidoException extends RuntimeException {

    /**
     * Constructor de la excepción que recibe el mensaje de error específico.
     * @param mensaje Texto detallado que describe la violación de la regla de negocio.
     */
    public DatoInvalidoException(String mensaje) {
        // Envia el mensaje al constructor de la clase padre (RuntimeException)
        super(mensaje);
    }
}
