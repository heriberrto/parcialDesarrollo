package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CalificacionesScreenTest {

    // ==========================================
    // 1. isValidIdActividad Tests
    // ==========================================

    @Test
    fun testIsValidIdActividadWithValidNumericStringReturnsTrue() {
        assertTrue(CalificacionesScreen.isValidIdActividad("1"))
        assertTrue(CalificacionesScreen.isValidIdActividad("100"))
        assertTrue(CalificacionesScreen.isValidIdActividad(" 42 "))
    }

    @Test
    fun testIsValidIdActividadWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdActividad(""))
        assertFalse(CalificacionesScreen.isValidIdActividad("   "))
        assertFalse(CalificacionesScreen.isValidIdActividad("\t\n"))
    }

    @Test
    fun testIsValidIdActividadWithNonNumericReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdActividad("act_1"))
        assertFalse(CalificacionesScreen.isValidIdActividad("12.5"))
        assertFalse(CalificacionesScreen.isValidIdActividad("abc"))
    }

    // ==========================================
    // 2. isValidIdUsuario Tests
    // ==========================================

    @Test
    fun testIsValidIdUsuarioWithValidNumericStringReturnsTrue() {
        assertTrue(CalificacionesScreen.isValidIdUsuario("5"))
        assertTrue(CalificacionesScreen.isValidIdUsuario(" 200 "))
        assertTrue(CalificacionesScreen.isValidIdUsuario("10"))
    }

    @Test
    fun testIsValidIdUsuarioWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdUsuario(""))
        assertFalse(CalificacionesScreen.isValidIdUsuario("   "))
        assertFalse(CalificacionesScreen.isValidIdUsuario("\n"))
    }

    @Test
    fun testIsValidIdUsuarioWithNonNumericReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdUsuario("user_abc"))
        assertFalse(CalificacionesScreen.isValidIdUsuario("3.14"))
        assertFalse(CalificacionesScreen.isValidIdUsuario("xyz"))
    }

    // ==========================================
    // 3. isValidIdEvidencia Tests
    // ==========================================

    @Test
    fun testIsValidIdEvidenciaWithValidNumericStringReturnsTrue() {
        assertTrue(CalificacionesScreen.isValidIdEvidencia("10"))
        assertTrue(CalificacionesScreen.isValidIdEvidencia(" 15 "))
        assertTrue(CalificacionesScreen.isValidIdEvidencia("1"))
    }

    @Test
    fun testIsValidIdEvidenciaWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdEvidencia(""))
        assertFalse(CalificacionesScreen.isValidIdEvidencia("   "))
        assertFalse(CalificacionesScreen.isValidIdEvidencia("\t"))
    }

    @Test
    fun testIsValidIdEvidenciaWithNonNumericReturnsFalse() {
        assertFalse(CalificacionesScreen.isValidIdEvidencia("ev_1"))
        assertFalse(CalificacionesScreen.isValidIdEvidencia("4.2"))
        assertFalse(CalificacionesScreen.isValidIdEvidencia("null"))
    }

    // ==========================================
    // 4. isValidCalificacion Tests
    // ==========================================

    @Test
    fun testIsValidCalificacionWithAllValidNumericInputsReturnsTrue() {
        assertTrue(
            CalificacionesScreen.isValidCalificacion(
                idActividad = "10",
                idUsuario = "3",
                idEvidencia = "5",
            )
        )
    }

    @Test
    fun testIsValidCalificacionWithInvalidActividadReturnsFalse() {
        assertFalse(
            CalificacionesScreen.isValidCalificacion(
                idActividad = "abc",
                idUsuario = "3",
                idEvidencia = "5",
            )
        )
    }

    @Test
    fun testIsValidCalificacionWithInvalidUsuarioReturnsFalse() {
        assertFalse(
            CalificacionesScreen.isValidCalificacion(
                idActividad = "10",
                idUsuario = "",
                idEvidencia = "5",
            )
        )
    }

    @Test
    fun testIsValidCalificacionWithInvalidEvidenciaReturnsFalse() {
        assertFalse(
            CalificacionesScreen.isValidCalificacion(
                idActividad = "10",
                idUsuario = "3",
                idEvidencia = "   ",
            )
        )
    }

    @Test
    fun testIsValidCalificacionWithAllInvalidInputsReturnsFalse() {
        assertFalse(
            CalificacionesScreen.isValidCalificacion(
                idActividad = "",
                idUsuario = "  ",
                idEvidencia = "invalid",
            )
        )
    }

    // ==========================================
    // 5. Form Validation Logic Tests
    // ==========================================

    private fun validarFormulario(v: Map<String, String>): String? {
        val evidencia = v["id_evidencia"].orEmpty()
        val actividad = v["id_actividad"].orEmpty()
        val usuario = v["id_usuario"].orEmpty()
        return when {
            !CalificacionesScreen.isValidCalificacion(actividad, usuario, evidencia) ->
                "Evidencia, Actividad y Estudiante deben ser IDs numéricos válidos."
            v["nota"].orEmpty().toDoubleOrNull() == null ->
                "La nota debe ser un número válido (ej. 4.5)."
            else -> null
        }
    }

    @Test
    fun testValidarFormularioSuccessWhenAllFieldsAreValid() {
        val valores = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "2",
            "id_usuario" to "3",
            "nota" to "4.5",
            "observaciones" to "Excelente trabajo"
        )
        assertNull(validarFormulario(valores))
    }

    @Test
    fun testValidarFormularioSuccessWithIntegerNotaAndEmptyObservaciones() {
        val valores = mapOf(
            "id_evidencia" to "10",
            "id_actividad" to "5",
            "id_usuario" to "20",
            "nota" to "5",
            "observaciones" to ""
        )
        assertNull(validarFormulario(valores))
    }

    @Test
    fun testValidarFormularioFailsWhenEvidenciaIsMissingOrInvalid() {
        val valoresFaltaEvidencia = mapOf(
            "id_evidencia" to "",
            "id_actividad" to "2",
            "id_usuario" to "3",
            "nota" to "4.5"
        )
        val valoresEvidenciaNoNumerica = mapOf(
            "id_evidencia" to "ev1",
            "id_actividad" to "2",
            "id_usuario" to "3",
            "nota" to "4.5"
        )

        val mensajeEsperado = "Evidencia, Actividad y Estudiante deben ser IDs numéricos válidos."
        assertEquals(mensajeEsperado, validarFormulario(valoresFaltaEvidencia))
        assertEquals(mensajeEsperado, validarFormulario(valoresEvidenciaNoNumerica))
    }

    @Test
    fun testValidarFormularioFailsWhenActividadIsMissingOrInvalid() {
        val valoresFaltaActividad = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "  ",
            "id_usuario" to "3",
            "nota" to "4.5"
        )
        val valoresActividadNoNumerica = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "act_x",
            "id_usuario" to "3",
            "nota" to "4.5"
        )

        val mensajeEsperado = "Evidencia, Actividad y Estudiante deben ser IDs numéricos válidos."
        assertEquals(mensajeEsperado, validarFormulario(valoresFaltaActividad))
        assertEquals(mensajeEsperado, validarFormulario(valoresActividadNoNumerica))
    }

    @Test
    fun testValidarFormularioFailsWhenUsuarioIsMissingOrInvalid() {
        val valoresFaltaUsuario = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "2",
            "id_usuario" to "",
            "nota" to "4.5"
        )
        val valoresUsuarioNoNumerico = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "2",
            "id_usuario" to "usr_99",
            "nota" to "4.5"
        )

        val mensajeEsperado = "Evidencia, Actividad y Estudiante deben ser IDs numéricos válidos."
        assertEquals(mensajeEsperado, validarFormulario(valoresFaltaUsuario))
        assertEquals(mensajeEsperado, validarFormulario(valoresUsuarioNoNumerico))
    }

    @Test
    fun testValidarFormularioFailsWhenNotaIsMissingOrInvalid() {
        val valoresFaltaNota = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "2",
            "id_usuario" to "3",
            "nota" to ""
        )
        val valoresNotaNoNumerica = mapOf(
            "id_evidencia" to "1",
            "id_actividad" to "2",
            "id_usuario" to "3",
            "nota" to "cuatro"
        )

        val mensajeEsperado = "La nota debe ser un número válido (ej. 4.5)."
        assertEquals(mensajeEsperado, validarFormulario(valoresFaltaNota))
        assertEquals(mensajeEsperado, validarFormulario(valoresNotaNoNumerica))
    }

    // ==========================================
    // 6. Item Mapper and Formatting Tests
    // ==========================================

    @Test
    fun testTituloItemMapper() {
        val item: ElementoCrud = mapOf(
            "nota" to "4.8",
            "evidencia" to "Informe Final"
        )
        val titulo = "Nota: ${item.texto("nota")} - ${item.texto("evidencia")}"
        assertEquals("Nota: 4.8 - Informe Final", titulo)
    }

    @Test
    fun testDetalleItemMapper() {
        val item: ElementoCrud = mapOf(
            "id_calificacion" to "10",
            "estudiante" to "Juan Pérez",
            "id_usuario" to "3",
            "actividad" to "Taller 1",
            "id_actividad" to "5",
            "observaciones" to "Buen análisis"
        )
        val detalle = "ID ${item.texto("id_calificacion")} · Estudiante: ${item.texto("estudiante")} (ID ${item.texto("id_usuario")})\n" +
                "Actividad: ${item.texto("actividad")} (ID ${item.texto("id_actividad")})\n" +
                "Obs: ${item.texto("observaciones")}"

        assertEquals(
            "ID 10 · Estudiante: Juan Pérez (ID 3)\nActividad: Taller 1 (ID 5)\nObs: Buen análisis",
            detalle
        )
    }

    @Test
    fun testValoresParaEditarMapper() {
        val item: ElementoCrud = mapOf(
            "id_evidencia" to "12",
            "id_actividad" to "4",
            "id_usuario" to "8",
            "nota" to "4.2",
            "observaciones" to "Revisar conclusiones"
        )
        val valores = mapOf(
            "id_evidencia" to item.texto("id_evidencia"),
            "id_actividad" to item.texto("id_actividad"),
            "id_usuario" to item.texto("id_usuario"),
            "nota" to item.texto("nota"),
            "observaciones" to item.texto("observaciones")
        )

        assertEquals("12", valores["id_evidencia"])
        assertEquals("4", valores["id_actividad"])
        assertEquals("8", valores["id_usuario"])
        assertEquals("4.2", valores["nota"])
        assertEquals("Revisar conclusiones", valores["observaciones"])
    }

    @Test
    fun testIdentificadorMapper() {
        val item: ElementoCrud = mapOf(
            "id_calificacion" to "15"
        )
        val identificador = mapOf("id_calificacion" to item.texto("id_calificacion"))

        assertEquals(1, identificador.size)
        assertEquals("15", identificador["id_calificacion"])
    }

    // ==========================================
    // 7. Screen Instantiation Test
    // ==========================================

    @Test
    fun testCalificacionesScreenInstantiation() {
        val screen = CalificacionesScreen()
        assertNotNull(screen)
    }
}
