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
 * Módulo: Asignación de docentes tutores.
 * Cubre registro de proyectos a estudiantes/docente y consulta de
 * proyectos asignados al docente.
 */
class DocentesTutoresScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var docente by remember { mutableStateOf("") }
        var proyectoAsignado by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Docentes tutores") },
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
                    "Registro de proyectos a estudiantes y docente, y consulta de " +
                            "proyectos asignados al docente.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = docente,
                    onValueChange = { docente = it },
                    label = { Text("Nombre del docente") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = proyectoAsignado,
                    onValueChange = { proyectoAsignado = it },
                    label = { Text("Proyecto asignado") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { /* TODO: conectar con la lógica/repositorio real */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar asignación")
                }
            }
        }
    }
}