package com.gestion.Persistencia;

import com.gestion.Modelo.DatosEquipo;
import com.gestion.Modelo.EquipoComputo;
import com.gestion.Modelo.EquipoImpresion;
import com.gestion.Modelo.EquipoProyectore;
import com.gestion.Modelo.Falla;
import com.gestion.Modelo.Mantenimiento;
import com.gestion.Modelo.Usuario;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class MongoDataStore {

    private static final String DEFAULT_URI = "mongodb://localhost:27017/gestion";
    private static final MongoDataStore INSTANCE = new MongoDataStore(resolveUri());

    private final MongoClient client;
    private final MongoDatabase database;

    private MongoDataStore(String uri) {
        ConnectionString connectionString = new ConnectionString(uri);
        this.client = MongoClients.create(connectionString);
        String databaseName = connectionString.getDatabase() != null ? connectionString.getDatabase() : "gestion";
        this.database = client.getDatabase(databaseName);
    }

    public static MongoDataStore getInstance() {
        return INSTANCE;
    }

    private static String resolveUri() {
        String systemProperty = System.getProperty("gestion.mongodb.uri");
        if (systemProperty != null && !systemProperty.isBlank()) {
            return systemProperty;
        }

        String env = System.getenv("GESTION_MONGODB_URI");
        if (env != null && !env.isBlank()) {
            return env;
        }

        return DEFAULT_URI;
    }

    private MongoCollection<Document> usuariosCollection() {
        return database.getCollection("usuarios");
    }

    private MongoCollection<Document> equiposCollection() {
        return database.getCollection("equipos");
    }

    private MongoCollection<Document> mantenimientosCollection() {
        return database.getCollection("mantenimientos");
    }

    private MongoCollection<Document> fallasCollection() {
        return database.getCollection("fallas");
    }

    public List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        for (Document documento : usuariosCollection().find().sort(Sorts.ascending("cedulaId"))) {
            usuarios.add(new Usuario(
                    documento.getString("cedulaId"),
                    documento.getString("nombre"),
                    normalizarCargo(documento.getString("cargo")),
                    documento.getString("telefono"),
                    documento.getString("password")
            ));
        }
        return usuarios;
    }

    public void guardarUsuario(Usuario usuario) {
        Document documento = new Document("cedulaId", usuario.getCedulaId())
                .append("nombre", usuario.getNombre())
                .append("cargo", usuario.getCargo())
                .append("telefono", usuario.getTelefono())
                .append("password", usuario.getPassword());

        usuariosCollection().replaceOne(
                new Document("cedulaId", usuario.getCedulaId()),
                documento,
                new ReplaceOptions().upsert(true)
        );
    }

    public void eliminarUsuario(String cedulaId) {
        usuariosCollection().deleteOne(new Document("cedulaId", cedulaId));
    }

    public List<DatosEquipo> cargarEquipos() {
        List<DatosEquipo> equipos = new ArrayList<>();
        for (Document documento : equiposCollection().find().sort(Sorts.ascending("id"))) {
            DatosEquipo equipo = construirEquipo(documento);
            if (equipo != null) {
                equipos.add(equipo);
            }
        }
        return equipos;
    }

    public void guardarEquipo(DatosEquipo equipo) {
        equipo.setTipo(normalizarTipo(equipo));

        Document documento = new Document("id", equipo.getId())
                .append("nombre", equipo.getNombre())
                .append("tipo", equipo.getTipo())
                .append("cantidad", equipo.getCantidad())
                .append("estado", equipo.getEstado())
                .append("fechaIngreso", equipo.getFechaIngreso() != null ? equipo.getFechaIngreso().toString() : null)
                .append("clase", equipo.getClass().getSimpleName())
                .append("subtipo", obtenerSubtipo(equipo));

        equiposCollection().replaceOne(
                new Document("id", equipo.getId()),
                documento,
                new ReplaceOptions().upsert(true)
        );
    }

    public void eliminarEquipo(String id) {
        equiposCollection().deleteOne(new Document("id", id));
    }

    public List<Mantenimiento> cargarMantenimientos() {
        List<Mantenimiento> mantenimientos = new ArrayList<>();
        int maxId = 0;

        for (Document documento : mantenimientosCollection().find().sort(Sorts.ascending("id"))) {
            Mantenimiento mantenimiento = new Mantenimiento(
                    documento.getString("idEquipo"),
                    documento.getString("tipo"),
                    documento.getString("descripcion"),
                    documento.getString("tecnicoResponsable"),
                    parseLocalDate(documento.getString("fecha"))
            );
            int id = documento.getInteger("id", mantenimiento.getId());
            mantenimiento.setId(id);
            maxId = Math.max(maxId, id);
            mantenimientos.add(mantenimiento);
        }

        Mantenimiento.setContadorId(Math.max(maxId + 1, 1));
        return mantenimientos;
    }

    public void guardarMantenimiento(Mantenimiento mantenimiento) {
        Document documento = new Document("id", mantenimiento.getId())
                .append("idEquipo", mantenimiento.getIdEquipo())
                .append("tipo", mantenimiento.getTipo())
                .append("descripcion", mantenimiento.getDescripcion())
                .append("tecnicoResponsable", mantenimiento.getTecnicoResponsable())
                .append("fecha", mantenimiento.getFecha() != null ? mantenimiento.getFecha().toString() : null);

        mantenimientosCollection().replaceOne(
                new Document("id", mantenimiento.getId()),
                documento,
                new ReplaceOptions().upsert(true)
        );
    }

    public List<Falla> cargarFallas() {
        List<Falla> fallas = new ArrayList<>();
        int maxId = 0;

        for (Document documento : fallasCollection().find().sort(Sorts.ascending("id"))) {
            Falla falla = new Falla(
                    documento.getString("idEquipo"),
                    documento.getString("descripcion"),
                    documento.getString("usuarioReporta"),
                    parseLocalDate(documento.getString("fechaReporte"))
            );
            int id = documento.getInteger("id", falla.getId());
            falla.setId(id);
            String estado = documento.getString("estadoFalla");
            if (estado != null) {
                falla.setEstadoFalla(estado);
            }
            maxId = Math.max(maxId, id);
            fallas.add(falla);
        }

        Falla.setContadorId(Math.max(maxId + 1, 1));
        return fallas;
    }

    public void guardarFalla(Falla falla) {
        Document documento = new Document("id", falla.getId())
                .append("idEquipo", falla.getIdEquipo())
                .append("descripcion", falla.getDescripcion())
                .append("usuarioReporta", falla.getUsuarioReporta())
                .append("fechaReporte", falla.getFechaReporte() != null ? falla.getFechaReporte().toString() : null)
                .append("estadoFalla", falla.getEstadoFalla());

        fallasCollection().replaceOne(
                new Document("id", falla.getId()),
                documento,
                new ReplaceOptions().upsert(true)
        );
    }

    private DatosEquipo construirEquipo(Document documento) {
        String id = documento.getString("id");
        String nombre = documento.getString("nombre");
        String tipo = documento.getString("tipo");
        int cantidad = documento.getInteger("cantidad", 0);
        String clase = documento.getString("clase");
        String subtipo = documento.getString("subtipo");
        LocalDate fechaIngreso = parseLocalDate(documento.getString("fechaIngreso"));

        DatosEquipo equipo = switch (clase != null ? clase : "") {
            case "EquipoImpresion" -> new EquipoImpresion(id, nombre, subtipo, cantidad, fechaIngreso);
            case "EquipoProyectore" -> new EquipoProyectore(id, nombre, subtipo, cantidad, fechaIngreso);
            case "EquipoComputo" -> new EquipoComputo(id, nombre, subtipo, cantidad, fechaIngreso);
            default -> construirEquipoPorTipo(id, nombre, tipo, subtipo, cantidad, fechaIngreso);
        };

        if (tipo != null && !tipo.isBlank()) {
            equipo.setTipo(tipo);
        }

        String estado = documento.getString("estado");
        if (estado != null && !estado.isBlank()) {
            equipo.setEstado(estado);
        }

        return equipo;
    }

    private String obtenerSubtipo(DatosEquipo equipo) {
        if (equipo instanceof EquipoComputo computo) {
            return computo.getSubtipo();
        }
        if (equipo instanceof EquipoImpresion impresion) {
            return impresion.getSubTipo();
        }
        if (equipo instanceof EquipoProyectore proyector) {
            return proyector.getSubTipo();
        }
        return null;
    }

    private String normalizarTipo(DatosEquipo equipo) {
        if (equipo instanceof EquipoComputo) {
            return "Cómputo";
        }
        if (equipo instanceof EquipoImpresion) {
            return "Impresión";
        }
        if (equipo instanceof EquipoProyectore) {
            return "Proyección";
        }
        return equipo.getTipo();
    }

    private DatosEquipo construirEquipoPorTipo(String id, String nombre, String tipo, String subtipo, int cantidad, LocalDate fechaIngreso) {
        String tipoNormalizado = tipo != null ? tipo.trim() : "";

        if (tipoNormalizado.equalsIgnoreCase("Impresión") || tipoNormalizado.equalsIgnoreCase("Impresion")) {
            return new EquipoImpresion(id, nombre, subtipo, cantidad, fechaIngreso);
        }
        if (tipoNormalizado.equalsIgnoreCase("Proyección") || tipoNormalizado.equalsIgnoreCase("Proyector")) {
            return new EquipoProyectore(id, nombre, subtipo, cantidad, fechaIngreso);
        }
        return new EquipoComputo(id, nombre, subtipo, cantidad, fechaIngreso);
    }

    private LocalDate parseLocalDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDate.parse(value);
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



