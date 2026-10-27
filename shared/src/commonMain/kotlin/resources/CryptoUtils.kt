package resources

/**
 * Utilidad de criptografía para proteger contraseñas y datos sensibles
 * antes de transmitirlos por la red, previniendo su captura en texto plano
 * mediante herramientas de interceptación proxy como Burp Suite.
 */
object CryptoUtils {

    /**
     * Genera un hash SHA-256 en formato hexadecimal para una cadena de texto.
     * Ejemplo: "123456" -> "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
     */
    fun sha256(input: String): String {
        if (input.isEmpty()) return ""

        val bytes = input.encodeToByteArray()
        val h = intArrayOf(
            0x6a09e667, 0xbb67ae85.toInt(), 0x3c6ef372, -0x5ab00ac5,
            0x510e527f, -0x64fa97a3, 0x1f83d9ab, 0x5be0cd19
        )
        val k = intArrayOf(
            0x428a2f98, 0x71374491, -0x3a3f7269, -0x5c703e1b,
            0x3956c25b, 0x59f111f1, -0x6805c643, -0x09e330b2,
            -0x1121d582, -0x7a83d470, -0x06f91757, 0x78a5636f,
            0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc,
            0x76f988da, -0x67c1a631, -0x5826f03b, -0x442659e5,
            -0x21c221b6, 0x2287236d, -0x34cc1b95, -0x08382c42,
            0x1d3032d8, 0x2b8f800f, 0x4b783ed9, 0x2c10f5a7,
            0x0e6081e7, -0x77d94343, -0x69cb84f4, -0x41f3e792,
            0x106aa070, 0x19a4c116, 0x1e376c08, 0x2748774c,
            0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f,
            0x682e6ff3, 0x748f82ee, 0x78a5636f, -0x7b37303c,
            -0x67397022, -0x5b533d7b, -0x499d63c5, -0x3213032b,
            -0x122a2754, 0x06ca6351, 0x14292967, 0x27b70a85,
            0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354,
            0x766a0abb, -0x7e3136c7, -0x6d8f1e31, -0x41f6e21c,
            -0x310217be, -0x1c9955e6, 0x0113f980, 0x09e1d889
        )

        val bitLen = bytes.size.toLong() * 8
        val paddingSize = ((56 - (bytes.size + 1) % 64) + 64) % 64
        val padded = ByteArray(bytes.size + 1 + paddingSize + 8)
        bytes.copyInto(padded)
        padded[bytes.size] = 0x80.toByte()

        for (i in 0 until 8) {
            padded[padded.size - 1 - i] = (bitLen ushr (i * 8)).toByte()
        }

        val w = IntArray(64)
        for (chunk in padded.indices step 64) {
            for (i in 0 until 16) {
                val j = chunk + i * 4
                w[i] = ((padded[j].toInt() and 0xFF) shl 24) or
                        ((padded[j + 1].toInt() and 0xFF) shl 16) or
                        ((padded[j + 2].toInt() and 0xFF) shl 8) or
                        (padded[j + 3].toInt() and 0xFF)
            }
            for (i in 16 until 64) {
                val s0 = w[i - 15].rotateRight(7) xor w[i - 15].rotateRight(18) xor (w[i - 15] ushr 3)
                val s1 = w[i - 2].rotateRight(17) xor w[i - 2].rotateRight(19) xor (w[i - 2] ushr 10)
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }

            var a = h[0]; var b = h[1]; var c = h[2]; var d = h[3]
            var e = h[4]; var f = h[5]; var g = h[6]; var hVal = h[7]

            for (i in 0 until 64) {
                val S1 = e.rotateRight(6) xor e.rotateRight(11) xor e.rotateRight(25)
                val ch = (e and f) xor (e.inv() and g)
                val temp1 = hVal + S1 + ch + k[i] + w[i]
                val S0 = a.rotateRight(2) xor a.rotateRight(13) xor a.rotateRight(22)
                val maj = (a and b) xor (a and c) xor (b and c)
                val temp2 = S0 + maj

                hVal = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            h[0] += a; h[1] += b; h[2] += c; h[3] += d
            h[4] += e; h[5] += f; h[6] += g; h[7] += hVal
        }

        return h.joinToString("") { i ->
            ((i ushr 24 and 0xFF).toString(16).padStart(2, '0')) +
                    ((i ushr 16 and 0xFF).toString(16).padStart(2, '0')) +
                    ((i ushr 8 and 0xFF).toString(16).padStart(2, '0')) +
                    ((i and 0xFF).toString(16).padStart(2, '0'))
        }
    }

    /**
     * Encripta una contraseña o campo de texto usando cifrado XOR con clave secreta estática
     * y codificación Hex. Esto evita que Burp Suite pueda leer texto plano en las peticiones.
     */
    fun encryptField(input: String, secretKey: String = "ParcialDesarrolloSecretKey2025"): String {
        if (input.isEmpty()) return ""
        val inputBytes = input.encodeToByteArray()
        val keyBytes = secretKey.encodeToByteArray()
        val encryptedBytes = ByteArray(inputBytes.size)

        for (i in inputBytes.indices) {
            encryptedBytes[i] = (inputBytes[i].toInt() xor keyBytes[i % keyBytes.size].toInt()).toByte()
        }

        return encryptedBytes.joinToString("") { b -> (b.toInt() and 0xFF).toString(16).padStart(2, '0') }
    }
}
