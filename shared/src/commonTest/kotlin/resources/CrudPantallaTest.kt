package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CrudPantallaTest {

    // ==========================================
    // 1. ApiCliente Tests
    // ==========================================

    @Test
    fun testApiClienteDefaultBaseUrl() {
        assertEquals("http://192.168.2.8/api/", ApiCliente.BASE_URL)
    }

    @Test
    fun testApiClienteCustomBaseUrl() {
        val originalUrl = ApiCliente.BASE_URL
        try {
            ApiCliente.BASE_URL = "http://localhost:8080/api/"
            assertEquals("http://localhost:8080/api/", ApiCliente.BASE_URL)
        } finally {
            ApiCliente.BASE_URL = originalUrl
        }
    }

    // ==========================================
    // 2. CampoFormulario Data Class Tests
    // ==========================================

    @Test
    fun testCampoFormularioDefaultValues() {
        val campo = CampoFormulario(clave = "nombre", etiqueta = "Nombre del usuario")
        assertEquals("nombre", campo.clave)
        assertEquals("Nombre del usuario", campo.etiqueta)
        assertFalse(campo.numerico)
        assertFalse(campo.esPassword)
    }

    @Test
    fun testCampoFormularioCustomValues() {
        val campoNumerico = CampoFormulario(
            clave = "edad",
            etiqueta = "Edad",
            numerico = true,
            esPassword = false,
        )
        assertTrue(campoNumerico.numerico)
        assertFalse(campoNumerico.esPassword)

        val campoPassword = CampoFormulario(
            clave = "password",
            etiqueta = "Contraseña",
            numerico = false,
            esPassword = true,
        )
        assertFalse(campoPassword.numerico)
        assertTrue(campoPassword.esPassword)
    }

    @Test
    fun testCampoFormularioEqualsAndHashCode() {
        val campo1 = CampoFormulario("codigo", "Código de asignatura", numerico = true)
        val campo2 = CampoFormulario("codigo", "Código de asignatura", numerico = true)
        val campo3 = CampoFormulario("codigo", "Diferente", numerico = true)

        assertEquals(campo1, campo2)
        assertEquals(campo1.hashCode(), campo2.hashCode())
        assertNotEquals(campo1, campo3)
    }

    @Test
    fun testCampoFormularioCopy() {
        val original = CampoFormulario("email", "Correo Electrónico")
        val copiaConPassword = original.copy(esPassword = true)

        assertEquals("email", copiaConPassword.clave)
        assertEquals("Correo Electrónico", copiaConPassword.etiqueta)
        assertFalse(copiaConPassword.numerico)
        assertTrue(copiaConPassword.esPassword)
    }

    @Test
    fun testCampoFormularioDestructuring() {
        val campo = CampoFormulario("id_proyecto", "ID Proyecto", numerico = true, esPassword = false)
        val (clave, etiqueta, numerico, esPassword) = campo

        assertEquals("id_proyecto", clave)
        assertEquals("ID Proyecto", etiqueta)
        assertTrue(numerico)
        assertFalse(esPassword)
    }

    @Test
    fun testCampoFormularioToString() {
        val campo = CampoFormulario("titulo", "Título")
        val str = campo.toString()
        assertTrue(str.contains("titulo"))
        assertTrue(str.contains("Título"))
    }

    // ==========================================
    // 3. ElementoCrud.texto Extension Function Tests
    // ==========================================

    @Test
    fun testTextoReturnsValueWhenKeyExists() {
        val elemento: ElementoCrud = mapOf("nombre" to "Juan", "apellido" to "Pérez")
        assertEquals("Juan", elemento.texto("nombre"))
        assertEquals("Pérez", elemento.texto("apellido"))
    }

    @Test
    fun testTextoReturnsEmptyStringWhenKeyMissing() {
        val elemento: ElementoCrud = mapOf("nombre" to "Juan")
        assertEquals("", elemento.texto("rol"))
        assertEquals("", elemento.texto("identificacion"))
    }

    @Test
    fun testTextoReturnsEmptyStringWhenValueIsEmpty() {
        val elemento: ElementoCrud = mapOf("nombre" to "")
        assertEquals("", elemento.texto("nombre"))
    }

    @Test
    fun testTextoHandlesEmptyMap() {
        val elemento: ElementoCrud = emptyMap()
        assertEquals("", elemento.texto("cualquier_clave"))
    }

    @Test
    fun testTextoHandlesSpecialAndNumericStrings() {
        val elemento: ElementoCrud = mapOf(
            "id" to "1024",
            "email" to "user@domain.org",
            "caracteres" to "ñáéíóú!@#$%^&*()"
        )
        assertEquals("1024", elemento.texto("id"))
        assertEquals("user@domain.org", elemento.texto("email"))
        assertEquals("ñáéíóú!@#$%^&*()", elemento.texto("caracteres"))
    }

    // ==========================================
    // 4. Response Parsing (parsearRespuestaCrud) Tests
    // ==========================================

    @Test
    fun testParsearRespuestaCrudDirectJsonArray() {
        val jsonInput = """
            [
                {"id_usuario": "1", "nombre": "Juan", "apellido": "Pérez"},
                {"id_usuario": "2", "nombre": "María", "apellido": "Gómez"}
            ]
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(jsonInput, "listarUsuario.php")

        assertNull(error)
        assertEquals(2, items.size)
        assertEquals("1", items[0].texto("id_usuario"))
        assertEquals("Juan", items[0].texto("nombre"))
        assertEquals("Pérez", items[0].texto("apellido"))
        assertEquals("2", items[1].texto("id_usuario"))
        assertEquals("María", items[1].texto("nombre"))
    }

    @Test
    fun testParsearRespuestaCrudJsonObjectWithDatosArray() {
        val jsonInput = """
            {
                "datos": [
                    {"id_proyecto": "10", "titulo": "Proyecto Integrador A"}
                ]
            }
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(jsonInput, "listarProyectos.php")

        assertNull(error)
        assertEquals(1, items.size)
        assertEquals("10", items[0].texto("id_proyecto"))
        assertEquals("Proyecto Integrador A", items[0].texto("titulo"))
    }

    @Test
    fun testParsearRespuestaCrudJsonObjectWithDataArray() {
        val jsonInput = """
            {
                "data": [
                    {"id_asignatura": "5", "nombre": "Programación Móvil"}
                ]
            }
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(jsonInput)

        assertNull(error)
        assertEquals(1, items.size)
        assertEquals("5", items[0].texto("id_asignatura"))
        assertEquals("Programación Móvil", items[0].texto("nombre"))
    }

    @Test
    fun testParsearRespuestaCrudJsonObjectWithUsuariosArray() {
        val jsonInput = """
            {
                "usuarios": [
                    {"id_usuario": "100", "rol": "Administrador"}
                ]
            }
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(jsonInput)

        assertNull(error)
        assertEquals(1, items.size)
        assertEquals("100", items[0].texto("id_usuario"))
        assertEquals("Administrador", items[0].texto("rol"))
    }

    @Test
    fun testParsearRespuestaCrudJsonObjectWithProyectosEquiposProgramasAsignaturas() {
        val jsonProyectos = """{"proyectos": [{"titulo": "P1"}]}"""
        val jsonEquipos = """{"equipos": [{"nombre": "E1"}]}"""
        val jsonProgramas = """{"programas": [{"nombre": "Prog1"}]}"""
        val jsonAsignaturas = """{"asignaturas": [{"nombre": "Asig1"}]}"""

        assertEquals("P1", parsearRespuestaCrud(jsonProyectos).first[0].texto("titulo"))
        assertEquals("E1", parsearRespuestaCrud(jsonEquipos).first[0].texto("nombre"))
        assertEquals("Prog1", parsearRespuestaCrud(jsonProgramas).first[0].texto("nombre"))
        assertEquals("Asig1", parsearRespuestaCrud(jsonAsignaturas).first[0].texto("nombre"))
    }

    @Test
    fun testParsearRespuestaCrudJsonObjectWithServerError() {
        val jsonError = """{"error": "Acceso no autorizado"}"""
        val (items1, err1) = parsearRespuestaCrud(jsonError)
        assertTrue(items1.isEmpty())
        assertNotNull(err1)
        assertTrue(err1.contains("Acceso no autorizado"))

        val jsonMensaje = """{"mensaje": "No hay datos disponibles"}"""
        val (items2, err2) = parsearRespuestaCrud(jsonMensaje)
        assertTrue(items2.isEmpty())
        assertNotNull(err2)
        assertTrue(err2.contains("No hay datos disponibles"))

        val jsonMessage = """{"message": "Database connection lost"}"""
        val (items3, err3) = parsearRespuestaCrud(jsonMessage)
        assertTrue(items3.isEmpty())
        assertNotNull(err3)
        assertTrue(err3.contains("Database connection lost"))
    }

    @Test
    fun testParsearRespuestaCrudHtmlResponseWithTitle() {
        val htmlResponse = """
            <!DOCTYPE html>
            <html>
            <head><title>404 Not Found</title></head>
            <body><h1>Page Not Found</h1></body>
            </html>
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(htmlResponse, "listarUsuario.php")

        assertTrue(items.isEmpty())
        assertNotNull(error)
        assertTrue(error.contains("HTML"))
        assertTrue(error.contains("404 Not Found"))
        assertTrue(error.contains("listarUsuario.php"))
    }

    @Test
    fun testParsearRespuestaCrudHtmlResponseWithoutTitle() {
        val htmlResponse = "<div><span>Fatal Error: Database connection failed</span></div>"

        val (items, error) = parsearRespuestaCrud(htmlResponse)

        assertTrue(items.isEmpty())
        assertNotNull(error)
        assertTrue(error.contains("HTML"))
        assertTrue(error.contains("Fatal Error"))
    }

    @Test
    fun testParsearRespuestaCrudInvalidJson() {
        val invalidJson = "{este no es un json valido: 123"

        val (items, error) = parsearRespuestaCrud(invalidJson)

        assertTrue(items.isEmpty())
        assertNotNull(error)
        assertTrue(error.contains("Error al conectar"))
    }

    @Test
    fun testParsearRespuestaCrudNullAndPrimitiveJsonValues() {
        val jsonInput = """
            [
                {
                    "id": 123,
                    "activo": true,
                    "nombre": "Ana",
                    "nota": null
                }
            ]
        """.trimIndent()

        val (items, error) = parsearRespuestaCrud(jsonInput)

        assertNull(error)
        assertEquals(1, items.size)
        val item = items[0]
        assertEquals("123", item.texto("id"))
        assertEquals("true", item.texto("activo"))
        assertEquals("Ana", item.texto("nombre"))
        assertEquals("", item.texto("nota"))
    }

    // ==========================================
    // 5. CRUD Callback & Mapper Logic Tests
    // ==========================================

    @Test
    fun testTituloAndDetalleItemMappers() {
        val item: ElementoCrud = mapOf(
            "id_usuario" to "10",
            "nombre" to "Carlos",
            "apellido" to "Ramírez",
            "identificacion" to "12345678",
            "email" to "carlos@test.com",
            "rol" to "Docente"
        )

        val tituloItem: (ElementoCrud) -> String = { "${it.texto("nombre")} ${it.texto("apellido")}" }
        val detalleItem: (ElementoCrud) -> String = {
            "ID ${it.texto("id_usuario")} · CC ${it.texto("identificacion")}\n" +
                    "${it.texto("email")} · ${it.texto("rol").ifEmpty { "Sin rol" }}"
        }

        assertEquals("Carlos Ramírez", tituloItem(item))
        assertEquals("ID 10 · CC 12345678\ncarlos@test.com · Docente", detalleItem(item))
    }

    @Test
    fun testValoresParaEditarMapper() {
        val item: ElementoCrud = mapOf(
            "id_usuario" to "15",
            "nombre" to "Laura",
            "apellido" to "Torres",
            "email" to "laura@test.com",
            "identificacion" to "87654321",
            "rol" to "Estudiante, Lider"
        )

        val valoresParaEditar: (ElementoCrud) -> Map<String, String> = {
            mapOf(
                "nombre" to it.texto("nombre"),
                "apellido" to it.texto("apellido"),
                "email" to it.texto("email"),
                "identificacion" to it.texto("identificacion"),
                "password" to "",
                "rol" to it.texto("rol").substringBefore(",").trim()
            )
        }

        val valores = valoresParaEditar(item)
        assertEquals("Laura", valores["nombre"])
        assertEquals("Torres", valores["apellido"])
        assertEquals("laura@test.com", valores["email"])
        assertEquals("87654321", valores["identificacion"])
        assertEquals("", valores["password"])
        assertEquals("Estudiante", valores["rol"])
    }

    @Test
    fun testIdentificadorMapper() {
        val item: ElementoCrud = mapOf("id_proyecto" to "42", "titulo" to "Proyecto X")
        val identificador: (ElementoCrud) -> Map<String, String> = {
            mapOf("id_proyecto" to it.texto("id_proyecto"))
        }

        val idMap = identificador(item)
        assertEquals(1, idMap.size)
        assertEquals("42", idMap["id_proyecto"])
    }

    @Test
    fun testValidarCallbackLogic() {
        val validar: (valores: Map<String, String>, editando: Boolean) -> String? = { v, editando ->
            val nombre = v["nombre"].orEmpty().trim()
            val email = v["email"].orEmpty().trim()
            val password = v["password"].orEmpty()

            when {
                nombre.isEmpty() -> "El nombre es obligatorio."
                !email.contains("@") -> "Email inválido."
                (!editando && password.length < 6) -> "La contraseña debe tener al menos 6 caracteres."
                (editando && password.isNotEmpty() && password.length < 6) -> "La nueva contraseña debe tener al menos 6 caracteres."
                else -> null
            }
        }

        // Test creating with empty name
        assertEquals("El nombre es obligatorio.", validar(mapOf("nombre" to "", "email" to "a@b.com", "password" to "123456"), false))

        // Test creating with invalid email
        assertEquals("Email inválido.", validar(mapOf("nombre" to "Juan", "email" to "invalid-email", "password" to "123456"), false))

        // Test creating with short password
        assertEquals("La contraseña debe tener al menos 6 caracteres.", validar(mapOf("nombre" to "Juan", "email" to "juan@test.com", "password" to "123"), false))

        // Test creating valid
        assertNull(validar(mapOf("nombre" to "Juan", "email" to "juan@test.com", "password" to "123456"), false))

        // Test editing without changing password (password is empty)
        assertNull(validar(mapOf("nombre" to "Juan", "email" to "juan@test.com", "password" to ""), true))

        // Test editing with valid new password
        assertNull(validar(mapOf("nombre" to "Juan", "email" to "juan@test.com", "password" to "654321"), true))

        // Test editing with short new password
        assertEquals("La nueva contraseña debe tener al menos 6 caracteres.", validar(mapOf("nombre" to "Juan", "email" to "juan@test.com", "password" to "12"), true))
    }
}
