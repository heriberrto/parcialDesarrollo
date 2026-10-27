package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class CalificacionesScreen : Screen {

    companion object {
        fun isValidIdActividad(idActividad: String): Boolean =
            idActividad.isNotBlank() && idActividad.trim().toIntOrNull() != null

        fun isValidIdUsuario(idUsuario: String): Boolean =
            idUsuario.isNotBlank() && idUsuario.trim().toIntOrNull() != null

        fun isValidIdEvidencia(idEvidencia: String): Boolean =
            idEvidencia.isNotBlank() && idEvidencia.trim().toIntOrNull() != null

        fun isValidCalificacion(idActividad: String, idUsuario: String, idEvidencia: String): Boolean {
            return isValidIdActividad(idActividad) && isValidIdUsuario(idUsuario) && isValidIdEvidencia(idEvidencia)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Gestión de Calificaciones",
            descripcion = "Evaluación y asignación de notas a las evidencias enviadas por estudiantes.",
            campos = listOf(
                CampoFormulario("id_evidencia", "ID de la Evidencia", numerico = true),
                CampoFormulario("id_actividad", "ID de la Actividad", numerico = true),
                CampoFormulario("id_usuario", "ID del Estudiante", numerico = true),
                CampoFormulario("nota", "Nota / Calificación (ej. 4.5)"),
                CampoFormulario("observaciones", "Observaciones / Retroalimentación")
            ),
            archivoListar = "listarCalificacion.php",
            archivoCrear = "crearCalificacion.php",
            archivoModificar = "modificarCalificacion.php",
            archivoEliminar = "eliminarCalificacion.php",
            tituloItem = { "Nota: ${it.texto("nota")} - ${it.texto("evidencia")}" },
            detalleItem = {
                "ID ${it.texto("id_calificacion")} · Estudiante: ${it.texto("estudiante")} (ID ${it.texto("id_usuario")})\n" +
                        "Actividad: ${it.texto("actividad")} (ID ${it.texto("id_actividad")})\n" +
                        "Obs: ${it.texto("observaciones")}"
            },
            valoresParaEditar = {
                mapOf(
                    "id_evidencia" to it.texto("id_evidencia"),
                    "id_actividad" to it.texto("id_actividad"),
                    "id_usuario" to it.texto("id_usuario"),
                    "nota" to it.texto("nota"),
                    "observaciones" to it.texto("observaciones")
                )
            },
            identificador = { mapOf("id_calificacion" to it.texto("id_calificacion")) },
            validar = { v, _ ->
                val evidencia = v["id_evidencia"].orEmpty()
                val actividad = v["id_actividad"].orEmpty()
                val usuario = v["id_usuario"].orEmpty()
                when {
                    !isValidCalificacion(actividad, usuario, evidencia) -> "Evidencia, Actividad y Estudiante deben ser IDs numéricos válidos."
                    v["nota"].orEmpty().toDoubleOrNull() == null -> "La nota debe ser un número válido (ej. 4.5)."
                    else -> null
                }
            },
            textoGuardar = "Guardar calificación"
        )
    }
}
