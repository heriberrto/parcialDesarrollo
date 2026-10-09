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
 * Módulo: Gestión de equipos de estudiantes.
 * Mapea con las tablas `equipo` (id_proyecto, nombre, estado) y `equipo_estudiante`.
 */
class EquiposScreen : Screen {

    companion object {
        fun isValidProyecto(proyecto: String): Boolean = proyecto.trim().isNotEmpty()
        fun isValidNombreEquipo(nombre: String): Boolean = nombre.trim().isNotEmpty()
        fun isValidIntegrantes(integrantes: String): Boolean = integrantes.trim().isNotEmpty()

        fun isValidEquipo(proyecto: String, nombreEquipo: String, integrantes: String): Boolean {
            return isValidProyecto(proyecto) && isValidNombreEquipo(nombreEquipo) && isValidIntegrantes(integrantes)
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var proyecto by remember { mutableStateOf("") }
        var nombreEquipo by remember { mutableStateOf("") }
        var integrantes by remember { mutableStateOf("") }

        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Equipos de estudiantes") },
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
                    "Creación de equipos de trabajo y asignación de integrantes por proyecto.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = proyecto,
                    onValueChange = { proyecto = it },
                    label = { Text("Proyecto ID o asociado (id_proyecto)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nombreEquipo,
                    onValueChange = { nombreEquipo = it },
                    label = { Text("Nombre del equipo") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = integrantes,
                    onValueChange = { integrantes = it },
                    label = { Text("Estudiantes / Integrantes (IDs separados por coma)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (!isValidEquipo(proyecto, nombreEquipo, integrantes)) {
                            mensaje = "Por favor complete el proyecto, nombre del equipo e integrantes."
                            showDialog = true
                            return@Button
                        }

                        scope.launch {
                            try {
                                val client = HttpClient()
                                val responseText: String = client.post("http://192.168.2.13/API/crearEquipo.php") {
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        Parameters.build {
                                            append("id_proyecto", proyecto)
                                            append("proyecto", proyecto)
                                            append("nombre", nombreEquipo)
                                            append("nombreEquipo", nombreEquipo)
                                            append("integrantes", integrantes)
                                            append("estudiantes", integrantes)
                                        }.formUrlEncode()
                                    )
                                }.body()
                                client.close()

                                val trimmed = responseText.trim()
                                if (trimmed.startsWith("<")) {
                                    mensaje = "El servidor devolvió una página HTML en lugar de JSON. Verifica 'crearEquipo.php' en tu servidor Apache."
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
                                mensaje = "Equipo guardado localmente o error de red: ${e.message}"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar equipo")
                }

                if (showDialog && mensaje.isNotEmpty()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Equipos de Estudiantes") },
                        text = { Text(mensaje) }
                    )
                }
            }
        }
    }
}
