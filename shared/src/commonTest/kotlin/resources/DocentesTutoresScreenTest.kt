package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DocentesTutoresScreenTest {

    // ==========================================
    // 1. isValidDocente Tests
    // ==========================================

    @Test
    fun testIsValidDocenteWithValidStringReturnsTrue() {
        assertTrue(DocentesTutoresScreen.isValidDocente("1"))
        assertTrue(DocentesTutoresScreen.isValidDocente("Juan Pérez"))
        assertTrue(DocentesTutoresScreen.isValidDocente(" 10 "))
    }

    @Test
    fun testIsValidDocenteWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(DocentesTutoresScreen.isValidDocente(""))
        assertFalse(DocentesTutoresScreen.isValidDocente("   "))
        assertFalse(DocentesTutoresScreen.isValidDocente("\t\n"))
    }

    // ==========================================
    // 2. isValidProyecto Tests
    // ==========================================

    @Test
    fun testIsValidProyectoWithValidStringReturnsTrue() {
        assertTrue(DocentesTutoresScreen.isValidProyecto("1"))
        assertTrue(DocentesTutoresScreen.isValidProyecto("Proyecto Integrador"))
        assertTrue(DocentesTutoresScreen.isValidProyecto(" 5 "))
    }

    @Test
    fun testIsValidProyectoWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(DocentesTutoresScreen.isValidProyecto(""))
        assertFalse(DocentesTutoresScreen.isValidProyecto("   "))
        assertFalse(DocentesTutoresScreen.isValidProyecto("\n"))
    }

    // ==========================================
    // 3. isValidRolAsignacion Tests
    // ==========================================

    @Test
    fun testIsValidRolAsignacionWithValidStringReturnsTrue() {
        assertTrue(DocentesTutoresScreen.isValidRolAsignacion("Tutor"))
        assertTrue(DocentesTutoresScreen.isValidRolAsignacion("Evaluador"))
        assertTrue(DocentesTutoresScreen.isValidRolAsignacion(" 1 "))
    }

    @Test
    fun testIsValidRolAsignacionWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(DocentesTutoresScreen.isValidRolAsignacion(""))
        assertFalse(DocentesTutoresScreen.isValidRolAsignacion("   "))
        assertFalse(DocentesTutoresScreen.isValidRolAsignacion("\t"))
    }

    // ==========================================
    // 4. isValidAsignacion Tests
    // ==========================================

    @Test
    fun testIsValidAsignacionWithAllValidReturnsTrue() {
        assertTrue(
            DocentesTutoresScreen.isValidAsignacion(
                docente = "1",
                proyecto = "2",
                rolAsignacion = "Tutor",
            )
        )
    }

    @Test
    fun testIsValidAsignacionWithInvalidDocenteReturnsFalse() {
        assertFalse(
            DocentesTutoresScreen.isValidAsignacion(
                docente = "  ",
                proyecto = "2",
                rolAsignacion = "Tutor",
            )
        )
    }

    @Test
    fun testIsValidAsignacionWithInvalidProyectoReturnsFalse() {
        assertFalse(
            DocentesTutoresScreen.isValidAsignacion(
                docente = "1",
                proyecto = "",
                rolAsignacion = "Tutor",
            )
        )
    }

    @Test
    fun testIsValidAsignacionWithInvalidRolAsignacionReturnsFalse() {
        assertFalse(
            DocentesTutoresScreen.isValidAsignacion(
                docente = "1",
                proyecto = "2",
                rolAsignacion = "   ",
            )
        )
    }

    @Test
    fun testIsValidAsignacionWithAllInvalidReturnsFalse() {
        assertFalse(
            DocentesTutoresScreen.isValidAsignacion(
                docente = "",
                proyecto = "  ",
                rolAsignacion = "",
            )
        )
    }

    // ==========================================
    // 5. Form Validation Logic Tests
    // ==========================================

    private fun validarFormulario(v: Map<String, String>): String? {
        val docente = v["id_usuario"].orEmpty()
        val proyecto = v["id_proyecto"].orEmpty()
        return when {
            !DocentesTutoresScreen.isValidAsignacion(docente, proyecto, v["rol_asignacion"].orEmpty()) ->
                "Por favor complete el docente, proyecto y rol de asignación."
            (docente.toIntOrNull() == null) || (proyecto.toIntOrNull() == null) ->
                "El docente y el proyecto deben ser IDs numéricos."
            else -> null
        }
    }

    @Test
    fun testValidationSuccessWhenAllFieldsValid() {
        val valores = mapOf(
            "id_usuario" to "10",
            "id_proyecto" to "5",
            "rol_asignacion" to "Tutor"
        )
        assertNull(validarFormulario(valores))
    }

    @Test
    fun testValidationFailsWhenFieldsAreIncomplete() {
        val incompletoDocente = mapOf("id_usuario" to "", "id_proyecto" to "5", "rol_asignacion" to "Tutor")
        val incompletoProyecto = mapOf("id_usuario" to "10", "id_proyecto" to "  ", "rol_asignacion" to "Tutor")
        val incompletoRol = mapOf("id_usuario" to "10", "id_proyecto" to "5", "rol_asignacion" to "")

        val mensajeEsperado = "Por favor complete el docente, proyecto y rol de asignación."
        assertEquals(mensajeEsperado, validarFormulario(incompletoDocente))
        assertEquals(mensajeEsperado, validarFormulario(incompletoProyecto))
        assertEquals(mensajeEsperado, validarFormulario(incompletoRol))
    }

    @Test
    fun testValidationFailsWhenDocenteIsNotNumeric() {
        val valores = mapOf(
            "id_usuario" to "docente1",
            "id_proyecto" to "5",
            "rol_asignacion" to "Tutor"
        )
        assertEquals("El docente y el proyecto deben ser IDs numéricos.", validarFormulario(valores))
    }

    @Test
    fun testValidationFailsWhenProyectoIsNotNumeric() {
        val valores = mapOf(
            "id_usuario" to "10",
            "id_proyecto" to "xyz",
            "rol_asignacion" to "Evaluador"
        )
        assertEquals("El docente y el proyecto deben ser IDs numéricos.", validarFormulario(valores))
    }

    @Test
    fun testValidationFailsWhenBothDocenteAndProyectoAreNonNumeric() {
        val valores = mapOf(
            "id_usuario" to "abc",
            "id_proyecto" to "def",
            "rol_asignacion" to "Tutor"
        )
        assertEquals("El docente y el proyecto deben ser IDs numéricos.", validarFormulario(valores))
    }

    // ==========================================
    // 6. Item Mapper and Data Formatting Tests
    // ==========================================

    @Test
    fun testTituloItemMapper() {
        val item: ElementoCrud = mapOf(
            "docente" to "María López",
            "rol" to "Evaluador"
        )
        val titulo = "${item.texto("docente")} - ${item.texto("rol")}"
        assertEquals("María López - Evaluador", titulo)
    }

    @Test
    fun testDetalleItemMapper() {
        val item: ElementoCrud = mapOf(
            "id_usuario" to "12",
            "proyecto" to "Plataforma Educativa",
            "id_proyecto" to "3"
        )
        val detalle = "Docente ID ${item.texto("id_usuario")} · Proyecto: ${item.texto("proyecto")} (ID ${item.texto("id_proyecto")})"
        assertEquals("Docente ID 12 · Proyecto: Plataforma Educativa (ID 3)", detalle)
    }

    @Test
    fun testValoresParaEditarMapperWithRolPresent() {
        val item: ElementoCrud = mapOf(
            "id_usuario" to "8",
            "id_proyecto" to "15",
            "rol" to "Tutor",
            "id_rol_asignacion" to "1"
        )
        val valores = mapOf(
            "id_usuario" to item.texto("id_usuario"),
            "id_proyecto" to item.texto("id_proyecto"),
            "rol_asignacion" to item.texto("rol").ifEmpty { item.texto("id_rol_asignacion") }
        )

        assertEquals("8", valores["id_usuario"])
        assertEquals("15", valores["id_proyecto"])
        assertEquals("Tutor", valores["rol_asignacion"])
    }

    @Test
    fun testValoresParaEditarMapperWithRolEmptyFallbackToIdRolAsignacion() {
        val item: ElementoCrud = mapOf(
            "id_usuario" to "8",
            "id_proyecto" to "15",
            "rol" to "",
            "id_rol_asignacion" to "2"
        )
        val valores = mapOf(
            "id_usuario" to item.texto("id_usuario"),
            "id_proyecto" to item.texto("id_proyecto"),
            "rol_asignacion" to item.texto("rol").ifEmpty { item.texto("id_rol_asignacion") }
        )

        assertEquals("8", valores["id_usuario"])
        assertEquals("15", valores["id_proyecto"])
        assertEquals("2", valores["rol_asignacion"])
    }

    @Test
    fun testIdentificadorMapper() {
        val item: ElementoCrud = mapOf(
            "id_proyecto" to "20",
            "id_usuario" to "7",
            "id_rol_asignacion" to "1"
        )
        val identificador = mapOf(
            "orig_id_proyecto" to item.texto("id_proyecto"),
            "orig_id_usuario" to item.texto("id_usuario"),
            "orig_id_rol_asignacion" to item.texto("id_rol_asignacion")
        )

        assertEquals(3, identificador.size)
        assertEquals("20", identificador["orig_id_proyecto"])
        assertEquals("7", identificador["orig_id_usuario"])
        assertEquals("1", identificador["orig_id_rol_asignacion"])
    }

    // ==========================================
    // 7. Screen Instantiation Test
    // ==========================================

    @Test
    fun testDocentesTutoresScreenInstantiation() {
        val screen = DocentesTutoresScreen()
        assertNotNull(screen)
    }
}
