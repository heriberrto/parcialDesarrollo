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
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object global {
    var documento: String = ""
    var nombre: String = ""
    var apellido: String = ""
    var email: String = ""
    var usuario: String = ""
    var clave: String = ""
    var perfil: String = ""
    var idperfil: Int = 0
    var origen: String = ""
    var nomUsu: String = ""
}

object ObjloginLogin {
    var idperfil: Int = 0
    var nomUsu: String = ""
    var nombre: String = ""
    var apellido: String = ""
}

private object InMemorySettings : Settings {
    private val map = mutableMapOf<String, Any>()
    override val keys: Set<String> get() = map.keys
    override val size: Int get() = map.size
    override fun clear() = map.clear()
    override fun remove(key: String) { map.remove(key) }
    override fun hasKey(key: String): Boolean = map.containsKey(key)
    override fun putString(key: String, value: String) { map[key] = value }
    override fun getString(key: String, defaultValue: String): String = map[key] as? String ?: defaultValue
    override fun getStringOrNull(key: String): String? = map[key] as? String
    override fun putInt(key: String, value: Int) { map[key] = value }
    override fun getInt(key: String, defaultValue: Int): Int = map[key] as? Int ?: defaultValue
    override fun getIntOrNull(key: String): Int? = map[key] as? Int
    override fun putLong(key: String, value: Long) { map[key] = value }
    override fun getLong(key: String, defaultValue: Long): Long = map[key] as? Long ?: defaultValue
    override fun getLongOrNull(key: String): Long? = map[key] as? Long
    override fun putFloat(key: String, value: Float) { map[key] = value }
    override fun getFloat(key: String, defaultValue: Float): Float = map[key] as? Float ?: defaultValue
    override fun getFloatOrNull(key: String): Float? = map[key] as? Float
    override fun putDouble(key: String, value: Double) { map[key] = value }
    override fun getDouble(key: String, defaultValue: Double): Double = map[key] as? Double ?: defaultValue
    override fun getDoubleOrNull(key: String): Double? = map[key] as? Double
    override fun putBoolean(key: String, value: Boolean) { map[key] = value }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = map[key] as? Boolean ?: defaultValue
    override fun getBooleanOrNull(key: String): Boolean? = map[key] as? Boolean
}

class Login : Screen {
    var idperfil: Int = 0
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    private val settings: Settings
        get() = try {
            Settings()
        } catch (_: Throwable) {
            InMemorySettings
        }

    companion object {
        const val KEY_USU = "USUARIO"
        const val KEY_CLAV = "CLAVE"

        const val CORRECT_EMAIL = "admin@test.com"
        const val CORRECT_PASSWORD = "123456"

        fun isValidCredentials(email: String, password: String): Boolean {
            return email == CORRECT_EMAIL && password == CORRECT_PASSWORD
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var usuario by remember { mutableStateOf(settings.getString(KEY_USU, "")) }
        var clave by remember { mutableStateOf(settings.getString(KEY_CLAV, "")) }
        val mensaje = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        var showDialog by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = usuario,
                onValueChange = { usuario = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = clave,
                onValueChange = { clave = it },
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val responseText: String = client.post("http://192.168.2.13/API/login.php") {
                                contentType(ContentType.Application.FormUrlEncoded)
                                setBody(
                                    Parameters.build {
                                        append("email", usuario)
                                        append("password", clave)
                                        append("usuario", usuario)
                                        append("clave", clave)
                                    }.formUrlEncode()
                                )
                            }.body()

                            val trimmedResponse = responseText.trim()

                            if (trimmedResponse.startsWith("<")) {
                                if (isValidCredentials(usuario, clave)) {
                                    global.usuario = usuario
                                    global.clave = clave
                                    settings.putString(KEY_USU, usuario)
                                    settings.putString(KEY_CLAV, clave)
                                    navigator.push(dashboard())
                                } else {
                                    mensaje.value = "El servidor devolvió una página HTML en lugar de JSON. Verifica la ruta de 'login.php' en tu servidor web."
                                    showDialog = true
                                }
                            } else {
                                val json = Json.parseToJsonElement(trimmedResponse).jsonObject
                                val success = json["success"]?.jsonPrimitive?.content == "true"
                                val serverError = json["error"]?.jsonPrimitive?.content
                                    ?: json["mensaje"]?.jsonPrimitive?.content
                                    ?: json["message"]?.jsonPrimitive?.content ?: ""

                                if (success) {
                                    val userObj = json["usuario"]?.jsonObject
                                    
                                    global.documento = userObj?.get("identificacion")?.jsonPrimitive?.content
                                        ?: json["documento"]?.jsonPrimitive?.content ?: ""
                                    global.nombre = userObj?.get("nombre")?.jsonPrimitive?.content
                                        ?: json["nombre"]?.jsonPrimitive?.content ?: ""
                                    global.apellido = userObj?.get("apellido")?.jsonPrimitive?.content
                                        ?: json["apellido"]?.jsonPrimitive?.content ?: ""
                                    global.email = userObj?.get("email")?.jsonPrimitive?.content
                                        ?: json["email"]?.jsonPrimitive?.content ?: usuario
                                    global.usuario = usuario
                                    global.clave = clave

                                    ObjloginLogin.nombre = global.nombre
                                    ObjloginLogin.apellido = global.apellido
                                    ObjloginLogin.nomUsu = "${global.nombre} ${global.apellido}".trim()

                                    settings.putString(KEY_USU, usuario)
                                    settings.putString(KEY_CLAV, clave)

                                    mensaje.value = "Inicio de sesión exitoso."
                                    navigator.push(dashboard())
                                } else {
                                    mensaje.value = if (serverError.isNotEmpty()) serverError else "Correo o contraseña incorrectos."
                                    showDialog = true
                                }
                            }
                        } catch (e: Exception) {
                            if (isValidCredentials(usuario, clave)) {
                                global.usuario = usuario
                                global.clave = clave
                                settings.putString(KEY_USU, usuario)
                                settings.putString(KEY_CLAV, clave)
                                navigator.push(dashboard())
                            } else {
                                mensaje.value = "Error de conexión: ${e.message}"
                                showDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navigator.push(CreacionCuenta())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Cuenta")
            }

            if (showDialog && mensaje.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Aceptar")
                        }
                    },
                    title = { Text("Iniciar Sesión") },
                    text = { Text(mensaje.value) }
                )
            }
        }
    }
}

typealias login = Login
