package resources

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen

class RolesScreen : Screen {

    companion object {
        fun isValidIdRol(idRol: String): Boolean = idRol.trim().isNotEmpty()
        fun isValidNombre(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidDescripcion(descripcion: String): Boolean = descripcion.trim().isNotEmpty()
    }

    @Composable
    override fun Content() {
        val rolActual = dashboard.obtenerRolActual()
        if (rolActual != UserRole.ADMINISTRADOR) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Acceso restringido. Solo los usuarios con rol de Administrador pueden acceder a la gestión de roles.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
            return
        }

        CrudPantalla(
            titulo = "Roles",
            descripcion = "Registro, actualización, consulta y eliminación de roles.",
            campos = listOf(
                CampoFormulario("nombre", "Nombre del rol"),
                CampoFormulario("descripcion", "Descripción")
            ),
            archivoListar = "listarRol.php",
            archivoCrear = "crearRol.php",
            archivoModificar = "modificarRol.php",
            archivoEliminar = "eliminarRol.php",
            tituloItem = { "${it.texto("codigo")}-${it.texto("nombre")}" },
            detalleItem = {
                "ID ${it.texto("id_rol")} · Descripción: ${it.texto("descripcion")}"
            },
            valoresParaEditar = {
                mapOf(
                    "nombre" to it.texto("nombre"),
                    "descripcion" to it.texto("descripcion"),
                    "estado" to it.texto("estado"),
                    "id_rol" to it.texto("id_rol")
                )
            },
            identificador = { mapOf("id_rol" to it.texto("id_rol")) },
            validar = { v, editando ->
                when {
                    editando && !isValidIdRol(v["id_rol"].orEmpty()) -> "Por favor complete el ID del rol."
                    !isValidNombre(v["nombre"].orEmpty()) -> "Por favor complete el nombre del rol."
                    !isValidDescripcion(v["descripcion"].orEmpty()) -> "Por favor complete la descripción del rol."
                    else -> null
                }
            },
            textoGuardar = "Guardar rol"
        )
    }
}
