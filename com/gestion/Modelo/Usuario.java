package com.gestion.Modelo;

import com.gestion.Exception.FormatoInvalidoException;

public class Usuario {
    private String cedulaId;
    private String nombre;
    private String cargo;
    private String telefono;
    private String password;

    // El constructor ahora está seguro de que recibirá datos limpios gracias a la interfaz
    public Usuario(String cedulaId, String nombre, String cargo, String telefono, String password) {
        this.cedulaId = cedulaId;
        this.nombre = nombre;
        this.cargo = cargo;
        this.telefono = telefono;
        this.password = password;
    }

    // --- MÉTODOS DE VALIDACIÓN CAMPO POR CAMPO ---

    public static void validarCedula(String cedula) {
        if (cedula == null || cedula.trim().isEmpty()) {
            throw new FormatoInvalidoException("La cédula no puede estar vacía.");
        }
        String limpia = cedula.trim();
        if (!limpia.matches("^[0-9]+$")) {
            throw new FormatoInvalidoException("La cédula debe contener únicamente números positivos. No se permiten letras ni signos (-) negativos.");
        }
        if (limpia.length() != 10) {
            throw new FormatoInvalidoException("La cédula debe tener exactamente 10 dígitos numéricos.");
        }
    }

    public static void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new FormatoInvalidoException("El nombre no puede estar vacío.");
        }
        String limpio = nombre.trim();
        if (!limpio.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new FormatoInvalidoException("El nombre no debe contener números ni caracteres especiales (ej: @, $, -, *, _).");
        }
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
            throw new FormatoInvalidoException("El nombre solo debe contener letras (caracteres alfabéticos).");
        }
    }

    public static void validarTelefono(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new FormatoInvalidoException("El teléfono no puede estar vacío.");
        }
        String limpio = telefono.trim();
        if (!limpio.matches("^[0-9]+$")) {
            throw new FormatoInvalidoException("El número de teléfono debe contener solo dígitos numéricos (sin letras ni guiones).");
        }
    }

    // Getters básicos
    public String getCedulaId() { return cedulaId; }
    public String getNombre() { return nombre; }
    public String getCargo() { return cargo; }
    public String getTelefono() { return telefono; }
    public String getPassword() { return password; }

    public void setCedulaId(String cedulaId) {
        this.cedulaId = cedulaId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "[USUARIO] Cédula: " + cedulaId + " | Nombre: " + nombre + " | Cargo: " + cargo + " | Tel: " + telefono;
    }
}