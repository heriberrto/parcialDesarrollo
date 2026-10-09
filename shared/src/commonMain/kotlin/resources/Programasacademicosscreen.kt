package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Gestión de programas académicos.
 * Mapea directamente con la tabla `programa_academico` (nombre, descripcion, estado).
 */
class ProgramasAcademicosScreen : Screen {

    companion object {
        fun isValidNombrePrograma(nombre: String): Boolean {
            return nombre.trim().isNotEmpty()
        }

        fun isValidPrograma(nombre: String): Boolean {
            return isValidNombrePrograma(nombre)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Programas académicos",
            descripcion = "Registro, consulta, edición y eliminación de programas académicos.",
            campos = listOf(
                CampoFormulario("nombre", "Nombre del programa"),
                CampoFormulario("descripcion", "Descripción")
            ),
            archivoListar = "listarPrograma.php",
            archivoCrear = "crearPrograma.php",
            archivoModificar = "modificarPrograma.php",
            archivoEliminar = "eliminarPrograma.php",
            tituloItem = { it.texto("nombre") },
            detalleItem = {
                val descripcion = it.texto("descripcion")
                "ID ${it.texto("id_programa")}" + if (descripcion.isNotEmpty()) " · $descripcion" else ""
            },
            valoresParaEditar = {
                mapOf(
                    "nombre" to it.texto("nombre"),
                    "descripcion" to it.texto("descripcion")
                )
            },
            identificador = { mapOf("id_programa" to it.texto("id_programa")) },
            validar = { v, _ ->
                if (!isValidPrograma(v["nombre"].orEmpty())) "El nombre del programa académico es obligatorio." else null
            },
            textoGuardar = "Guardar programa"
        )
    }
}
