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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold

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
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        val opciones = listOf(
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
        ) { paddingValues ->

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp)
            ) {

                // Título
                item {
                    Text(text = "Dashboard")
                    Text(text = "Sistema de Gestión de Proyectos Integradores")
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
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // Menú de módulos
                item {
                    Text(
                        text = "Módulos",
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
                        onClick = { navigator?.pop() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }

    }
}