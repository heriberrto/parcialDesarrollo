package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

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
        var proyecto by remember { mutableStateOf("") }
        var nombreEquipo by remember { mutableStateOf("") }
        var integrantes by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

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
                    label = { Text("Proyecto integrador asociado") },
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
                    label = { Text("Estudiantes / Integrantes (IDs o Nombres)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        errorMessage = if (isValidEquipo(proyecto, nombreEquipo, integrantes)) {
                            /* TODO: conectar con repositorio/base de datos */
                            ""
                        } else {
                            "Por favor complete el proyecto, nombre del equipo e integrantes."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar equipo")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
