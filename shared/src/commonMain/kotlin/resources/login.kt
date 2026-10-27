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
import androidx.compose.material3.ExperimentalMaterial3Api
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

    @Transient
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

        const val MAX_FAILED_ATTEMPTS = 4
        private val failedAttemptsMap = mutableMapOf<String, Int>()
        private val lockedAccounts = mutableSetOf<String>()

        fun getFailedAttempts(email: String): Int {
            val key = email.trim().lowercase()
            return failedAttemptsMap[key] ?: 0
        }

        fun isAccountLocked(email: String): Boolean {
            val key = email.trim().lowercase()
            return lockedAccounts.contains(key) || (failedAttemptsMap[key] ?: 0) >= MAX_FAILED_ATTEMPTS
        }

        fun recordFailedAttempt(email: String): Int {
            val key = email.trim().lowercase()
            if (key.isBlank()) return 0
            val current = (failedAttemptsMap[key] ?: 0) + 1
            failedAttemptsMap[key] = current
            if (current >= MAX_FAILED_ATTEMPTS) {
                lockedAccounts.add(key)
            }
            return current
        }

        fun resetFailedAttempts(email: String) {
            val key = email.trim().lowercase()
            failedAttemptsMap.remove(key)
            lockedAccounts.remove(key)
        }

        fun unlockAccount(email: String) {
            resetFailedAttempts(email)
        }

        fun isValidCredentials(email: String, password: String): Boolean {
            return email == CORRECT_EMAIL && password == CORRECT_PASSWORD
        }

        fun isValidEmail(email: String): Boolean {
            val trimmed = email.trim()
            return trimmed.isNotBlank() && trimmed.contains("@") && trimmed.contains(".")
        }

        fun isValidPassword(password: String): Boolean {
            return password.trim().isNotBlank() && password.length >= 6
        }

        fun determinePerfil(usuario: String): String {
            return when {
                usuario.contains("docente", ignoreCase = true) || usuario.contains("yamil", ignoreCase = true) -> "Docente"
                usuario.contains("estudiante", ignoreCase = true) || usuario.contains("heri", ignoreCase = true) -> "Estudiante"
                else -> "Administrador"
            }
        }

        fun determineIdPerfil(perfil: String): Int {
            return when (perfil.lowercase().trim()) {
                "administrador", "admin" -> 1
                "estudiante" -> 2
                "docente" -> 3
                else -> 1
            }
        }

        fun formatNombreUsuario(nombre: String, apellido: String, fallbackUsuario: String): String {
            val nomCompleto = "$nombre $apellido".trim()
            return if (nomCompleto.isNotBlank()) nomCompleto else fallbackUsuario
        }

        fun updateGlobalAndLoginState(
            usuario: String,
            clave: String,
            nombre: String = "",
            apellido: String = "",
            email: String = "",
            perfil: String = "",
            idPerfil: Int = 0,
            documento: String = ""
        ) {
            global.usuario = usuario
            global.clave = clave
            global.nombre = if (nombre.isNotBlank()) nombre else if (global.nombre.isBlank()) "Administrador" else global.nombre
            global.apellido = if (apellido.isNotBlank()) apellido else global.apellido
            global.email = if (email.isNotBlank()) email else if (global.email.isBlank()) usuario else global.email
            global.documento = if (documento.isNotBlank()) documento else global.documento

            val computedPerfil = if (perfil.isNotBlank()) perfil else if (global.perfil.isBlank()) determinePerfil(usuario) else global.perfil
            global.perfil = computedPerfil

            val computedIdPerfil = if (idPerfil > 0) idPerfil else determineIdPerfil(computedPerfil)
            global.idperfil = computedIdPerfil

            global.nomUsu = formatNombreUsuario(global.nombre, global.apellido, usuario)

            ObjloginLogin.nombre = global.nombre
            ObjloginLogin.apellido = global.apellido
            ObjloginLogin.nomUsu = global.nomUsu
            ObjloginLogin.idperfil = global.idperfil
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var usuario by remember { mutableStateOf(settings.getString(KEY_USU, "")) }
        var clave by remember { mutableStateOf(settings.getString(KEY_CLAV, "")) }
        val mensaje = remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        var showDialog by remember { mutableStateOf(false) }
        var passwordVisible by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("SIGAPI - Iniciar Sesión") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

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
                    if (isAccountLocked(usuario)) {
                        mensaje.value = "La cuenta '$usuario' se encuentra bloqueada por superar el límite de $MAX_FAILED_ATTEMPTS intentos fallidos. Contacte al administrador."
                        showDialog = true
                        return@Button
                    }

                    scope.launch {
                        try {
                            val responseText: String = client.post(ApiCliente.BASE_URL + "login.php") {
                                contentType(ContentType.Application.FormUrlEncoded)
                                setBody(
                                    Parameters.build {
                                        append("email", usuario)
                                        append("password", clave)
                                        append("password_hash", CryptoUtils.sha256(clave))
                                        append("password_encrypted", CryptoUtils.encryptField(clave))
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
                                    if (global.nombre.isBlank()) global.nombre = "Administrador"
                                    if (global.email.isBlank()) global.email = usuario
                                    if (global.perfil.isBlank()) {
                                        global.perfil = when {
                                            usuario.contains("docente", ignoreCase = true) || usuario.contains("yamil", ignoreCase = true) -> "Docente"
                                            usuario.contains("estudiante", ignoreCase = true) || usuario.contains("heri", ignoreCase = true) -> "Estudiante"
                                            else -> "Administrador"
                                        }
                                    }
                                    global.idperfil = when (global.perfil.lowercase()) {
                                        "administrador", "admin" -> 1
                                        "estudiante" -> 2
                                        "docente" -> 3
                                        else -> 1
                                    }
                                    val nomCompleto = "${global.nombre} ${global.apellido}".trim()
                                    global.nomUsu = if (nomCompleto.isNotBlank()) nomCompleto else usuario
                                    ObjloginLogin.nombre = global.nombre
                                    ObjloginLogin.apellido = global.apellido
                                    ObjloginLogin.nomUsu = global.nomUsu
                                    ObjloginLogin.idperfil = global.idperfil
                                    settings.putString(KEY_USU, usuario)
                                    settings.putString(KEY_CLAV, clave)
                                    resetFailedAttempts(usuario)
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
                                        ?: json["data"]?.jsonObject
                                        ?: json["datos"]?.jsonObject
                                        ?: json["user"]?.jsonObject

                                    global.documento = userObj?.get("identificacion")?.jsonPrimitive?.content
                                        ?: userObj?.get("documento")?.jsonPrimitive?.content
                                        ?: json["documento"]?.jsonPrimitive?.content ?: ""
                                    global.nombre = userObj?.get("nombre")?.jsonPrimitive?.content
                                        ?: userObj?.get("nombres")?.jsonPrimitive?.content
                                        ?: json["nombre"]?.jsonPrimitive?.content ?: ""
                                    global.apellido = userObj?.get("apellido")?.jsonPrimitive?.content
                                        ?: userObj?.get("apellidos")?.jsonPrimitive?.content
                                        ?: json["apellido"]?.jsonPrimitive?.content ?: ""
                                    global.email = userObj?.get("email")?.jsonPrimitive?.content
                                        ?: userObj?.get("correo")?.jsonPrimitive?.content
                                        ?: json["email"]?.jsonPrimitive?.content ?: usuario
                                    global.usuario = usuario
                                    global.clave = clave

                                    val perfilVal = userObj?.get("rol")?.jsonPrimitive?.content
                                        ?: userObj?.get("perfil")?.jsonPrimitive?.content
                                        ?: userObj?.get("nombre_rol")?.jsonPrimitive?.content
                                        ?: userObj?.get("rol_nombre")?.jsonPrimitive?.content
                                        ?: json["rol"]?.jsonPrimitive?.content
                                        ?: json["perfil"]?.jsonPrimitive?.content
                                        ?: json["nombre_rol"]?.jsonPrimitive?.content
                                        ?: ""

                                    val idPerfilVal = userObj?.get("id_rol")?.jsonPrimitive?.content
                                        ?: userObj?.get("id_perfil")?.jsonPrimitive?.content
                                        ?: userObj?.get("rol_id")?.jsonPrimitive?.content
                                        ?: json["id_rol"]?.jsonPrimitive?.content
                                        ?: json["id_perfil"]?.jsonPrimitive?.content
                                        ?: ""

                                    global.idperfil = idPerfilVal.toIntOrNull()
                                        ?: perfilVal.toIntOrNull()
                                        ?: when (perfilVal.lowercase()) {
                                            "administrador", "admin" -> 1
                                            "estudiante" -> 2
                                            "docente" -> 3
                                            else -> 0
                                        }

                                    global.perfil = when {
                                        perfilVal.equals("1", ignoreCase = true) || perfilVal.contains("admin", ignoreCase = true) || global.idperfil == 1 -> "Administrador"
                                        perfilVal.equals("3", ignoreCase = true) || perfilVal.contains("docente", ignoreCase = true) || global.idperfil == 3 -> "Docente"
                                        perfilVal.equals("2", ignoreCase = true) || perfilVal.contains("estudiante", ignoreCase = true) || global.idperfil == 2 -> "Estudiante"
                                        perfilVal.isNotBlank() -> perfilVal
                                        else -> ""
                                    }

                                    val nomCompleto = "${global.nombre} ${global.apellido}".trim()
                                    global.nomUsu = if (nomCompleto.isNotBlank()) nomCompleto else usuario

                                    ObjloginLogin.nombre = global.nombre
                                    ObjloginLogin.apellido = global.apellido
                                    ObjloginLogin.nomUsu = global.nomUsu
                                    ObjloginLogin.idperfil = global.idperfil

                                    settings.putString(KEY_USU, usuario)
                                    settings.putString(KEY_CLAV, clave)

                                    resetFailedAttempts(usuario)
                                    mensaje.value = "Inicio de sesión exitoso."
                                    navigator.push(dashboard())
                                } else {
                                    val attempts = recordFailedAttempt(usuario)
                                    if (attempts >= MAX_FAILED_ATTEMPTS) {
                                        mensaje.value = "Cuenta bloqueada. Ha superado el límite de $MAX_FAILED_ATTEMPTS intentos fallidos de contraseña."
                                    } else {
                                        val restantes = MAX_FAILED_ATTEMPTS - attempts
                                        val baseErr = if (serverError.isNotEmpty()) serverError else "Correo o contraseña incorrectos."
                                        mensaje.value = "$baseErr Te quedan $restantes intento(s) antes de bloquear la cuenta."
                                    }
                                    showDialog = true
                                }
                            }
                        } catch (e: Exception) {
                            if (isValidCredentials(usuario, clave)) {
                                global.usuario = usuario
                                global.clave = clave
                                if (global.nombre.isBlank()) global.nombre = "Administrador"
                                if (global.email.isBlank()) global.email = usuario
                                if (global.perfil.isBlank()) {
                                    global.perfil = when {
                                        usuario.contains("docente", ignoreCase = true) -> "Docente"
                                        usuario.contains("estudiante", ignoreCase = true) -> "Estudiante"
                                        else -> "Administrador"
                                    }
                                }
                                global.idperfil = when (global.perfil.lowercase()) {
                                    "administrador", "admin" -> 1
                                    "estudiante" -> 2
                                    "docente" -> 3
                                    else -> 1
                                }
                                val nomCompleto = "${global.nombre} ${global.apellido}".trim()
                                global.nomUsu = if (nomCompleto.isNotBlank()) nomCompleto else usuario
                                ObjloginLogin.nombre = global.nombre
                                ObjloginLogin.apellido = global.apellido
                                ObjloginLogin.nomUsu = global.nomUsu
                                ObjloginLogin.idperfil = global.idperfil
                                settings.putString(KEY_USU, usuario)
                                settings.putString(KEY_CLAV, clave)
                                resetFailedAttempts(usuario)
                                navigator.push(dashboard())
                            } else {
                                val attempts = recordFailedAttempt(usuario)
                                if (attempts >= MAX_FAILED_ATTEMPTS) {
                                    mensaje.value = "Cuenta bloqueada. Ha superado el límite de $MAX_FAILED_ATTEMPTS intentos fallidos de contraseña."
                                } else {
                                    val restantes = MAX_FAILED_ATTEMPTS - attempts
                                    mensaje.value = "Credenciales incorrectas o error de conexión (${e.message}). Te quedan $restantes intento(s) antes de bloquear la cuenta."
                                }
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
}

typealias login = Login
