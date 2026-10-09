package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

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
        val scope = rememberCoroutineScope()

        var nombres by remember { mutableStateOf("") }
        var apellidos by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var identificacion by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rol by remember { mutableStateOf("Estudiante") }
        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

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
                        if (!isValidUsuario(nombres, apellidos, email, identificacion, password, rol)) {
                            mensaje = "Por favor complete todos los campos obligatorios correctamente."
                            showDialog = true
                            return@Button
                        }

                        scope.launch {
                            try {
                                val client = HttpClient()
                                val responseText: String = client.post("http://192.168.2.13/API/crearUsuario.php") {
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        Parameters.build {
                                            append("identificacion", identificacion)
                                            append("documento", identificacion)
                                            append("nombre", nombres)
                                            append("nombres", nombres)
                                            append("apellido", apellidos)
                                            append("apellidos", apellidos)
                                            append("email", email)
                                            append("correo", email)
                                            append("password_hash", password)
                                            append("password", password)
                                            append("rol", rol)
                                            append("id_rol", when(rol.trim().lowercase()) {
                                                "administrador", "admin" -> "1"
                                                "estudiante" -> "2"
                                                "docente" -> "3"
                                                else -> "2"
                                            })
                                        }.formUrlEncode()
                                    )
                                }.body()
                                client.close()

                                val trimmed = responseText.trim()
                                if (trimmed.startsWith("<")) {
                                    mensaje = "El servidor devolvió una página HTML en lugar de JSON. Verifica 'crearUsuario.php' en tu servidor Apache."
                                } else {
                                    try {
                                        val json = Json.parseToJsonElement(trimmed).jsonObject
                                        val msg = json["message"]?.jsonPrimitive?.content
                                            ?: json["mensaje"]?.jsonPrimitive?.content
                                            ?: json["error"]?.jsonPrimitive?.content ?: trimmed
                                        mensaje = msg
                                    } catch (_: Exception) {
                                        mensaje = trimmed
                                    }
                                }
                            } catch (e: Exception) {
                                mensaje = "Usuario guardado localmente o error de red: ${e.message}"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar usuario")
                }

                if (showDialog && mensaje.isNotEmpty()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Usuarios y roles") },
                        text = { Text(mensaje) }
                    )
                }
            }
        }
    }
}
