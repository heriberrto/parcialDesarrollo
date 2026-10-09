package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Asignación de docentes tutores y evaluadores.
 * Mapea con la tabla `asignacion_docente` (id_proyecto, id_usuario, id_rol_asignacion).
 */
class DocentesTutoresScreen : Screen {

    companion object {
        fun isValidDocente(docente: String): Boolean = docente.trim().isNotEmpty()
        fun isValidProyecto(proyecto: String): Boolean = proyecto.trim().isNotEmpty()
        fun isValidRolAsignacion(rol: String): Boolean = rol.trim().isNotEmpty()

        fun isValidAsignacion(docente: String, proyecto: String, rolAsignacion: String): Boolean {
            return isValidDocente(docente) && isValidProyecto(proyecto) && isValidRolAsignacion(rolAsignacion)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Docentes tutores y evaluadores",
            descripcion = "Asignación de docentes a proyectos integradores como tutores o evaluadores. " +
                    "Use el ID del docente (lista de Usuarios) y el ID del proyecto (lista de Proyectos).",
            campos = listOf(
                CampoFormulario("id_usuario", "ID del docente", numerico = true),
                CampoFormulario("id_proyecto", "ID del proyecto integrador", numerico = true),
                CampoFormulario("rol_asignacion", "Rol de asignación (Tutor, Evaluador)")
            ),
            archivoListar = "listarAsignacionDocente.php",
            archivoCrear = "crearAsignacionDocente.php",
            archivoModificar = "modificarAsignacionDocente.php",
            archivoEliminar = "eliminarAsignacionDocente.php",
            tituloItem = { "${it.texto("docente")} - ${it.texto("rol")}" },
            detalleItem = {
                "Docente ID ${it.texto("id_usuario")} · Proyecto: ${it.texto("proyecto")} (ID ${it.texto("id_proyecto")})"
            },
            valoresParaEditar = {
                mapOf(
                    "id_usuario" to it.texto("id_usuario"),
                    "id_proyecto" to it.texto("id_proyecto"),
                    "rol_asignacion" to it.texto("rol").ifEmpty { it.texto("id_rol_asignacion") }
                )
            },
            // La asignación se identifica por sus valores originales
            identificador = {
                mapOf(
                    "orig_id_proyecto" to it.texto("id_proyecto"),
                    "orig_id_usuario" to it.texto("id_usuario"),
                    "orig_id_rol_asignacion" to it.texto("id_rol_asignacion")
                )
            },
            validar = { v, _ ->
                val docente = v["id_usuario"].orEmpty()
                val proyecto = v["id_proyecto"].orEmpty()
                when {
                    !isValidAsignacion(docente, proyecto, v["rol_asignacion"].orEmpty()) ->
                        "Por favor complete el docente, proyecto y rol de asignación."
                    docente.toIntOrNull() == null || proyecto.toIntOrNull() == null ->
                        "El docente y el proyecto deben ser IDs numéricos."
                    else -> null
                }
            },
            textoGuardar = "Guardar asignación"
        )
    }
}
