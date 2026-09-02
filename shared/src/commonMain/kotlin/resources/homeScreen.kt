package resources

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator


class homeScreen: Screen {
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.current

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Trabajo Final de curso"
            )
            Text(
                text = "Titulo: SIGAPI"
            )
            Text(
                text = "Integrantes:"
            )
            Text(
                text = "Heriberto Perez Belandria"
            )
            Text(
                text = "Manuel Tinjaca"
            )
            Spacer(
                modifier = Modifier.height(24.dp)
            )
            Button(
                onClick = {
                    navigator?.push(
                        login()
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Navegacion a pantalla de inicio de sesion")
            }
        }
    }
}