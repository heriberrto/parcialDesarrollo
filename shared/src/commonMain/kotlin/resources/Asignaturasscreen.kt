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
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var idPrograma by remember { mutableStateOf("") }
        var nombreAsignatura by remember { mutableStateOf("") }
        var codigo by remember { mutableStateOf("") }
        var programa by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }

        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Asignaturas") },
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
                    "Registro y actualización de asignaturas, y consulta de las activas.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = idPrograma,
                    onValueChange = { idPrograma = it },
                    label = { Text("ID del programa académico (id_programa)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nombreAsignatura,
                    onValueChange = { nombreAsignatura = it },
                    label = { Text("Nombre de la asignatura") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código de la asignatura") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = programa,
                    onValueChange = { programa = it },
                    label = { Text("Programa académico asociado") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (!isValidAsignatura(idPrograma, nombreAsignatura, codigo, programa)) {
                            mensaje = "Por favor complete el ID del programa, nombre, código y programa académico."
                            showDialog = true
                            return@Button
                        }

                        scope.launch {
                            try {
                                val client = HttpClient()
                                val responseText: String = client.post("http://192.168.2.13/API/crearAsignatura.php") {
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        Parameters.build {
                                            append("id_programa", idPrograma)
                                            append("idPrograma", idPrograma)
                                            append("id", idPrograma)
                                            append("nombre", nombreAsignatura)
                                            append("nombreAsignatura", nombreAsignatura)
                                            append("codigo", codigo)
                                            append("programa", programa)
                                            append("descripcion", descripcion)
                                        }.formUrlEncode()
                                    )
                                }.body()
                                client.close()

                                val trimmed = responseText.trim()
                                if (trimmed.startsWith("<")) {
                                    mensaje = "El servidor devolvió una página HTML en lugar de JSON. Verifica 'crearAsignatura.php' en tu servidor Apache."
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
                                mensaje = "Asignatura guardada localmente o error de red: ${e.message}"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar asignatura")
                }

                if (showDialog && mensaje.isNotEmpty()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Asignaturas") },
                        text = { Text(mensaje) }
                    )
                }
            }
        }
    }
}
