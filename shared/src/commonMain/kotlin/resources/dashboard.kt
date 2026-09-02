package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator

class dashboard: Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {

            // Título
            Text(
                text = "Dashboard"
            )

            Text(
                text = "Sistema de Gestión de Proyectos Integradores"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Proyectos: --",
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Activos: --",
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Finalizados: --",
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Estudiantes: --",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // Acciones
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = {
                        // TODO: Crear proyecto
                    }
                ) {
                    Text("Nuevo proyecto")
                }

                OutlinedButton(
                    onClick = {
                        // TODO: Ver proyectos
                    }
                ) {
                    Text("Ver proyectos")
                }
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            // Proyectos recientes
            Text(
                text = "Proyectos recientes"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            // Placeholder
            Text(
                text = "No hay proyectos para mostrar"
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

            // Cerrar sesión
            OutlinedButton(
                onClick = {
                    navigator?.pop()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}