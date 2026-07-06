package com.gestion.Exception;

//Excepción para evitar el registro de usuarios duplicados en el sistema.

public class UsuarioDuplicadoException extends Exception {
    public UsuarioDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
