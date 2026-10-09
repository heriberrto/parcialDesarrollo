package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

/**
 * Módulo: Administración de usuarios y roles.
 * Mapea con las tablas `usuario`, `rol` y `usuario_rol`
 * (nombre, apellido, email, identificacion, password_hash, estado).
 */
class UsuariosScreen : Screen {

    companion object {
        fun isValidNombre(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidApellido(apellido: String): Boolean = apellido.trim().isNotEmpty()
        fun isValidEmail(email: String): Boolean = email.contains("@") && email.trim().length > 3
        fun isValidIdentificacion(identificacion: String): Boolean = identificacion.trim().isNotEmpty()
        fun isValidPassword(password: String): Boolean = password.length >= 6
        fun isValidRol(rol: String): Boolean = rol.trim().isNotEmpty()

        fun isValidUsuario(
            nombre: String,
            apellido: String,
            email: String,
            identificacion: String,
            password: String,
            rol: String
        ): Boolean {
            return isValidNombre(nombre) &&
                    isValidApellido(apellido) &&
                    isValidEmail(email) &&
                    isValidIdentificacion(identificacion) &&
                    isValidPassword(password) &&
                    isValidRol(rol)
        }
    }

    @Composable
    override fun Content() {
        CrudPantalla(
            titulo = "Usuarios y roles",
            descripcion = "Registro, consulta, edición y eliminación de usuarios. " +
                    "El ID de cada usuario se usa para armar equipos y asignar docentes.",
            campos = listOf(
                CampoFormulario("nombre", "Nombres"),
                CampoFormulario("apellido", "Apellidos"),
                CampoFormulario("email", "Correo electrónico"),
                CampoFormulario("identificacion", "Identificación / Cédula"),
                CampoFormulario("password", "Contraseña (mínimo 6 caracteres)", esPassword = true),
                CampoFormulario("rol", "Rol (Administrador, Estudiante, Docente)")
            ),
            archivoListar = "listarUsuario.php",
            archivoCrear = "crearUsuario.php",
            archivoModificar = "modificarUsuario.php",
            archivoEliminar = "eliminarUsuario.php",
            tituloItem = { "${it.texto("nombre")} ${it.texto("apellido")}" },
            detalleItem = {
                "ID ${it.texto("id_usuario")} · CC ${it.texto("identificacion")}\n" +
                        "${it.texto("email")} · ${it.texto("rol").ifEmpty { "Sin rol" }}"
            },
            valoresParaEditar = {
                mapOf(
                    "nombre" to it.texto("nombre"),
                    "apellido" to it.texto("apellido"),
                    "email" to it.texto("email"),
                    "identificacion" to it.texto("identificacion"),
                    "password" to "",
                    "rol" to it.texto("rol").substringBefore(",").trim()
                )
            },
            identificador = { mapOf("id_usuario" to it.texto("id_usuario")) },
            validar = { v, editando ->
                val password = v["password"].orEmpty()
                when {
                    !isValidNombre(v["nombre"].orEmpty()) -> "El nombre es obligatorio."
                    !isValidApellido(v["apellido"].orEmpty()) -> "El apellido es obligatorio."
                    !isValidEmail(v["email"].orEmpty()) -> "Ingrese un correo electrónico válido."
                    !isValidIdentificacion(v["identificacion"].orEmpty()) -> "La identificación es obligatoria."
                    !editando && !isValidPassword(password) -> "La contraseña debe tener mínimo 6 caracteres."
                    editando && password.isNotEmpty() && !isValidPassword(password) ->
                        "La nueva contraseña debe tener mínimo 6 caracteres."
                    !isValidRol(v["rol"].orEmpty()) -> "El rol es obligatorio."
                    else -> null
                }
            },
            textoGuardar = "Guardar usuario"
        )
    }
}
