package org.example.parcial

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

@Composable
expect fun rememberFilePickerLauncher(onFileSelected: (nombre: String, bytes: ByteArray) -> Unit): () -> Unit
