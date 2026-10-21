package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EquiposScreenTest {

    // ==========================================
    // 1. isValidProyecto Tests
    // ==========================================

    @Test
    fun testIsValidProyectoWithValidStringReturnsTrue() {
        assertTrue(EquiposScreen.isValidProyecto("1"))
        assertTrue(EquiposScreen.isValidProyecto("Proyecto Integrador"))
        assertTrue(EquiposScreen.isValidProyecto(" 10 "))
    }

    @Test
    fun testIsValidProyectoWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(EquiposScreen.isValidProyecto(""))
        assertFalse(EquiposScreen.isValidProyecto("   "))
        assertFalse(EquiposScreen.isValidProyecto("\t\n"))
    }

    // ==========================================
    // 2. isValidNombreEquipo Tests
    // ==========================================

    @Test
    fun testIsValidNombreEquipoWithValidStringReturnsTrue() {
        assertTrue(EquiposScreen.isValidNombreEquipo("Equipo Alpha"))
        assertTrue(EquiposScreen.isValidNombreEquipo(" Los Innovadores "))
    }

    @Test
    fun testIsValidNombreEquipoWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(EquiposScreen.isValidNombreEquipo(""))
        assertFalse(EquiposScreen.isValidNombreEquipo("   "))
        assertFalse(EquiposScreen.isValidNombreEquipo("\n"))
    }

    // ==========================================
    // 3. isValidIntegrantes Tests
    // ==========================================

    @Test
    fun testIsValidIntegrantesWithValidStringReturnsTrue() {
        assertTrue(EquiposScreen.isValidIntegrantes("1,2,3"))
        assertTrue(EquiposScreen.isValidIntegrantes(" 5 "))
        assertTrue(EquiposScreen.isValidIntegrantes("1, 2"))
    }

    @Test
    fun testIsValidIntegrantesWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(EquiposScreen.isValidIntegrantes(""))
        assertFalse(EquiposScreen.isValidIntegrantes("   "))
        assertFalse(EquiposScreen.isValidIntegrantes("\t"))
    }

    // ==========================================
    // 4. isValidEquipo Tests
    // ==========================================

    @Test
    fun testIsValidEquipoWithAllValidReturnsTrue() {
        assertTrue(
            EquiposScreen.isValidEquipo(
                proyecto = "1",
                nombreEquipo = "Equipo A",
                integrantes = "1,2,3",
            )
        )
    }

    @Test
    fun testIsValidEquipoWithInvalidProyectoReturnsFalse() {
        assertFalse(
            EquiposScreen.isValidEquipo(
                proyecto = "  ",
                nombreEquipo = "Equipo A",
                integrantes = "1,2,3",
            )
        )
    }

    @Test
    fun testIsValidEquipoWithInvalidNombreEquipoReturnsFalse() {
        assertFalse(
            EquiposScreen.isValidEquipo(
                proyecto = "1",
                nombreEquipo = "",
                integrantes = "1,2,3",
            )
        )
    }

    @Test
    fun testIsValidEquipoWithInvalidIntegrantesReturnsFalse() {
        assertFalse(
            EquiposScreen.isValidEquipo(
                proyecto = "1",
                nombreEquipo = "Equipo A",
                integrantes = "   ",
            )
        )
    }

    @Test
    fun testIsValidEquipoWithAllInvalidReturnsFalse() {
        assertFalse(
            EquiposScreen.isValidEquipo(
                proyecto = "",
                nombreEquipo = "  ",
                integrantes = "",
            )
        )
    }

    // ==========================================
    // 5. isValidListaIds Tests
    // ==========================================

    @Test
    fun testIsValidListaIdsWithSingleValidIdReturnsTrue() {
        assertTrue(EquiposScreen.isValidListaIds("1"))
        assertTrue(EquiposScreen.isValidListaIds(" 42 "))
    }

    @Test
    fun testIsValidListaIdsWithMultipleValidIdsReturnsTrue() {
        assertTrue(EquiposScreen.isValidListaIds("1,2,3"))
        assertTrue(EquiposScreen.isValidListaIds("1, 2, 3"))
        assertTrue(EquiposScreen.isValidListaIds(" 10 , 20 , 30 "))
    }

    @Test
    fun testIsValidListaIdsWithExtraCommasAndValidIdsReturnsTrue() {
        assertTrue(EquiposScreen.isValidListaIds("1,2,"))
        assertTrue(EquiposScreen.isValidListaIds(",1,2"))
        assertTrue(EquiposScreen.isValidListaIds("1,,2"))
    }

    @Test
    fun testIsValidListaIdsWithNonNumericStringReturnsFalse() {
        assertFalse(EquiposScreen.isValidListaIds("abc"))
        assertFalse(EquiposScreen.isValidListaIds("1,a,3"))
        assertFalse(EquiposScreen.isValidListaIds("1, 2.5, 3"))
    }

    @Test
    fun testIsValidListaIdsWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(EquiposScreen.isValidListaIds(""))
        assertFalse(EquiposScreen.isValidListaIds("   "))
        assertFalse(EquiposScreen.isValidListaIds(" , , "))
    }

    @Test
    fun testIsValidListaIdsWithNegativeIntegersReturnsTrue() {
        assertTrue(EquiposScreen.isValidListaIds("-1, 2, -3"))
    }

    @Test
    fun testIsValidListaIdsWithOverflowNumberReturnsFalse() {
        assertFalse(EquiposScreen.isValidListaIds("9999999999999999999999"))
    }

    // ==========================================
    // 6. Validation Logic Tests (EquiposScreen Form Validation)
    // ==========================================

    private fun validarFormulario(v: Map<String, String>): String? {
        val proyecto = v["id_proyecto"].orEmpty()
        val integrantes = v["integrantes"].orEmpty()
        return when {
            !EquiposScreen.isValidEquipo(proyecto, v["nombre"].orEmpty(), integrantes) ->
                "Por favor complete el proyecto, nombre del equipo e integrantes."
            proyecto.toIntOrNull() == null -> "El proyecto debe ser un ID numérico."
            !EquiposScreen.isValidListaIds(integrantes) -> "Los integrantes deben ser IDs numéricos separados por comas."
            else -> null
        }
    }

    @Test
    fun testValidationSuccessWhenAllFieldsValid() {
        val valores = mapOf(
            "id_proyecto" to "5",
            "nombre" to "Equipo Rocket",
            "integrantes" to "3,5,8"
        )
        assertNull(validarFormulario(valores))
    }

    @Test
    fun testValidationFailsWhenFieldsAreIncomplete() {
        val incompletoProyecto = mapOf("id_proyecto" to "", "nombre" to "Equipo Rocket", "integrantes" to "3,5,8")
        val incompletoNombre = mapOf("id_proyecto" to "5", "nombre" to "  ", "integrantes" to "3,5,8")
        val incompletoIntegrantes = mapOf("id_proyecto" to "5", "nombre" to "Equipo Rocket", "integrantes" to "")

        val mensajeEsperado = "Por favor complete el proyecto, nombre del equipo e integrantes."
        assertEquals(mensajeEsperado, validarFormulario(incompletoProyecto))
        assertEquals(mensajeEsperado, validarFormulario(incompletoNombre))
        assertEquals(mensajeEsperado, validarFormulario(incompletoIntegrantes))
    }

    @Test
    fun testValidationFailsWhenProyectoIsNotNumeric() {
        val valores = mapOf(
            "id_proyecto" to "abc",
            "nombre" to "Equipo Rocket",
            "integrantes" to "3,5,8"
        )
        assertEquals("El proyecto debe ser un ID numérico.", validarFormulario(valores))
    }

    @Test
    fun testValidationFailsWhenIntegrantesIsNotNumericList() {
        val valores = mapOf(
            "id_proyecto" to "5",
            "nombre" to "Equipo Rocket",
            "integrantes" to "3, x, 8"
        )
        assertEquals("Los integrantes deben ser IDs numéricos separados por comas.", validarFormulario(valores))
    }

    // ==========================================
    // 7. Item Mapper and Data Formatting Tests
    // ==========================================

    @Test
    fun testTituloItemMapper() {
        val item: ElementoCrud = mapOf("nombre" to "Equipo Titanes")
        val titulo = item.texto("nombre")
        assertEquals("Equipo Titanes", titulo)
    }

    @Test
    fun testDetalleItemMapperWithIntegrantes() {
        val item: ElementoCrud = mapOf(
            "id_equipo" to "12",
            "proyecto" to "Sistema de Gestión",
            "id_proyecto" to "3",
            "nombres_integrantes" to "Juan, María, Carlos"
        )

        val detalle = "ID ${item.texto("id_equipo")} · Proyecto: ${item.texto("proyecto")} (ID ${item.texto("id_proyecto")})\n" +
                "Integrantes: ${item.texto("nombres_integrantes").ifEmpty { "ninguno" }}"

        assertEquals(
            "ID 12 · Proyecto: Sistema de Gestión (ID 3)\nIntegrantes: Juan, María, Carlos",
            detalle
        )
    }

    @Test
    fun testDetalleItemMapperWithoutIntegrantes() {
        val item: ElementoCrud = mapOf(
            "id_equipo" to "15",
            "proyecto" to "App Móvil",
            "id_proyecto" to "4",
            "nombres_integrantes" to ""
        )

        val detalle = "ID ${item.texto("id_equipo")} · Proyecto: ${item.texto("proyecto")} (ID ${item.texto("id_proyecto")})\n" +
                "Integrantes: ${item.texto("nombres_integrantes").ifEmpty { "ninguno" }}"

        assertEquals(
            "ID 15 · Proyecto: App Móvil (ID 4)\nIntegrantes: ninguno",
            detalle
        )
    }

    @Test
    fun testValoresParaEditarMapper() {
        val item: ElementoCrud = mapOf(
            "id_proyecto" to "3",
            "nombre" to "Equipo Dinamita",
            "integrantes" to "10,11,12"
        )

        val valores = mapOf(
            "id_proyecto" to item.texto("id_proyecto"),
            "nombre" to item.texto("nombre"),
            "integrantes" to item.texto("integrantes")
        )

        assertEquals("3", valores["id_proyecto"])
        assertEquals("Equipo Dinamita", valores["nombre"])
        assertEquals("10,11,12", valores["integrantes"])
    }

    @Test
    fun testIdentificadorMapper() {
        val item: ElementoCrud = mapOf("id_equipo" to "99")
        val identificador = mapOf("id_equipo" to item.texto("id_equipo"))

        assertEquals(1, identificador.size)
        assertEquals("99", identificador["id_equipo"])
    }

    // ==========================================
    // 8. Screen Instantiation Test
    // ==========================================

    @Test
    fun testEquiposScreenInstantiation() {
        val screen = EquiposScreen()
        assertNotNull(screen)
    }
}
