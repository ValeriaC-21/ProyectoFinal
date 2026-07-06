package com.gestion.Interfaz;

// interfaz/Sistema.java

import com.gestion.Exception.DatoInvalidoException;
import com.gestion.Exception.FormatoInvalidoException;
import com.gestion.Exception.UsuarioDuplicadoException;
import com.gestion.Modelo.*;
import com.gestion.Negocio.*;

import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Sistema {

    // Instancias globales de los controladores de negocio
    public static final GestorEquipo gestorEquipo = new GestorEquipo();
    public static final GestorMantenimiento gestorMant = new GestorMantenimiento();
    public static final GestorFallas gestorFallas = new GestorFallas();
    public static final GestorReporte gestorReporte = new GestorReporte();
    public static final GestorUsuario gestorUsuario = new GestorUsuario();
    public static final Scanner sc = new Scanner(System.in);

    public int menu() {
        while (true) {
            try {
                System.out.println("\n====================================");
                System.out.println("  Sistema de Gestión de Mantenimiento");
                System.out.println("====================================");
                System.out.println("1. Gestión de usuarios");
                System.out.println("2. Gestión de equipos");
                System.out.println("3. Gestión de mantenimiento");
                System.out.println("4. Gestión de fallas");
                System.out.println("5. Control y seguimiento");
                System.out.print("Opción: ");
                int opcion = sc.nextInt();
                sc.nextLine(); // Limpiar búfer
                return opcion;
            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR] Entrada inválida. Ingrese un número entero.");
                sc.nextLine(); // Limpiar búfer corrupto
            }
        }
    }

    /**
     * Muestra el menú de bienvenida y valida los datos campo por campo en tiempo real.
     */
    public Usuario identificarORegistrarUsuarioInicial() {
        while (true) {
            try {
                System.out.println("\n--- ACCESO AL SISTEMA ---");
                System.out.println("1. Ya estoy registrado (Iniciar Sesión)");
                System.out.println("2. Registrarse como usuario nuevo");
                System.out.print("Opción: ");
                int opcionAcceso = sc.nextInt();
                sc.nextLine(); // Limpiar búfer

                switch (opcionAcceso) {
                    case 1:
                        System.out.print("\nIngrese su Cédula/ID: ");
                        String cedulaLogin = sc.nextLine().trim();

                        Usuario usuarioExistente = gestorUsuario.buscarPorCedula(cedulaLogin);

                        if (usuarioExistente != null) {
                            return usuarioExistente;
                        } else {
                            System.out.println("\n[AVISO] La cédula ingresada no existe en el sistema.");
                            System.out.println("Por favor, verifique el número o elija la opción 2 para registrarse.");
                        }
                        break;

                    case 2:
                        System.out.println("\n--- Formulario de Registro Obligatorio ---");

                        String cedulaNueva = "";
                        String nombre = "";
                        String telefono = "";
                        String contrasenia = ""; // ✔ SOLUCIONADO: Se declara la variable correctamente
                        String cargo = "";

                        // 1. VALIDACIÓN INMEDIATA DE CÉDULA
                        while (true) {
                            try {
                                System.out.print("Ingrese Cédula (10 dígitos): ");
                                cedulaNueva = sc.nextLine();
                                Usuario.validarCedula(cedulaNueva); // Valida al instante

                                // Validación extra: que no esté repetida en el gestor antes de continuar
                                if (gestorUsuario.buscarPorCedula(cedulaNueva.trim()) != null) {
                                    throw new UsuarioDuplicadoException("Esta cédula ya está registrada en el sistema.");
                                }
                                break; // Si pasa las validaciones, sale del bucle de la cédula
                            } catch (FormatoInvalidoException | UsuarioDuplicadoException e) {
                                System.out.println("[ERROR INMEDIATO] " + e.getMessage() + " Intente de nuevo.\n");
                            }
                        }

                        // 2. VALIDACIÓN INMEDIATA DE NOMBRE
                        while (true) {
                            try {
                                System.out.print("Ingrese Nombre Completo (Solo letras): ");
                                nombre = sc.nextLine();
                                Usuario.validarNombre(nombre); // Valida al instante
                                break;
                            } catch (FormatoInvalidoException e) {
                                System.out.println("[ERROR INMEDIATO] " + e.getMessage() + " Intente de nuevo.\n");
                            }
                        }

                        // 3. VALIDACIÓN INMEDIATA DE TELÉFONO
                        while (true) {
                            try {
                                System.out.print("Ingrese Teléfono de Contacto: ");
                                telefono = sc.nextLine();
                                Usuario.validarTelefono(telefono); // Valida al instante
                                break;
                            } catch (FormatoInvalidoException e) {
                                System.out.println("[ERROR INMEDIATO] " + e.getMessage() + " Intente de nuevo.\n");
                            }
                        }

                        // 4. VALIDACIÓN INMEDIATA DE CONTRASEÑA
                        while (true) {
                            System.out.print("Ingrese Contraseña: ");
                            contrasenia = sc.nextLine();
                            if (contrasenia.trim().isEmpty()) {
                                System.out.println("[ERROR INMEDIATO] La contraseña no puede quedar vacía. Intente de nuevo.\n");
                            } else {
                                break;
                            }
                        }

                        // 5. SELECCIÓN DE CARGO
                        while (true) {
                            try {
                                System.out.println("Seleccione su cargo:");
                                System.out.println("1. Técnico\n2. Administrador\n3. Operador");
                                System.out.print("Opción: ");
                                int cargoOp = sc.nextInt(); sc.nextLine(); // Limpiar búfer

                                cargo = switch (cargoOp) {
                                    case 1 -> "Técnico";
                                    case 2 -> "Administrador";
                                    case 3 -> "Operador";
                                    default -> "";
                                };

                                if (!cargo.isEmpty()) {
                                    break; // Cargo válido
                                }
                                System.out.println("[AVISO] Opción fuera de rango. Seleccione 1, 2 o 3.\n");
                            } catch (InputMismatchException e) {
                                System.out.println("[ERROR] Debe ingresar un número entero.\n");
                                sc.nextLine(); // Limpiar búfer corrupto
                            }
                        }

                        // Cuando llega aquí, todos los datos están 100% limpios y validados con los 5 parámetros
                        Usuario nuevoUsuario = new Usuario(cedulaNueva.trim(), nombre.trim(), cargo, telefono.trim(), contrasenia.trim());
                        gestorUsuario.registrarUsuario(nuevoUsuario);

                        System.out.println("\n✔ ¡Registro completado con éxito!");
                        return nuevoUsuario;

                    default:
                        System.out.println("[AVISO] Opción no válida. Seleccione 1 o 2.");
                        break;
                }

            } catch (InputMismatchException e) {
                System.out.println("\n[ERROR] Entrada inválida. Debe ingresar un número entero para el menú.");
                sc.nextLine();
            } catch (Exception e) {
                System.out.println("\n[ERROR GENERAL] Ocurrió un inconveniente: " + e.getMessage());
            }
        }
    }

    // ─── MÓDULO NUEVO: GESTIÓN DE USUARIOS ───────────────────────────

    public void menuUsuarios() {
        try {
            System.out.println("\n--- Gestión de Usuarios ---");
            System.out.println("1. Registrar usuario / técnico");
            System.out.println("2. Consultar usuarios");
            System.out.print("Opción: ");
            int op = sc.nextInt(); sc.nextLine();

            switch (op) {
                case 1:
                    registrarUsuario();
                    break;
                case 2:
                    consultarUsuarios();
                    break;
                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        } catch (InputMismatchException e) {
            System.out.println("[ERROR] Debe ingresar un número entero.");
            sc.nextLine();
        }
    }

    public void registrarUsuario() {
        try {
            System.out.println("\n--- Formulario de Registro de Usuario ---");
            System.out.print("Ingrese Cédula (10 dígitos): ");
            String cedula = sc.nextLine();

            System.out.print("Ingrese Nombre Completo (Solo letras): ");
            String nombre = sc.nextLine();

            System.out.print("Ingrese Teléfono de Contacto: ");
            String telefono = sc.nextLine();

            System.out.print("Ingrese Contraseña: ");
            String contrasenia = sc.nextLine();

            System.out.println("Seleccione el cargo:");
            System.out.println("1. Técnico\n2. Administrador\n3. Operador");
            System.out.print("Opción: ");
            int cargoOp = sc.nextInt(); sc.nextLine(); // Limpiar búfer

            String cargo = switch (cargoOp) {
                case 1 -> "Técnico";
                case 2 -> "Administrador";
                case 3 -> "Operador";
                default -> "General";
            };

            // Intentamos construir el objeto con los 5 parámetros requeridos.
            Usuario nuevoUsuario = new Usuario(cedula, nombre, cargo, telefono, contrasenia);

            // Intentamos guardarlo en la lista.
            gestorUsuario.registrarUsuario(nuevoUsuario);
            System.out.println("\n✔ ¡Usuario registrado exitosamente en el sistema!");

        } catch (FormatoInvalidoException e) {
            System.out.println("\n[ERROR DE FORMATO] " + e.getMessage());
            System.out.println("-> Registro cancelado. Por favor, intente de nuevo.");
        } catch (UsuarioDuplicadoException e) {
            System.out.println("\n[ERROR DE DUPLICADO] " + e.getMessage());
        } catch (DatoInvalidoException e) {
            System.out.println("\n[ERROR LÓGICO] " + e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("\n[ERROR DE ENTRADA] Ingresó caracteres alfabéticos en una opción numérica.");
            sc.nextLine(); // Limpieza del búfer obligatoria
        }
    }

    public void consultarUsuarios() {
        List<Usuario> lista = gestorUsuario.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        System.out.println("\n--- Lista de usuarios registrados ---");
        for (Usuario u : lista) {
            System.out.println(u);
        }
    }

    // ─── MÓDULO 1: Gestión de equipos ───────────────────────────────

    public void menuEquipos() {
        try {
            System.out.println("\n--- Gestión de Equipos ---");
            System.out.println("1. Registrar equipo");
            System.out.println("2. Consultar equipos");
            System.out.print("Opción: ");
            int op = sc.nextInt(); sc.nextLine();

            switch (op) {
                case 1 -> registrarEquipo();
                case 2 -> consultarEquipos();
                default -> System.out.println("Opción no válida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("[ERROR] Ingrese un número entero.");
            sc.nextLine();
        }
    }

    public void registrarEquipo() {
        try {
            System.out.println("\nTipo de equipo:");
            System.out.println("1. Cómputo   2. Impresión   3. Proyección");
            System.out.print("Opción: ");
            int tipo = sc.nextInt(); sc.nextLine();

            List<String> subtipos = switch (tipo) {
                case 1 -> EquipoComputo.getSubTipos();
                case 2 -> EquipoImpresion.getSubTipos();
                case 3 -> EquipoProyectore.getSubtipos();
                default -> null;
            };

            if (subtipos == null) { System.out.println("Tipo no válido."); return; }

            System.out.println("Subtipo:");
            for (int i = 0; i < subtipos.size(); i++)
                System.out.println((i + 1) + ". " + subtipos.get(i));
            System.out.print("Opción: ");
            int subOp = sc.nextInt() - 1; sc.nextLine();

            if (subOp < 0 || subOp >= subtipos.size()) {
                System.out.println("Subtipo fuera de rango.");
                return;
            }
            String subtipo = subtipos.get(subOp);

            System.out.print("ID del equipo: ");
            String id = sc.nextLine();
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Cantidad: ");
            int cantidad = sc.nextInt(); sc.nextLine();

            DatosEquipo equipo = switch (tipo) {
                case 1 -> new EquipoComputo(id, nombre, subtipo, cantidad, LocalDate.now());
                case 2 -> new EquipoImpresion(id, nombre, subtipo, cantidad, LocalDate.now());
                case 3 -> new EquipoProyectore(id, nombre, subtipo, cantidad, LocalDate.now());
                default -> null;
            };

            gestorEquipo.agregarEquipo(equipo);
            System.out.println("\n✔ Equipo agregado exitosamente.");
        } catch (Exception e) {
            System.out.println("\n[ERROR CONTROLADO] " + e.getMessage());
        }
    }

    public void consultarEquipos() {
        List<DatosEquipo> lista = gestorEquipo.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }
        System.out.println("\n--- Lista de equipos ---");
        for (DatosEquipo e : lista)
            System.out.println(e.obtenerFicha());
    }

    // ─── MÓDULO 2: Gestión de mantenimiento ─────────────────────────

    public void menuMantenimiento() {
        try {
            System.out.println("\n--- Gestión de Mantenimiento ---");
            System.out.println("1. Registrar mantenimiento ");
            System.out.println("2. Consultar historial");
            System.out.print("Opción: ");
            int op = sc.nextInt(); sc.nextLine();

            switch (op) {
                case 1 -> registrarMantenimiento();
                case 2 -> consultarHistorial();
                default -> System.out.println("Opción no válida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("[ERROR] Opción inválida.");
            sc.nextLine();
        }
    }

    public void registrarMantenimiento() {
        try {
            List<DatosEquipo> equipos = gestorEquipo.listarTodos();

            if (equipos.isEmpty()) {
                System.out.println("No hay equipos registrados para dar mantenimiento.");
                return;
            }

            System.out.println("\n--- Equipos registrados ---");
            for (int i = 0; i < equipos.size(); i++) {
                System.out.println((i + 1) + ". " + equipos.get(i).getNombre()
                        + " | ID: "     + equipos.get(i).getId()
                        + " | Estado: " + equipos.get(i).getEstado());
            }

            System.out.print("Seleccione el equipo (número): ");
            int seleccion = sc.nextInt(); sc.nextLine();

            if (seleccion < 1 || seleccion > equipos.size()) {
                System.out.println("Selección no válida.");
                return;
            }

            DatosEquipo equipoSeleccionado = equipos.get(seleccion - 1);
            String idEquipo = equipoSeleccionado.getId();

            System.out.println("\nTipo de mantenimiento:");
            System.out.println("1. Preventivo\n2. Correctivo\n3. Diagnóstico");
            System.out.print("Opción: ");
            int tipoOp = sc.nextInt(); sc.nextLine();

            String tipo = switch (tipoOp) {
                case 1 -> "Preventivo";
                case 2 -> "Correctivo";
                case 3 -> "Diagnóstico";
                default -> "Desconocido";
            };

            System.out.print("Descripción: ");
            String desc = sc.nextLine();

            System.out.print("Cédula del Técnico responsable: ");
            String cedulaTec = sc.nextLine();

            // Validación cruzada con el Gestor de Usuarios
            Usuario tecnico = gestorUsuario.buscarPorCedula(cedulaTec);
            if (tecnico == null) {
                throw new DatoInvalidoException("El técnico con cédula '" + cedulaTec + "' no está registrado. Regístrelo primero en el módulo de Usuarios.");
            }

            gestorMant.registrar(idEquipo, tipo, desc, tecnico.getNombre(), LocalDate.now());
            gestorEquipo.actualizarEstado(idEquipo, "en mantenimiento");

            System.out.println("\n✔ Mantenimiento asignado correctamente al técnico: " + tecnico.getNombre());
            System.out.println("  Equipo '" + equipoSeleccionado.getNombre() + "' marcado como: en mantenimiento");
        } catch (Exception e) {
            System.out.println("\n[ERROR EN OPERACIÓN] " + e.getMessage());
        }
    }

    public void consultarHistorial() {
        List<DatosEquipo> equipos = gestorEquipo.listarTodos();

        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }

        System.out.println("\n--- Equipos registrados ---");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombre()
                    + " | ID: "     + equipos.get(i).getId()
                    + " | Estado actual en sistema: " + equipos.get(i).getEstado());
        }

        System.out.print("Seleccione el equipo (número) para ver su historial: ");
        int seleccion = sc.nextInt(); sc.nextLine();

        if (seleccion < 1 || seleccion > equipos.size()) {
            System.out.println("Selección no válida.");
            return;
        }

        DatosEquipo equipoSeleccionado = equipos.get(seleccion - 1);
        String idEquipo = equipoSeleccionado.getId();

        // Obtenemos los registros de mantenimiento de este equipo
        List<Mantenimiento> historial = gestorMant.consultarPorEquipo(idEquipo);

        if (historial.isEmpty()) {
            System.out.println("El equipo '" + equipoSeleccionado.getNombre()
                    + "' no tiene mantenimientos registrados.");
            return;
        }

        System.out.println("\n=======================================================");
        System.out.println("   HISTORIAL DE MANTENIMIENTO: " + equipoSeleccionado.getNombre().toUpperCase());
        System.out.println("   ESTADO ACTUAL DEL EQUIPO: " + equipoSeleccionado.getEstado().toUpperCase());
        System.out.println("=======================================================");

        for (Mantenimiento m : historial) {
            System.out.println(m);
        }
        System.out.println("=======================================================");
    }

    // ─── MÓDULO 4: Gestión de fallas ────────────────────────────────

    public void menuFallas() {
        try {
            System.out.println("\n--- Gestión de Fallas ---");
            System.out.println("1. Reportar falla");
            System.out.println("2. Actualizar estado del equipo");
            System.out.print("Opción: ");
            int op = sc.nextInt(); sc.nextLine();

            switch (op) {
                case 1 -> reportarFalla();
                case 2 -> actualizarEstado();
                default -> System.out.println("Opción no válida.");
            }
        } catch (InputMismatchException e) {
            System.out.println("[ERROR] Ingrese un número entero.");
            sc.nextLine();
        }
    }

    public void reportarFalla() {
        List<DatosEquipo> equipos = gestorEquipo.listarTodos();

        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados para reportar falla.");
            return;
        }

        System.out.println("\n--- Equipos registrados ---");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombre()
                    + " | ID: " + equipos.get(i).getId()
                    + " | Estado: " + equipos.get(i).getEstado());
        }

        System.out.print("Seleccione el equipo (número): ");
        int seleccion = sc.nextInt(); sc.nextLine();

        if (seleccion < 1 || seleccion > equipos.size()) {
            System.out.println("Selección no válida.");
            return;
        }

        DatosEquipo equipoSeleccionado = equipos.get(seleccion - 1);
        String idEquipo = equipoSeleccionado.getId();

        System.out.print("Descripción de la falla: ");
        String desc = sc.nextLine();
        System.out.print("Tu nombre (usuario que reporta): ");
        String usuario = sc.nextLine();

        try {
            gestorFallas.reportar(idEquipo, desc, usuario, LocalDate.now());
            gestorEquipo.actualizarEstado(idEquipo, "en mantenimiento");

            System.out.println("\n✔ Falla registrada correctamente");
            System.out.println("  Equipo '" + equipoSeleccionado.getNombre() + "' marcado como: en mantenimiento");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    public void actualizarEstado() {
        List<DatosEquipo> equipos = gestorEquipo.listarTodos();

        if (equipos.isEmpty()) {
            System.out.println("No hay equipos registrados.");
            return;
        }

        System.out.println("\n--- Equipos registrados ---");
        for (int i = 0; i < equipos.size(); i++) {
            System.out.println((i + 1) + ". " + equipos.get(i).getNombre()
                    + " | ID: "     + equipos.get(i).getId()
                    + " | Estado actual: " + equipos.get(i).getEstado());
        }

        System.out.print("Seleccione el equipo (número): ");
        int seleccion = sc.nextInt(); sc.nextLine();

        if (seleccion < 1 || seleccion > equipos.size()) {
            System.out.println("Selección no válida.");
            return;
        }

        String idEquipo = equipos.get(seleccion - 1).getId();

        System.out.println("\nNuevo estado:");
        System.out.println("1. Operativo\n2. En mantenimiento\n3. Fuera de servicio");
        System.out.print("Opción: ");
        int op = sc.nextInt(); sc.nextLine();

        String estado = switch (op) {
            case 1 -> "operativo";
            case 2 -> "en mantenimiento";
            case 3 -> "fuera de servicio";
            default -> "desconocido";
        };

        try {
            gestorEquipo.actualizarEstado(idEquipo, estado);
            gestorFallas.actualizarEstado(idEquipo, estado);

            System.out.println("\n✔ Estado actualizado correctamente.");
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    // ─── MÓDULO 5: Control y seguimiento ────────────────────────────

    public void menuReportes() {
        System.out.println("\n--- Control y Seguimiento ---");
        System.out.println("1. Ver reportes de fallas");
        System.out.println("2. Ver historial de mantenimientos");
        System.out.print("Opción: ");
        int op = sc.nextInt(); sc.nextLine();

        switch (op) {
            case 1 -> gestorReporte.consultarFallas(gestorFallas);
            case 2 -> gestorReporte.consultarMantenimientos(gestorMant);
            default -> System.out.println("Opción no válida.");
        }
    }
}