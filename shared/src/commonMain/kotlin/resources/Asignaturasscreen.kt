package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Administración de asignaturas.
 * Mapea con la tabla `asignatura` (id_programa, nombre, codigo, descripcion, estado).
 */
class AsignaturasScreen : Screen {

    companion object {
        fun isValidIdPrograma(idPrograma: String): Boolean = idPrograma.trim().isNotEmpty()
        fun isValidNombre(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidCodigo(codigo: String): Boolean = codigo.trim().isNotEmpty()
        fun isValidPrograma(programa: String): Boolean = programa.trim().isNotEmpty()

        fun isValidAsignatura(idPrograma: String, nombre: String, codigo: String, programa: String): Boolean {
            return isValidIdPrograma(idPrograma) && isValidNombre(nombre) && isValidCodigo(codigo) && isValidPrograma(programa)
        }

        fun isValidAsignatura(nombre: String, codigo: String, programa: String): Boolean {
            return isValidNombre(nombre) && isValidCodigo(codigo) && isValidPrograma(programa)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Asignaturas",
            descripcion = "Registro, actualización, consulta y eliminación de asignaturas. " +
                    "Use el ID del programa que aparece en la lista de Programas académicos.",
            campos = listOf(
                CampoFormulario("nombre", "Nombre de la asignatura"),
                CampoFormulario("codigo", "Código de la asignatura"),
                CampoFormulario("id_programa", "ID del programa académico", numerico = true),
                CampoFormulario("descripcion", "Descripción")
            ),
            archivoListar = "listarAsignatura.php",
            archivoCrear = "crearAsignatura.php",
            archivoModificar = "modificarAsignatura.php",
            archivoEliminar = "eliminarAsignatura.php",
            tituloItem = { "${it.texto("codigo")} - ${it.texto("nombre")}" },
            detalleItem = {
                "ID ${it.texto("id_asignatura")} · Programa: ${it.texto("programa")} (ID ${it.texto("id_programa")})"
            },
            valoresParaEditar = {
                mapOf(
                    "nombre" to it.texto("nombre"),
                    "codigo" to it.texto("codigo"),
                    "id_programa" to it.texto("id_programa"),
                    "descripcion" to it.texto("descripcion")
                )
            },
            identificador = { mapOf("id_asignatura" to it.texto("id_asignatura")) },
            validar = { v, _ ->
                val programa = v["id_programa"].orEmpty()
                when {
                    !isValidAsignatura(v["nombre"].orEmpty(), v["codigo"].orEmpty(), programa) ->
                        "Por favor complete el nombre, código y programa académico."
                    programa.toIntOrNull() == null -> "El programa académico debe ser un ID numérico."
                    else -> null
                }
            },
            textoGuardar = "Guardar asignatura"
        )
    }
}
