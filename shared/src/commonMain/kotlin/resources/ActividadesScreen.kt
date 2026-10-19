package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class ActividadesScreen : Screen {

    companion object {
        fun isValidTitulo(titulo: String): Boolean = titulo.trim().isNotEmpty()
        fun isValidProyecto(proyecto: String): Boolean = proyecto.trim().isNotEmpty()

        fun isValidActividad(titulo: String, proyecto: String): Boolean {
            return isValidTitulo(titulo) && isValidProyecto(proyecto)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Gestión de Actividades",
            descripcion = "Creación, consulta, actualización y eliminación de actividades de proyectos integradores.",
            campos = listOf(
                CampoFormulario("id_proyecto", "ID del Proyecto Integrador", numerico = true),
                CampoFormulario("titulo", "Título de la actividad"),
                CampoFormulario("descripcion", "Descripción de la actividad"),
                CampoFormulario("fecha_inicio", "Fecha de inicio (YYYY-MM-DD)"),
                CampoFormulario("fecha_fin", "Fecha de finalización (YYYY-MM-DD)"),
                CampoFormulario("estado", "Estado (Pendiente, En progreso, Completada)")
            ),
            archivoListar = "listarActividad.php",
            archivoCrear = "crearActividad.php",
            archivoModificar = "modificarActividad.php",
            archivoEliminar = "eliminarActividad.php",
            tituloItem = { it.texto("titulo") },
            detalleItem = {
                "ID ${it.texto("id_actividad")} · Estado: ${it.texto("estado")}\n" +
                        "Proyecto: ${it.texto("proyecto")} (ID ${it.texto("id_proyecto")})\n" +
                        "Inicio: ${it.texto("fecha_inicio")} · Fin: ${it.texto("fecha_fin")}"
            },
            valoresParaEditar = {
                mapOf(
                    "id_proyecto" to it.texto("id_proyecto"),
                    "titulo" to it.texto("titulo"),
                    "descripcion" to it.texto("descripcion"),
                    "fecha_inicio" to it.texto("fecha_inicio"),
                    "fecha_fin" to it.texto("fecha_fin"),
                    "estado" to it.texto("estado")
                )
            },
            identificador = { mapOf("id_actividad" to it.texto("id_actividad")) },
            validar = { v, _ ->
                val proyecto = v["id_proyecto"].orEmpty()
                val titulo = v["titulo"].orEmpty()
                when {
                    !isValidActividad(titulo, proyecto) -> "El ID del proyecto y el título son obligatorios."
                    proyecto.toIntOrNull() == null -> "El ID del proyecto debe ser un número entero."
                    else -> null
                }
            },
            textoGuardar = "Guardar actividad"
        )
    }
}
