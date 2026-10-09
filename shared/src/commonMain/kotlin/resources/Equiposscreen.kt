package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Gestión de equipos de estudiantes.
 * Mapea con las tablas `equipo` (id_proyecto, nombre, estado) y `equipo_estudiante`.
 */
class EquiposScreen : Screen {

    companion object {
        fun isValidProyecto(proyecto: String): Boolean = proyecto.trim().isNotEmpty()
        fun isValidNombreEquipo(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidIntegrantes(integrantes: String): Boolean = integrantes.trim().isNotEmpty()

        fun isValidEquipo(proyecto: String, nombreEquipo: String, integrantes: String): Boolean {
            return isValidProyecto(proyecto) && isValidNombreEquipo(nombreEquipo) && isValidIntegrantes(integrantes)
        }

        /** Los integrantes deben ser IDs numéricos separados por comas, ej: "1,2,3". */
        fun isValidListaIds(integrantes: String): Boolean =
            integrantes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                .let { ids -> ids.isNotEmpty() && ids.all { it.toIntOrNull() != null } }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Equipos de estudiantes",
            descripcion = "Creación, consulta, edición y eliminación de equipos por proyecto. " +
                    "Los integrantes se escriben como IDs de usuario separados por comas (ej: 3,5,8).",
            campos = listOf(
                CampoFormulario("id_proyecto", "ID del proyecto integrador", numerico = true),
                CampoFormulario("nombre", "Nombre del equipo"),
                CampoFormulario("integrantes", "IDs de los integrantes (ej: 3,5,8)")
            ),
            archivoListar = "listarEquipo.php",
            archivoCrear = "crearEquipo.php",
            archivoModificar = "modificarEquipo.php",
            archivoEliminar = "eliminarEquipo.php",
            tituloItem = { it.texto("nombre") },
            detalleItem = {
                "ID ${it.texto("id_equipo")} · Proyecto: ${it.texto("proyecto")} (ID ${it.texto("id_proyecto")})\n" +
                        "Integrantes: ${it.texto("nombres_integrantes").ifEmpty { "ninguno" }}"
            },
            valoresParaEditar = {
                mapOf(
                    "id_proyecto" to it.texto("id_proyecto"),
                    "nombre" to it.texto("nombre"),
                    "integrantes" to it.texto("integrantes")
                )
            },
            identificador = { mapOf("id_equipo" to it.texto("id_equipo")) },
            validar = { v, _ ->
                val proyecto = v["id_proyecto"].orEmpty()
                val integrantes = v["integrantes"].orEmpty()
                when {
                    !isValidEquipo(proyecto, v["nombre"].orEmpty(), integrantes) ->
                        "Por favor complete el proyecto, nombre del equipo e integrantes."
                    proyecto.toIntOrNull() == null -> "El proyecto debe ser un ID numérico."
                    !isValidListaIds(integrantes) -> "Los integrantes deben ser IDs numéricos separados por comas."
                    else -> null
                }
            },
            textoGuardar = "Guardar equipo"
        )
    }
}
