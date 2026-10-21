package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EvidenciasScreenTest {

    // ==========================================
    // 1. ArchivoAdjuntoData Tests
    // ==========================================

    @Test
    fun testArchivoAdjuntoDataEqualsAndHashCode() {
        val bytes1 = byteArrayOf(1, 2, 3, 4)
        val bytes2 = byteArrayOf(1, 2, 3, 4)
        val bytes3 = byteArrayOf(5, 6, 7, 8)

        val archivo1 = ArchivoAdjuntoData("informe.pdf", bytes1)
        val archivo2 = ArchivoAdjuntoData("informe.pdf", bytes2)
        val archivo3 = ArchivoAdjuntoData("presentacion.pptx", bytes3)

        assertEquals(archivo1, archivo2)
        assertEquals(archivo1.hashCode(), archivo2.hashCode())

        assertNotEquals(archivo1, archivo3)
        assertFalse(archivo1 == archivo3)
    }

    @Test
    fun testArchivoAdjuntoDataProperties() {
        val bytes = byteArrayOf(10, 20, 30)
        val archivo = ArchivoAdjuntoData("codigo_fuente.zip", bytes)

        assertEquals("codigo_fuente.zip", archivo.nombre)
        assertTrue(bytes.contentEquals(archivo.bytes))
    }

    // ==========================================
    // 2. isValidIdActividad Tests
    // ==========================================

    @Test
    fun testIsValidIdActividadWithValidNumericStringReturnsTrue() {
        assertTrue(EvidenciasScreen.isValidIdActividad("1"))
        assertTrue(EvidenciasScreen.isValidIdActividad("100"))
        assertTrue(EvidenciasScreen.isValidIdActividad(" 42 "))
    }

    @Test
    fun testIsValidIdActividadWithInvalidOrNonNumericReturnsFalse() {
        assertFalse(EvidenciasScreen.isValidIdActividad(""))
        assertFalse(EvidenciasScreen.isValidIdActividad("   "))
        assertFalse(EvidenciasScreen.isValidIdActividad("act_1"))
        assertFalse(EvidenciasScreen.isValidIdActividad("12.5"))
    }

    // ==========================================
    // 3. isValidIdUsuario Tests
    // ==========================================

    @Test
    fun testIsValidIdUsuarioWithValidNumericStringReturnsTrue() {
        assertTrue(EvidenciasScreen.isValidIdUsuario("5"))
        assertTrue(EvidenciasScreen.isValidIdUsuario(" 200 "))
    }

    @Test
    fun testIsValidIdUsuarioWithInvalidOrNonNumericReturnsFalse() {
        assertFalse(EvidenciasScreen.isValidIdUsuario(""))
        assertFalse(EvidenciasScreen.isValidIdUsuario("   "))
        assertFalse(EvidenciasScreen.isValidIdUsuario("user_abc"))
        assertFalse(EvidenciasScreen.isValidIdUsuario("3.14"))
    }

    // ==========================================
    // 4. isValidTitulo Tests
    // ==========================================

    @Test
    fun testIsValidTituloWithValidStringReturnsTrue() {
        assertTrue(EvidenciasScreen.isValidTitulo("Entrega de Informe Final"))
        assertTrue(EvidenciasScreen.isValidTitulo(" Avance 1 - Diagramas de Arquitectura "))
    }

    @Test
    fun testIsValidTituloWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(EvidenciasScreen.isValidTitulo(""))
        assertFalse(EvidenciasScreen.isValidTitulo("   "))
        assertFalse(EvidenciasScreen.isValidTitulo("\n\t"))
    }

    // ==========================================
    // 5. isValidEvidencia Tests
    // ==========================================

    @Test
    fun testIsValidEvidenciaWithAllValidInputsReturnsTrue() {
        assertTrue(
            EvidenciasScreen.isValidEvidencia(
                idActividad = "10",
                idUsuario = "3",
                titulo = "Documento de Especificación de Requisitos"
            )
        )
    }

    @Test
    fun testIsValidEvidenciaWithInvalidActividadReturnsFalse() {
        assertFalse(
            EvidenciasScreen.isValidEvidencia(
                idActividad = "abc",
                idUsuario = "3",
                titulo = "Informe Técnico"
            )
        )
    }

    @Test
    fun testIsValidEvidenciaWithInvalidUsuarioReturnsFalse() {
        assertFalse(
            EvidenciasScreen.isValidEvidencia(
                idActividad = "10",
                idUsuario = "",
                titulo = "Informe Técnico"
            )
        )
    }

    @Test
    fun testIsValidEvidenciaWithInvalidTituloReturnsFalse() {
        assertFalse(
            EvidenciasScreen.isValidEvidencia(
                idActividad = "10",
                idUsuario = "3",
                titulo = "   "
            )
        )
    }

    // ==========================================
    // 6. validarFormulario Tests
    // ==========================================

    @Test
    fun testValidarFormularioSuccessWhenAllFieldsValid() {
        val error = EvidenciasScreen.validarFormulario(
            idActividad = "1",
            idUsuario = "2",
            titulo = "Evidencia Sprint 1"
        )
        assertNull(error)
    }

    @Test
    fun testValidarFormularioFailsWhenRequiredFieldsAreMissing() {
        val errorFaltaTitulo = EvidenciasScreen.validarFormulario(
            idActividad = "1",
            idUsuario = "2",
            titulo = ""
        )
        val errorFaltaActividad = EvidenciasScreen.validarFormulario(
            idActividad = "",
            idUsuario = "2",
            titulo = "Evidencia"
        )
        val errorFaltaUsuario = EvidenciasScreen.validarFormulario(
            idActividad = "1",
            idUsuario = "   ",
            titulo = "Evidencia"
        )

        val mensajeEsperado = "El título, ID de actividad e ID de usuario son obligatorios."
        assertEquals(mensajeEsperado, errorFaltaTitulo)
        assertEquals(mensajeEsperado, errorFaltaActividad)
        assertEquals(mensajeEsperado, errorFaltaUsuario)
    }

    @Test
    fun testValidarFormularioFailsWhenIdsAreNotNumeric() {
        val errorActividadNoNumerica = EvidenciasScreen.validarFormulario(
            idActividad = "actividad1",
            idUsuario = "2",
            titulo = "Evidencia Final"
        )
        val errorUsuarioNoNumerico = EvidenciasScreen.validarFormulario(
            idActividad = "1",
            idUsuario = "user_x",
            titulo = "Evidencia Final"
        )

        val mensajeEsperado = "El ID de la actividad y del usuario deben ser números enteros."
        assertEquals(mensajeEsperado, errorActividadNoNumerica)
        assertEquals(mensajeEsperado, errorUsuarioNoNumerico)
    }

    // ==========================================
    // 7. ElementoCrud Formatting & Mapping Tests
    // ==========================================

    @Test
    fun testEvidenciaItemDataMapping() {
        val item: ElementoCrud = mapOf(
            "id_evidencia" to "15",
            "id_actividad" to "4",
            "id_usuario" to "8",
            "actividad" to "Pruebas de Software",
            "titulo" to "Reporte de Cobertura de Pruebas",
            "descripcion" to "Resultados de pruebas unitarias",
            "fecha_entrega" to "2025-05-20",
            "estado" to "Entregada",
            "nombre_archivo" to "cobertura.pdf"
        )

        assertEquals("15", item.texto("id_evidencia"))
        assertEquals("4", item.texto("id_actividad"))
        assertEquals("8", item.texto("id_usuario"))
        assertEquals("Reporte de Cobertura de Pruebas", item.texto("titulo"))
        assertEquals("Entregada", item.texto("estado"))
        assertEquals("cobertura.pdf", item.texto("nombre_archivo"))
    }

    @Test
    fun testEvidenciaItemMappingWithAttachmentString() {
        val item: ElementoCrud = mapOf(
            "id_evidencia" to "20",
            "estado" to "Revisada",
            "actividad" to "Análisis",
            "id_actividad" to "2",
            "id_usuario" to "5",
            "fecha_entrega" to "2025-06-01",
            "nombre_archivo" to "documento.docx"
        )

        val arcNombre = item.texto("nombre_archivo")
        val adjuntStr = if (arcNombre.isNotBlank()) "\n📎 Adjunto: $arcNombre" else ""
        val detalle = "ID ${item.texto("id_evidencia")} · Estado: ${item.texto("estado")}\n" +
                "Actividad: ${item.texto("actividad")} (ID ${item.texto("id_actividad")})\n" +
                "Usuario ID: ${item.texto("id_usuario")} · Fecha: ${item.texto("fecha_entrega")}" + adjuntStr

        assertTrue(detalle.contains("ID 20 · Estado: Revisada"))
        assertTrue(detalle.contains("📎 Adjunto: documento.docx"))
    }

    // ==========================================
    // 8. Screen Instantiation Test
    // ==========================================

    @Test
    fun testEvidenciasScreenInstantiation() {
        val screen = EvidenciasScreen()
        assertNotNull(screen)
    }
}
