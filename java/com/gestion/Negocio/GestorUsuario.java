package com.gestion.Negocio;

import com.gestion.Exception.DatoInvalidoException;
import com.gestion.Exception.UsuarioDuplicadoException;
import com.gestion.Modelo.Usuario;
import com.gestion.Persistencia.MongoDataStore;

import java.util.ArrayList;
import java.util.List;

public class GestorUsuario {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final MongoDataStore dataStore = MongoDataStore.getInstance();

    public GestorUsuario() {
        usuarios.addAll(dataStore.cargarUsuarios());
    }

    public void registrarUsuario(Usuario usuario) throws UsuarioDuplicadoException, DatoInvalidoException {
        if (usuario == null) {
            throw new DatoInvalidoException("El usuario no puede ser nulo.");
        }

        // Validación de duplicados en el almacenamiento
        if (buscarPorCedula(usuario.getCedulaId()) != null) {
            throw new UsuarioDuplicadoException("No se puede registrar. La cédula '" + usuario.getCedulaId() + "' ya existe.");
        }

        usuario.setCargo(normalizarCargo(usuario.getCargo()));
        usuarios.add(usuario);
        dataStore.guardarUsuario(usuario);
    }

    public Usuario buscarPorCedula(String cedula) {
        for (Usuario u : usuarios) {
            if (u.getCedulaId().equals(cedula)) return u;
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        return usuarios;
    }

    public void eliminarUsuario(String cedula) throws DatoInvalidoException {
        Usuario usuarioExistente = buscarPorCedula(cedula);
        if (usuarioExistente == null) {
            throw new DatoInvalidoException("No se encontró el usuario con la cédula especificada para eliminar.");
        }

        usuarios.remove(usuarioExistente);
        dataStore.eliminarUsuario(cedula);
    }

    // ─── NUEVO MÉTODO REQUERIDO POR LA INTERFAZ GRÁFICA ───────────────────
    /**
     * Busca un usuario existente mediante su cédula y actualiza sus datos en la lista.
     */
    public void modificarUsuario(Usuario usuarioModificado) throws DatoInvalidoException {
        if (usuarioModificado == null) {
            throw new DatoInvalidoException("El usuario a modificar no puede ser nulo.");
        }

        // Buscamos el usuario en nuestra lista interna
        Usuario usuarioExistente = buscarPorCedula(usuarioModificado.getCedulaId());

        if (usuarioExistente != null) {
            // Rempazamos el usuario viejo en la lista con los nuevos datos actualizados
            usuarioModificado.setCargo(normalizarCargo(usuarioModificado.getCargo()));
            int indice = usuarios.indexOf(usuarioExistente);
            usuarios.set(indice, usuarioModificado);
            dataStore.guardarUsuario(usuarioModificado);
        } else {
            throw new DatoInvalidoException("No se encontró el usuario con la cédula especificada para modificar.");
        }
    }

    private String normalizarCargo(String cargo) {
        if (cargo == null) {
            return null;
        }

        return switch (cargo.trim()) {
            case "Técnico", "Técnico de Mantenimiento" -> "Técnico de Mantenimiento";
            case "Administrador" -> "Administrador";
            case "Operador" -> "Operador";
            default -> cargo.trim();
        };
    }
}