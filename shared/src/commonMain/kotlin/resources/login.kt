package resources

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator

class login: Screen {
    @Composable
    override fun Content(){
        val navigator = LocalNavigator.current

        var email by remember {
            mutableStateOf("")
        }

        var password by remember {
            mutableStateOf("")
        }

        var errorMessage by remember {
            mutableStateOf("")
        }

        val correctEmail = "admin@test.com"
        val correctPassword = "123456"

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Iniciar sesión"
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = {
                    Text("Correo electrónico")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                },
                label = {
                    Text("Contraseña")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    if (
                        email == correctEmail &&
                        password == correctPassword
                    ) {

                        // Credenciales correctas
                        errorMessage = ""

                        navigator?.push(
                            dashboard()
                        )

                    } else {

                        // Credenciales incorrectas
                        errorMessage = "Correo o contraseña incorrectos"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = {
                    // navigator.push(RegisterScreen())
                }
            ) {
                Text("¿No tienes una cuenta? Registrarse")
            }
        }
    }
}