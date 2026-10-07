package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

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
        val navigator = LocalNavigator.currentOrThrow

        var nombres by remember { mutableStateOf("") }
        var apellidos by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var identificacion by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rol by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Usuarios y roles") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Registro, consulta y edición de usuarios. Autenticación por " +
                            "usuario y contraseña, y control de acceso según el rol.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = identificacion,
                    onValueChange = { identificacion = it },
                    label = { Text("Identificación / Cédula") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña (mínimo 6 caracteres)") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text("Rol (Administrador, Estudiante, Docente)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        errorMessage = if (isValidUsuario(nombres, apellidos, email, identificacion, password, rol)) {
                            /* TODO: conectar con repositorio/base de datos */
                            ""
                        } else {
                            "Por favor complete todos los campos obligatorios correctamente."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar usuario")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
