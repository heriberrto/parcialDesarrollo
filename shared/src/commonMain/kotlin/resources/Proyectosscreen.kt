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
 * Módulo: Registro y administración de proyectos.
 * Cubre registro, consulta y edición de proyectos.
 */
class ProyectosScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var nombreProyecto by remember { mutableStateOf("") }
        var periodo by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Proyectos") },
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
                    value = nombreProyecto,
                    onValueChange = { nombreProyecto = it },
                    label = { Text("Nombre del proyecto") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = periodo,
                    onValueChange = { periodo = it },
                    label = { Text("Período") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { /* TODO: conectar con la lógica/repositorio real */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar proyecto")
                }
            }
        }
    }
}