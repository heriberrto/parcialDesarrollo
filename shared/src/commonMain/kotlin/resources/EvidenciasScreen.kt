package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.example.parcial.rememberFilePickerLauncher

data class ArchivoAdjuntoData(
    val nombre: String,
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ArchivoAdjuntoData) return false
        return nombre == other.nombre && bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = nombre.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

class EvidenciasScreen : Screen {

    companion object {
        fun isValidIdActividad(idActividad: String): Boolean =
            idActividad.isNotBlank() && idActividad.trim().toIntOrNull() != null

        fun isValidIdUsuario(idUsuario: String): Boolean =
            idUsuario.isNotBlank() && idUsuario.trim().toIntOrNull() != null

        fun isValidTitulo(titulo: String): Boolean =
            titulo.trim().isNotEmpty()

        fun isValidEvidencia(idActividad: String, idUsuario: String, titulo: String): Boolean {
            return isValidIdActividad(idActividad) && isValidIdUsuario(idUsuario) && isValidTitulo(titulo)
        }

        fun validarFormulario(idActividad: String, idUsuario: String, titulo: String): String? {
            if (titulo.isBlank() || idActividad.isBlank() || idUsuario.isBlank()) {
                return "El título, ID de actividad e ID de usuario son obligatorios."
            }
            if (idActividad.toIntOrNull() == null || idUsuario.toIntOrNull() == null) {
                return "El ID de la actividad y del usuario deben ser números enteros."
            }
            return null
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val scope = rememberCoroutineScope()

        val client = remember {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }

        var itemsList by remember { mutableStateOf<List<ElementoCrud>>(emptyList()) }
        var cargando by remember { mutableStateOf(false) }
        var mensajeError by remember { mutableStateOf<String?>(null) }

        var mostrarFormulario by remember { mutableStateOf(false) }
        var itemEditando by remember { mutableStateOf<ElementoCrud?>(null) }
        var errorFormulario by remember { mutableStateOf<String?>(null) }

        // Campos del formulario
        var idActividad by remember { mutableStateOf("") }
        var idUsuario by remember { mutableStateOf("") }
        var titulo by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var fechaEntrega by remember { mutableStateOf("") }
        var estado by remember { mutableStateOf("Pendiente") }
        var nombreArchivo by remember { mutableStateOf("") }
        var rutaArchivo by remember { mutableStateOf("") }

        // Datos del archivo seleccionado desde el almacenamiento local del dispositivo
        var archivoAdjunto by remember { mutableStateOf<ArchivoAdjuntoData?>(null) }

        var itemAEliminar by remember { mutableStateOf<ElementoCrud?>(null) }

        // Selector de archivos multiplataforma/nativo
        val abirSelectorArchivo = rememberFilePickerLauncher { nombre, bytes ->
            archivoAdjunto = ArchivoAdjuntoData(nombre, bytes)
            nombreArchivo = nombre
            rutaArchivo = "uploads/$nombre"
        }

        fun cargarDatos() {
            scope.launch {
                cargando = true
                mensajeError = null
                try {
                    val responseText: String = client.get(ApiCliente.BASE_URL + "listarEvidencia.php").body()
                    val trimmed = responseText.trim()

                    if (trimmed.startsWith("<")) {
                        val errorSnippet = if (trimmed.contains("<title>", ignoreCase = true)) {
                            trimmed.substringAfter("<title>").substringBefore("</title>").trim()
                        } else {
                            trimmed.take(150)
                        }
                        mensajeError = "Servidor devolvió HTML ($errorSnippet). Verifica 'listarEvidencia.php' en ${ApiCliente.BASE_URL}."
                        itemsList = emptyList()
                        return@launch
                    }

                    val json = Json { ignoreUnknownKeys = true; isLenient = true }
                    val parsed = json.parseToJsonElement(trimmed)
                    val list = mutableListOf<ElementoCrud>()

                    if (parsed is JsonArray) {
                        parsed.forEach { elem ->
                            if (elem is JsonObject) {
                                val map = elem.mapValues { (_, v) ->
                                    if (v is JsonPrimitive && v !is JsonNull) v.content else ""
                                }
                                list.add(map)
                            }
                        }
                    } else if (parsed is JsonObject) {
                        val serverErr = parsed["error"] ?: parsed["mensaje"] ?: parsed["message"]
                        if (serverErr is JsonPrimitive && serverErr !is JsonNull) {
                            mensajeError = "Servidor: ${serverErr.content}"
                        }
                        val data = parsed["datos"] ?: parsed["data"] ?: parsed["evidencias"]
                        if (data is JsonArray) {
                            data.forEach { elem ->
                                if (elem is JsonObject) {
                                    val map = elem.mapValues { (_, v) ->
                                        if (v is JsonPrimitive && v !is JsonNull) v.content else ""
                                    }
                                    list.add(map)
                                }
                            }
                        }
                    }

                    itemsList = list
                } catch (e: Exception) {
                    mensajeError = "Error al conectar con el servidor: ${e.message}"
                    itemsList = emptyList()
                } finally {
                    cargando = false
                }
            }
        }

        LaunchedEffect(Unit) {
            cargarDatos()
        }

        fun abrirNuevo() {
            itemEditando = null
            idActividad = ""
            idUsuario = ""
            titulo = ""
            descripcion = ""
            fechaEntrega = ""
            estado = "Pendiente"
            nombreArchivo = ""
            rutaArchivo = ""
            archivoAdjunto = null
            errorFormulario = null
            mostrarFormulario = true
        }

        fun abrirEditar(item: ElementoCrud) {
            itemEditando = item
            idActividad = item.texto("id_actividad")
            idUsuario = item.texto("id_usuario")
            titulo = item.texto("titulo")
            descripcion = item.texto("descripcion")
            fechaEntrega = item.texto("fecha_entrega")
            estado = item.texto("estado").ifEmpty { "Pendiente" }
            nombreArchivo = item.texto("nombre_archivo")
            rutaArchivo = item.texto("ruta_archivo")
            archivoAdjunto = null
            errorFormulario = null
            mostrarFormulario = true
        }

        fun guardar() {
            val error = validarFormulario(idActividad, idUsuario, titulo)
            if (error != null) {
                errorFormulario = error
                return
            }

            scope.launch {
                cargando = true
                errorFormulario = null
                try {
                    val esEditar = itemEditando != null
                    val idEvidencia = itemEditando?.texto("id_evidencia").orEmpty()
                    val archivoTarget = if (esEditar) "modificarEvidencia.php" else "crearEvidencia.php"

                    val paramsMap = mutableMapOf(
                        "id_actividad" to idActividad,
                        "id_usuario" to idUsuario,
                        "titulo" to titulo,
                        "descripcion" to descripcion,
                        "fecha_entrega" to fechaEntrega,
                        "estado" to estado,
                        "nombre_archivo" to nombreArchivo,
                        "ruta_archivo" to rutaArchivo
                    )
                    if (esEditar) {
                        paramsMap["id_evidencia"] = idEvidencia
                    }

                    val responseText: String = client.post(ApiCliente.BASE_URL + archivoTarget) {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            Parameters.build {
                                paramsMap.forEach { (k, v) -> append(k, v) }
                            }.formUrlEncode()
                        )
                    }.body()

                    val trimmed = responseText.trim()
                    if (trimmed.startsWith("<")) {
                        val errorSnippet = if (trimmed.contains("<title>", ignoreCase = true)) {
                            trimmed.substringAfter("<title>").substringBefore("</title>").trim()
                        } else {
                            trimmed.take(150)
                        }
                        errorFormulario = "Error del servidor ($errorSnippet). Revisa '$archivoTarget'."
                    } else {
                        val json = Json { ignoreUnknownKeys = true; isLenient = true }
                        val parsedObj = try { json.parseToJsonElement(trimmed) as? JsonObject } catch (_: Exception) { null }
                        val isSuccess = parsedObj?.get("success")?.let { (it as? JsonPrimitive)?.content == "true" } ?: true
                        val serverMsg = parsedObj?.get("message") ?: parsedObj?.get("mensaje")

                        if (!isSuccess && serverMsg is JsonPrimitive) {
                            errorFormulario = "Error del servidor: ${serverMsg.content}"
                            return@launch
                        }

                        // Subir archivo binario si se seleccionó uno desde el almacenamiento local
                        if (archivoAdjunto != null) {
                            try {
                                val idEvidenciaActual = if (esEditar) idEvidencia else {
                                    val idVal = parsedObj?.get("id_evidencia")
                                    if (idVal is JsonPrimitive) idVal.content else idEvidencia
                                }

                                client.post(ApiCliente.BASE_URL + "subirArchivoEvidencia.php") {
                                    setBody(
                                        MultiPartFormDataContent(
                                            formData {
                                                append("id_evidencia", idEvidenciaActual)
                                                append(
                                                    "archivo",
                                                    archivoAdjunto!!.bytes,
                                                    Headers.build {
                                                        append(HttpHeaders.ContentDisposition, "filename=\"${archivoAdjunto!!.nombre}\"")
                                                    }
                                                )
                                            }
                                        )
                                    )
                                }
                            } catch (_: Exception) {
                                // Se mantiene la evidencia guardada en la base de datos
                            }
                        }

                        mostrarFormulario = false
                        cargarDatos()
                    }
                } catch (e: Exception) {
                    errorFormulario = "Error al guardar: ${e.message}"
                } finally {
                    cargando = false
                }
            }
        }

        fun eliminar(item: ElementoCrud) {
            scope.launch {
                cargando = true
                try {
                    val idEvidencia = item.texto("id_evidencia")
                    val responseText: String = client.post(ApiCliente.BASE_URL + "eliminarEvidencia.php") {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            Parameters.build {
                                append("id_evidencia", idEvidencia)
                            }.formUrlEncode()
                        )
                    }.body()

                    val trimmed = responseText.trim()
                    if (trimmed.startsWith("<")) {
                        mensajeError = "El servidor devolvió HTML al eliminar en 'eliminarEvidencia.php'."
                    } else {
                        cargarDatos()
                    }
                    itemAEliminar = null
                } catch (e: Exception) {
                    mensajeError = "Error al eliminar: ${e.message}"
                    itemAEliminar = null
                } finally {
                    cargando = false
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Gestión de Evidencias") },
                    navigationIcon = {
                        IconButton(onClick = { navigator?.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { cargarDatos() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
                        }
                        IconButton(onClick = { abrirNuevo() }) {
                            Icon(Icons.Default.Add, contentDescription = "Nuevo")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Registro, consulta, edición y subida de evidencias con archivos adjuntos desde el dispositivo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (mensajeError != null) {
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (itemsList.isEmpty() && !cargando && mensajeError == null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = "No hay evidencias registradas en la base de datos. Presione '+' arriba para registrar una evidencia y adjuntar un archivo.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(itemsList) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.texto("titulo"),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    val arcNombre = item.texto("nombre_archivo")
                                    val adjuntStr = if (arcNombre.isNotBlank()) "\n📎 Adjunto: $arcNombre" else ""
                                    Text(
                                        text = "ID ${item.texto("id_evidencia")} · Estado: ${item.texto("estado")}\n" +
                                                "Actividad: ${item.texto("actividad")} (ID ${item.texto("id_actividad")})\n" +
                                                "Usuario ID: ${item.texto("id_usuario")} · Fecha: ${item.texto("fecha_entrega")}" + adjuntStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(onClick = { abrirEditar(item) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = { itemAEliminar = item }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (mostrarFormulario) {
            AlertDialog(
                onDismissRequest = { mostrarFormulario = false },
                title = { Text(if (itemEditando == null) "Crear Evidencia" else "Editar Evidencia") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (errorFormulario != null) {
                            Text(
                                text = errorFormulario!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        OutlinedTextField(
                            value = idActividad,
                            onValueChange = { idActividad = it },
                            label = { Text("ID de la Actividad") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = idUsuario,
                            onValueChange = { idUsuario = it },
                            label = { Text("ID del Usuario (Estudiante)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título de la Evidencia") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción / Observaciones") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = fechaEntrega,
                            onValueChange = { fechaEntrega = it },
                            label = { Text("Fecha de entrega (YYYY-MM-DD HH:MM)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = estado,
                            onValueChange = { estado = it },
                            label = { Text("Estado (Pendiente, Entregada, Revisada)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Botón selector de archivo nativo del dispositivo
                        Button(
                            onClick = { abirSelectorArchivo() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Adjuntar")
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text(if (archivoAdjunto != null) "Cambiar archivo seleccionado" else "📁 Seleccionar archivo del dispositivo")
                        }

                        if (archivoAdjunto != null) {
                            Text(
                                text = "📄 Seleccionado: ${archivoAdjunto!!.nombre} (${archivoAdjunto!!.bytes.size / 1024} KB)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else if (nombreArchivo.isNotBlank()) {
                            Text(
                                text = "📄 Adjunto actual: $nombreArchivo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { guardar() }) {
                        Text("Guardar Evidencia")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { mostrarFormulario = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        if (itemAEliminar != null) {
            AlertDialog(
                onDismissRequest = { itemAEliminar = null },
                title = { Text("Confirmar eliminación") },
                text = { Text("¿Desea eliminar la evidencia y sus archivos adjuntos?") },
                confirmButton = {
                    Button(onClick = { eliminar(itemAEliminar!!) }) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = { itemAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
