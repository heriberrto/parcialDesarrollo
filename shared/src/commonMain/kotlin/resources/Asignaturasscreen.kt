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
 * Módulo: Administración de asignaturas.
 * Cubre registro/actualización de asignaturas y consulta de activas.
 */
class AsignaturasScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var nombreAsignatura by remember { mutableStateOf("") }
        var creditos by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Asignaturas") },
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
                    "Registro y actualización de asignaturas, y consulta de las activas.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = nombreAsignatura,
                    onValueChange = { nombreAsignatura = it },
                    label = { Text("Nombre de la asignatura") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = creditos,
                    onValueChange = { creditos = it },
                    label = { Text("Créditos") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { /* TODO: conectar con la lógica/repositorio real */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar asignatura")
                }
            }
        }
    }
}