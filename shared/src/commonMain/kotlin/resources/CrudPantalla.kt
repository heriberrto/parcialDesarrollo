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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
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

object ApiCliente {
    var BASE_URL: String = "http://192.168.2.8/api/"
}

data class CampoFormulario(
    val clave: String,
    val etiqueta: String,
    val numerico: Boolean = false,
    val esPassword: Boolean = false
)

typealias ElementoCrud = Map<String, String>

fun ElementoCrud.texto(clave: String): String = get(clave).orEmpty()

fun parsearRespuestaCrud(responseText: String, archivoListar: String = ""): Pair<List<ElementoCrud>, String?> {
    val trimmed = responseText.trim()

    if (trimmed.startsWith("<")) {
        val errorSnippet = if (trimmed.contains("<title>", ignoreCase = true)) {
            trimmed.substringAfter("<title>").substringBefore("</title>").trim()
        } else {
            trimmed.take(150)
        }
        val urlInfo = if (archivoListar.isNotEmpty()) "Verifica '$archivoListar' en ${ApiCliente.BASE_URL}." else ""
        return Pair(emptyList(), "Servidor devolvió HTML ($errorSnippet). $urlInfo".trim())
    }

    return try {
        val json = Json { ignoreUnknownKeys = true; isLenient = true }
        val parsed = json.parseToJsonElement(trimmed)
        val list = mutableListOf<ElementoCrud>()
        var mensajeError: String? = null

        if (parsed is JsonArray) {
            parsed.forEach { elem ->
                if (elem is JsonObject) {
                    val map = elem.mapValues { (_, v) -> if (v is JsonPrimitive && v !is JsonNull) v.content else "" }
                    list.add(map)
                }
            }
        } else if (parsed is JsonObject) {
            val serverErr = parsed["error"] ?: parsed["mensaje"] ?: parsed["message"]
            if (serverErr is JsonPrimitive && serverErr !is JsonNull) {
                mensajeError = "Servidor: ${serverErr.content}"
            }
            val data = parsed["datos"] ?: parsed["data"] ?: parsed["usuarios"] ?: parsed["proyectos"] ?: parsed["equipos"] ?: parsed["programas"] ?: parsed["asignaturas"]
            if (data is JsonArray) {
                data.forEach { elem ->
                    if (elem is JsonObject) {
                        val map = elem.mapValues { (_, v) -> if (v is JsonPrimitive && v !is JsonNull) v.content else "" }
                        list.add(map)
                    }
                }
            }
        }

        Pair(list, mensajeError)
    } catch (e: Exception) {
        val urlInfo = if (archivoListar.isNotEmpty()) " (${ApiCliente.BASE_URL}$archivoListar)" else ""
        Pair(emptyList(), "Error al conectar con el servidor$urlInfo: ${e.message}")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrudPantalla(
    titulo: String,
    descripcion: String,
    campos: List<CampoFormulario>,
    archivoListar: String,
    archivoCrear: String,
    archivoModificar: String,
    archivoEliminar: String,
    tituloItem: (ElementoCrud) -> String,
    detalleItem: (ElementoCrud) -> String,
    valoresParaEditar: (ElementoCrud) -> Map<String, String>,
    identificador: (ElementoCrud) -> Map<String, String>,
    validar: (valores: Map<String, String>, editando: Boolean) -> String?,
    textoGuardar: String
) {
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
    var valoresFormulario by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var errorFormulario by remember { mutableStateOf<String?>(null) }

    var itemAEliminar by remember { mutableStateOf<ElementoCrud?>(null) }

    fun cargarDatos() {
        scope.launch {
            cargando = true
            mensajeError = null
            try {
                val responseText: String = client.get(ApiCliente.BASE_URL + archivoListar).body()
                val (list, err) = parsearRespuestaCrud(responseText, archivoListar)
                itemsList = list
                mensajeError = err
            } catch (e: Exception) {
                mensajeError = "Error al conectar con el servidor (${ApiCliente.BASE_URL}$archivoListar): ${e.message}"
                itemsList = emptyList()
            } finally {
                cargando = false
            }
        }
    }

    LaunchedEffect(archivoListar) {
        cargarDatos()
    }

    fun abrirNuevo() {
        itemEditando = null
        valoresFormulario = campos.associate { it.clave to "" }
        errorFormulario = null
        mostrarFormulario = true
    }

    fun abrirEditar(item: ElementoCrud) {
        itemEditando = item
        valoresFormulario = valoresParaEditar(item)
        errorFormulario = null
        mostrarFormulario = true
    }

    fun guardar() {
        val err = validar(valoresFormulario, itemEditando != null)
        if (err != null) {
            errorFormulario = err
            return
        }

        scope.launch {
            cargando = true
            errorFormulario = null
            try {
                val esEditar = itemEditando != null
                val archivoTarget = if (esEditar) archivoModificar else archivoCrear
                val paramsMap = mutableMapOf<String, String>()
                paramsMap.putAll(valoresFormulario)
                if (esEditar && itemEditando != null) {
                    paramsMap.putAll(identificador(itemEditando!!))
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
                    errorFormulario = "El servidor devolvió HTML al guardar en '$archivoTarget'."
                } else {
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
                val paramsMap = identificador(item)
                val responseText: String = client.post(ApiCliente.BASE_URL + archivoEliminar) {
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody(
                        Parameters.build {
                            paramsMap.forEach { (k, v) -> append(k, v) }
                        }.formUrlEncode()
                    )
                }.body()

                val trimmed = responseText.trim()
                if (trimmed.startsWith("<")) {
                    mensajeError = "El servidor devolvió HTML al eliminar en '$archivoEliminar'."
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
                title = { Text(titulo) },
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
                text = descripcion,
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
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "No hay registros guardados en la base de datos. Presione '+' en la parte superior derecha para agregar un nuevo registro.",
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
                                    text = tituloItem(item),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = detalleItem(item),
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
            title = { Text(if (itemEditando == null) "Crear $titulo" else "Editar $titulo") },
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

                    campos.forEach { campo ->
                        val valor = valoresFormulario[campo.clave].orEmpty()
                        OutlinedTextField(
                            value = valor,
                            onValueChange = { nuevoValor ->
                                valoresFormulario = valoresFormulario + (campo.clave to nuevoValor)
                            },
                            label = { Text(campo.etiqueta) },
                            singleLine = true,
                            keyboardOptions = if (campo.numerico) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                            visualTransformation = if (campo.esPassword) PasswordVisualTransformation() else VisualTransformation.None,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { guardar() }) {
                    Text(textoGuardar)
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
            text = { Text("¿Desea eliminar este registro?") },
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
