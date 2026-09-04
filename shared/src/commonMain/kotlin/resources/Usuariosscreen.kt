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
 * Módulo: Administración de usuarios y roles.
 * Cubre registro, consulta y edición de usuarios, y control de acceso por rol.
 */
class UsuariosScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var nombres by remember { mutableStateOf("") }
        var apellidos by remember { mutableStateOf("") }
        var rol by remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Usuarios y roles") },
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
                    "Registro, consulta y edición de usuarios. Autenticación por " +
                            "usuario y contraseña, y control de acceso según el rol.",
                    style = MaterialTheme.typography.bodyMedium
                )

                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = rol,
                    onValueChange = { rol = it },
                    label = { Text("Rol (estudiante, docente, evaluador...)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { /* TODO: conectar con la lógica/repositorio real */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar usuario")
                }
            }
        }
    }
}