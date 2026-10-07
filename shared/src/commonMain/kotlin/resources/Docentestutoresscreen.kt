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
        var docente by remember { mutableStateOf("") }
        var proyectoAsignado by remember { mutableStateOf("") }
        var rolAsignacion by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

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
                    label = { Text("Docente (Nombre o Cédula)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = proyectoAsignado,
                    onValueChange = { proyectoAsignado = it },
                    label = { Text("Proyecto integrador asignado") },
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
                        errorMessage = if (isValidAsignacion(docente, proyectoAsignado, rolAsignacion)) {
                            /* TODO: conectar con repositorio/base de datos */
                            ""
                        } else {
                            "Por favor complete el docente, proyecto y rol de asignación."
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar asignación")
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
