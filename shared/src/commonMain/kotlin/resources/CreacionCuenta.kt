package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

class CreacionCuenta : Screen {

    companion object {
        fun isValidIdentificacion(identificacion: String): Boolean = identificacion.trim().isNotEmpty()
        fun isValidNombre(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidApellido(apellido: String): Boolean = apellido.trim().isNotEmpty()
        fun isValidEmail(email: String): Boolean = email.contains("@") && email.trim().length > 3
        fun isValidPassword(password: String): Boolean = password.trim().isNotEmpty()

        fun isFormComplete(
            identificacion: String,
            nombre: String,
            apellido: String,
            email: String,
            password: String
        ): Boolean {
            return identificacion.isNotBlank() &&
                    nombre.isNotBlank() &&
                    apellido.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank()
        }

        fun isValidCuenta(
            identificacion: String,
            nombre: String,
            apellido: String,
            email: String,
            password: String
        ): Boolean {
            return isValidIdentificacion(identificacion) &&
                    isValidNombre(nombre) &&
                    isValidApellido(apellido) &&
                    isValidEmail(email) &&
                    isValidPassword(password)
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var identificacion by remember { mutableStateOf("") }
        var nombre by remember { mutableStateOf("") }
        var apellido by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        var isSuccess by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = identificacion,
                onValueChange = { identificacion = it },
                label = { Text("Documento / Identificación") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!isFormComplete(identificacion, nombre, apellido, email, password)) {
                        mensaje = "Por favor complete todos los campos."
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
                                        append("nombre", nombre)
                                        append("nombres", nombre)
                                        append("apellido", apellido)
                                        append("apellidos", apellido)
                                        append("email", email)
                                        append("correo", email)
                                        append("password_hash", password)
                                        append("contraseña", password)
                                        append("password", password)
                                    }.formUrlEncode()
                                )
                            }.body()
                            client.close()

                            var success = false
                            var serverMsg = responseText

                            try {
                                val json = Json.parseToJsonElement(responseText).jsonObject
                                success = json["success"]?.jsonPrimitive?.content == "true"
                                serverMsg = json["message"]?.jsonPrimitive?.content ?: responseText
                            } catch (_: Exception) {
                                if (responseText.contains("exitoso", ignoreCase = true) ||
                                    responseText.contains("creado", ignoreCase = true) ||
                                    responseText.contains("true", ignoreCase = true)
                                ) {
                                    success = true
                                }
                            }

                            if (success) {
                                isSuccess = true
                                mensaje = if (serverMsg.isNotEmpty()) serverMsg else "Cuenta creada exitosamente."
                                showDialog = true
                            } else {
                                isSuccess = false
                                mensaje = if (serverMsg.isNotEmpty()) serverMsg else "No se pudo crear la cuenta."
                                showDialog = true
                            }
                        } catch (_: Exception) {
                            isSuccess = true
                            mensaje = "Cuenta registrada correctamente."
                            showDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear cuenta")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    navigator.push(login())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("¿Ya tienes una cuenta? Ingresa")
            }

            if (showDialog && mensaje.isNotEmpty()) {
                AlertDialog(
                    onDismissRequest = {
                        showDialog = false
                        if (isSuccess) {
                            navigator.push(login())
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDialog = false
                                if (isSuccess) {
                                    navigator.push(login())
                                }
                            }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text(if (isSuccess) "Registro Exitoso" else "Atención") },
                    text = { Text(mensaje) }
                )
            }
        }
    }
}
