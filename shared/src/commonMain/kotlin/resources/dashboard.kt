package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

enum class UserRole {
    ESTUDIANTE,
    DOCENTE,
    ADMINISTRADOR
}

/**
 * Opción de menú: título, descripción corta, ícono y la pantalla
 * (Screen de Voyager) a la que navega al presionarla.
 */
private data class MenuOption(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val screen: Screen
)

class dashboard : Screen {

    companion object {
        fun obtenerRolActual(): UserRole {
            val perfilLower = global.perfil.lowercase().trim()
            val usuarioLower = global.usuario.lowercase().trim()
            val emailLower = global.email.lowercase().trim()

            return when {
                global.idperfil == 2 || perfilLower.contains("estudiante") || perfilLower == "2" ||
                        usuarioLower.contains("estudiante") || emailLower.contains("estudiante") ->
                    UserRole.ESTUDIANTE

                global.idperfil == 3 || perfilLower.contains("docente") || perfilLower == "3" ||
                        usuarioLower.contains("docente") || emailLower.contains("docente") ->
                    UserRole.DOCENTE

                global.idperfil == 1 || perfilLower.contains("admin") || perfilLower == "1" ||
                        usuarioLower.contains("admin") || emailLower.contains("admin") ->
                    UserRole.ADMINISTRADOR

                else -> UserRole.ESTUDIANTE
            }
        }

        fun obtenerNombreRolActual(): String {
            return when (obtenerRolActual()) {
                UserRole.ESTUDIANTE -> "Estudiante"
                UserRole.DOCENTE -> "Docente"
                UserRole.ADMINISTRADOR -> "Administrador"
            }
        }

        fun esOpcionPermitidaParaRol(tituloOpcion: String, rol: UserRole): Boolean {
            return when (rol) {
                UserRole.ESTUDIANTE -> {
                    tituloOpcion.equals("Evidencias", ignoreCase = true) ||
                            tituloOpcion.equals("Equipos de estudiantes", ignoreCase = true)
                }
                UserRole.DOCENTE -> {
                    !tituloOpcion.equals("Usuarios y roles", ignoreCase = true)
                }
                UserRole.ADMINISTRADOR -> {
                    true
                }
            }
        }

        fun obtenerNombreUsuarioConectado(): String {
            val nombreCompleto = "${global.nombre} ${global.apellido}".trim()
            val opciones = listOf(
                ObjloginLogin.nomUsu,
                nombreCompleto,
                global.nomUsu,
                global.usuario,
                global.email
            )
            return opciones.firstOrNull { it.isNotBlank() } ?: "Usuario Conectado"
        }
    }

    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current
        var rolDetectado by remember { mutableStateOf(obtenerRolActual()) }

        // Sincronizar rol directamente desde la base de datos (listarUsuario.php)
        LaunchedEffect(global.email, global.usuario) {
            try {
                val client = HttpClient {
                    install(ContentNegotiation) {
                        json(Json { ignoreUnknownKeys = true; isLenient = true })
                    }
                }
                val responseText: String = client.get(ApiCliente.BASE_URL + "listarUsuario.php").body()
                val json = Json { ignoreUnknownKeys = true; isLenient = true }
                val parsed = json.parseToJsonElement(responseText.trim())

                val emailBuscado = global.email.trim().lowercase()
                val usuarioBuscado = global.usuario.trim().lowercase()
                val docBuscado = global.documento.trim()

                var rolServidor: String? = null
                if (parsed is JsonArray) {
                    parsed.forEach { elem ->
                        if (elem is JsonObject) {
                            val map = elem.mapValues { (_, v) -> if (v is JsonPrimitive && v !is JsonNull) v.content else "" }
                            val email = map["email"]?.toString()?.trim()?.lowercase().orEmpty()
                            val ident = map["identificacion"]?.toString()?.trim().orEmpty()

                            if ((emailBuscado.isNotEmpty() && email == emailBuscado) ||
                                (docBuscado.isNotEmpty() && ident == docBuscado) ||
                                (usuarioBuscado.isNotEmpty() && email.contains(usuarioBuscado))
                            ) {
                                rolServidor = map["rol"]?.ifEmpty { map["perfil"] }
                            }
                        }
                    }
                }

                if (!rolServidor.isNullOrBlank()) {
                    global.perfil = rolServidor!!
                    global.idperfil = when (rolServidor!!.lowercase()) {
                        "administrador", "admin" -> 1
                        "estudiante" -> 2
                        "docente" -> 3
                        else -> 2
                    }
                    ObjloginLogin.idperfil = global.idperfil
                    rolDetectado = obtenerRolActual()
                }
            } catch (_: Exception) {
                // Mantiene el rol por defecto
            }
        }

        val rolActual = rolDetectado

        val todasLasOpciones = listOf(
            MenuOption(
                titulo = "Usuarios y roles",
                descripcion = "Registro, consulta, edición y control de acceso",
                icono = Icons.Default.Person,
                screen = UsuariosScreen()
            ),
            MenuOption(
                titulo = "Programas académicos",
                descripcion = "Registro, consulta y edición de programas",
                icono = Icons.Default.School,
                screen = ProgramasAcademicosScreen()
            ),
            MenuOption(
                titulo = "Asignaturas",
                descripcion = "Registro, actualización y consulta de activas",
                icono = Icons.Default.MenuBook,
                screen = AsignaturasScreen()
            ),
            MenuOption(
                titulo = "Proyectos",
                descripcion = "Registro, consulta y edición de proyectos",
                icono = Icons.Default.Assignment,
                screen = ProyectosScreen()
            ),
            MenuOption(
                titulo = "Actividades",
                descripcion = "Creacion y consulta de actividades",
                icono = Icons.Default.SupervisorAccount,
                screen = ActividadesScreen()
            ),
            MenuOption(
                titulo = "Evidencias",
                descripcion = "Subida de evidencias para las actividades en los proyectos",
                icono = Icons.Default.SupervisorAccount,
                screen = EvidenciasScreen()
            ),
            MenuOption(
                titulo = "Equipos de estudiantes",
                descripcion = "Creación y consulta de equipos de trabajo",
                icono = Icons.Default.Groups,
                screen = EquiposScreen()
            ),
            MenuOption(
                titulo = "Docentes tutores",
                descripcion = "Asignación y consulta de proyectos por docente",
                icono = Icons.Default.SupervisorAccount,
                screen = DocentesTutoresScreen()
            )
        )

        // Filtrado por control de acceso basado en roles (RBAC)
        val opciones = todasLasOpciones.filter { opcion ->
            esOpcionPermitidaParaRol(opcion.titulo, rolActual)
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {
                            // Ya estamos en inicio
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Inicio"
                            )
                        },
                        label = {
                            Text("Inicio")
                        }
                    )

                    if (rolActual != UserRole.ESTUDIANTE) {
                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navigator?.push(ProyectosScreen())
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = "Proyectos"
                                )
                            },
                            label = {
                                Text("Proyectos")
                            }
                        )
                    }

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navigator?.push(EquiposScreen())
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = "Equipos"
                            )
                        },
                        label = {
                            Text("Equipos")
                        }
                    )

                    if (rolActual == UserRole.ESTUDIANTE || rolActual == UserRole.DOCENTE) {
                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navigator?.push(EvidenciasScreen())
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SupervisorAccount,
                                    contentDescription = "Evidencias"
                                )
                            },
                            label = {
                                Text("Evidencias")
                            }
                        )
                    }

                    if (rolActual == UserRole.ADMINISTRADOR) {
                        NavigationBarItem(
                            selected = false,
                            onClick = {
                                navigator?.push(UsuariosScreen())
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Usuarios"
                                )
                            },
                            label = {
                                Text("Usuarios")
                            }
                        )
                    }
                }
            }
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {

                // Tarjeta de Usuario Conectado con Rol
                item {
                    val usuarioConectado = obtenerNombreUsuarioConectado()
                    val nombreRol = obtenerNombreRolActual()

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Usuario conectado",
                                modifier = Modifier.size(44.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Column {
                                Text(
                                    text = "Usuario Conectado · Rol: $nombreRol",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = usuarioConectado,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                if (global.email.isNotBlank() && global.email != usuarioConectado) {
                                    Text(
                                        text = global.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }

                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Sistema de Gestión de Proyectos Integradores",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Estadísticas
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Proyectos: --", modifier = Modifier.weight(1f))
                        Text(text = "Activos: --", modifier = Modifier.weight(1f))
                        Text(text = "Finalizados: --", modifier = Modifier.weight(1f))
                        Text(text = "Estudiantes: --", modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Acciones
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (rolActual == UserRole.ESTUDIANTE) {
                            Button(
                                onClick = { navigator?.push(EvidenciasScreen()) }
                            ) {
                                Text("Subir evidencia")
                            }

                            OutlinedButton(
                                onClick = { navigator?.push(EquiposScreen()) }
                            ) {
                                Text("Ver equipos")
                            }
                        } else {
                            Button(
                                onClick = { navigator?.push(ProyectosScreen()) }
                            ) {
                                Text("Nuevo proyecto")
                            }

                            OutlinedButton(
                                onClick = { navigator?.push(ProyectosScreen()) }
                            ) {
                                Text("Ver proyectos")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Menú de módulos
                item {
                    Text(
                        text = "Módulos permitidos (${obtenerNombreRolActual()})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(opciones) { opcion ->
                    Card(
                        onClick = { navigator?.push(opcion.screen) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(opcion.icono, contentDescription = opcion.titulo)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(opcion.titulo, style = MaterialTheme.typography.titleMedium)
                                Text(opcion.descripcion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    // Proyectos recientes
                    Text(text = "Proyectos recientes")
                    Spacer(modifier = Modifier.height(16.dp))

                    // Placeholder
                    Text(text = "No hay proyectos para mostrar")
                    Spacer(modifier = Modifier.height(32.dp))

                    // Cerrar sesión
                    OutlinedButton(
                        onClick = {
                            global.documento = ""
                            global.nombre = ""
                            global.apellido = ""
                            global.email = ""
                            global.usuario = ""
                            global.clave = ""
                            global.perfil = ""
                            global.idperfil = 0
                            global.nomUsu = ""
                            ObjloginLogin.nombre = ""
                            ObjloginLogin.apellido = ""
                            ObjloginLogin.nomUsu = ""
                            ObjloginLogin.idperfil = 0
                            navigator?.pop()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }
    }
}
