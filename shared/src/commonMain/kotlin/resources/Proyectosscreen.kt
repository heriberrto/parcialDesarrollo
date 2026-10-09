package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Registro y administración de proyectos integradores.
 * Mapea con la tabla `proyecto_integrador` (id_asignatura, id_periodo, titulo, descripcion, objetivos, estado).
 */
class ProyectosScreen : Screen {

    companion object {
        fun isValidTitulo(titulo: String): Boolean = titulo.trim().isNotEmpty()
        fun isValidAsignatura(asignatura: String): Boolean = asignatura.trim().isNotEmpty()
        fun isValidPeriodo(periodo: String): Boolean = periodo.trim().isNotEmpty()

        /**
         * Para mantener compatibilidad con pruebas unitarias previas.
         */
        fun isValidNombreProyecto(nombre: String): Boolean = isValidTitulo(nombre)

        fun isValidProyecto(
            titulo: String,
            asignatura: String,
            periodo: String
        ): Boolean {
            return isValidTitulo(titulo) && isValidAsignatura(asignatura) && isValidPeriodo(periodo)
        }

        /**
         * Sobrecarga de compatibilidad con versión previa de 2 parámetros.
         */
        fun isValidProyecto(nombre: String, periodo: String): Boolean {
            return isValidNombreProyecto(nombre) && isValidPeriodo(periodo)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Proyectos Integradores",
            descripcion = "Registro, consulta, edición y eliminación de proyectos integradores. " +
                    "Al eliminar un proyecto también se eliminan sus equipos y asignaciones de docentes.",
            campos = listOf(
                CampoFormulario("titulo", "Título del proyecto"),
                CampoFormulario("id_asignatura", "ID de la asignatura", numerico = true),
                CampoFormulario("id_periodo", "ID del período académico", numerico = true),
                CampoFormulario("descripcion", "Descripción del proyecto"),
                CampoFormulario("objetivos", "Objetivos"),
                CampoFormulario("estado", "Estado (Planeado, En curso, Finalizado)")
            ),
            archivoListar = "listarProyecto.php",
            archivoCrear = "crearProyecto.php",
            archivoModificar = "modificarProyecto.php",
            archivoEliminar = "eliminarProyecto.php",
            tituloItem = { it.texto("titulo") },
            detalleItem = {
                "ID ${it.texto("id_proyecto")} · ${it.texto("estado")}\n" +
                        "Asignatura: ${it.texto("asignatura")} (ID ${it.texto("id_asignatura")}) · " +
                        "Período: ${it.texto("periodo")} (ID ${it.texto("id_periodo")})"
            },
            valoresParaEditar = {
                mapOf(
                    "titulo" to it.texto("titulo"),
                    "id_asignatura" to it.texto("id_asignatura"),
                    "id_periodo" to it.texto("id_periodo"),
                    "descripcion" to it.texto("descripcion"),
                    "objetivos" to it.texto("objetivos"),
                    "estado" to it.texto("estado")
                )
            },
            identificador = { mapOf("id_proyecto" to it.texto("id_proyecto")) },
            validar = { v, _ ->
                val asignatura = v["id_asignatura"].orEmpty()
                val periodo = v["id_periodo"].orEmpty()
                when {
                    !isValidProyecto(v["titulo"].orEmpty(), asignatura, periodo) ->
                        "Por favor complete el título, asignatura y período académico."
                    asignatura.toIntOrNull() == null || periodo.toIntOrNull() == null ->
                        "La asignatura y el período deben ser IDs numéricos."
                    else -> null
                }
            },
            textoGuardar = "Guardar proyecto"
        )
    }
}
