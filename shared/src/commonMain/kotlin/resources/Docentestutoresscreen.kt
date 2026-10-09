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
 * Módulo: Asignación de docentes tutores y evaluadores.
 * Mapea con la tabla `asignacion_docente` (id_proyecto, id_usuario, id_rol_asignacion).
 */
class DocentesTutoresScreen : Screen {

    companion object {
        fun isValidDocente(docente: String): Boolean = docente.trim().isNotEmpty()
        fun isValidProyecto(proyecto: String): Boolean = proyecto.trim().isNotEmpty()
        fun isValidRolAsignacion(rol: String): Boolean = rol.trim().isNotEmpty()

        fun isValidAsignacion(docente: String, proyecto: String, rolAsignacion: String): Boolean {
            return isValidDocente(docente) && isValidProyecto(proyecto) && isValidRolAsignacion(rolAsignacion)
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var docente by remember { mutableStateOf("") }
        var proyectoAsignado by remember { mutableStateOf("") }
        var rolAsignacion by remember { mutableStateOf("") }

        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Docentes tutores y evaluadores") },
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
                    "Asignación de docentes a proyectos integradores como tutores o evaluadores.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = docente,
                    onValueChange = { docente = it },
                    label = { Text("Docente (ID de usuario / Cédula)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = proyectoAsignado,
                    onValueChange = { proyectoAsignado = it },
                    label = { Text("Proyecto integrador (ID de proyecto)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rolAsignacion,
                    onValueChange = { rolAsignacion = it },
                    label = { Text("Rol de asignación (Tutor, Evaluador)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (!isValidAsignacion(docente, proyectoAsignado, rolAsignacion)) {
                            mensaje = "Por favor complete el docente, proyecto y rol de asignación."
                            showDialog = true
                            return@Button
                        }

                        scope.launch {
                            try {
                                val client = HttpClient()
                                val responseText: String = client.post("http://192.168.2.13/API/crearAsignacionDocente.php") {
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        Parameters.build {
                                            append("id_usuario", docente)
                                            append("id_docente", docente)
                                            append("docente", docente)
                                            append("id_proyecto", proyectoAsignado)
                                            append("proyecto", proyectoAsignado)
                                            append("rol_asignacion", rolAsignacion)
                                            append("rol", rolAsignacion)
                                            append("id_rol_asignacion", when(rolAsignacion.trim().lowercase()) {
                                                "tutor" -> "1"
                                                "evaluador" -> "2"
                                                else -> "1"
                                            })
                                        }.formUrlEncode()
                                    )
                                }.body()
                                client.close()

                                val trimmed = responseText.trim()
                                if (trimmed.startsWith("<")) {
                                    mensaje = "El servidor devolvió una página HTML en lugar de JSON. Verifica 'crearAsignacionDocente.php' en tu servidor Apache."
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
                                mensaje = "Asignación guardada localmente o error de red: ${e.message}"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar asignación")
                }

                if (showDialog && mensaje.isNotEmpty()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Asignación de Docentes") },
                        text = { Text(mensaje) }
                    )
                }
            }
        }
    }
}
