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
 * Módulo: Registro y administración de proyectos integradores.
 * Mapea con la tabla `proyecto_integrador` (id_asignatura, id_periodo, titulo, descripcion, objetivos, estado).
 */
class ProyectosScreen : Screen {

    companion object {
        fun isValidTitulo(titulo: String): Boolean = titulo.trim().isNotEmpty()
        fun isValidAsignatura(asignatura: String): Boolean = asignatura.trim().isNotEmpty()
        fun isValidPeriodo(periodo: String): Boolean = periodo.trim().isNotEmpty()

        /**
         * Para mantener compatibilidad con pruebas unitarias previas.
         */
        fun isValidNombreProyecto(nombre: String): Boolean = isValidTitulo(nombre)

        fun isValidProyecto(
            titulo: String,
            asignatura: String,
            periodo: String
        ): Boolean {
            return isValidTitulo(titulo) && isValidAsignatura(asignatura) && isValidPeriodo(periodo)
        }

        /**
         * Sobrecarga de compatibilidad con versión previa de 2 parámetros.
         */
        fun isValidProyecto(nombre: String, periodo: String): Boolean {
            return isValidNombreProyecto(nombre) && isValidPeriodo(periodo)
        }
    }

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        var titulo by remember { mutableStateOf("") }
        var asignatura by remember { mutableStateOf("") }
        var periodo by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var objetivos by remember { mutableStateOf("") }

        var mensaje by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Proyectos Integradores") },
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
                    "Registro, consulta y edición de proyectos integradores.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título del proyecto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = asignatura,
                    onValueChange = { asignatura = it },
                    label = { Text("Asignatura asociada") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = periodo,
                    onValueChange = { periodo = it },
                    label = { Text("Período académico") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción del proyecto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = objetivos,
                    onValueChange = { objetivos = it },
                    label = { Text("Objetivos") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (!isValidProyecto(titulo, asignatura, periodo)) {
                            mensaje = "Por favor complete el título, asignatura y período académico."
                            showDialog = true
                            return@Button
                        }

                        scope.launch {
                            try {
                                val client = HttpClient()
                                val responseText: String = client.post("http://192.168.2.13/API/crearProyecto.php") {
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        Parameters.build {
                                            append("titulo", titulo)
                                            append("nombreProyecto", titulo)
                                            append("asignatura", asignatura)
                                            append("id_asignatura", asignatura)
                                            append("periodo", periodo)
                                            append("id_periodo", periodo)
                                            append("descripcion", descripcion)
                                            append("objetivos", objetivos)
                                        }.formUrlEncode()
                                    )
                                }.body()
                                client.close()

                                val trimmed = responseText.trim()
                                if (trimmed.startsWith("<")) {
                                    mensaje = "El servidor devolvió una página HTML en lugar de JSON. Verifica 'crearProyecto.php' en tu servidor Apache."
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
                                mensaje = "Proyecto guardado localmente o error de red: ${e.message}"
                            }
                            showDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar proyecto")
                }

                if (showDialog && mensaje.isNotEmpty()) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            TextButton(onClick = { showDialog = false }) {
                                Text("Aceptar")
                            }
                        },
                        title = { Text("Proyectos Integradores") },
                        text = { Text(mensaje) }
                    )
                }
            }
        }
    }
}
