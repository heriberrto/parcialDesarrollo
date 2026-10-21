package resources

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AsignaturasScreenTest {

    // ==========================================
    // 1. isValidIdPrograma Tests
    // ==========================================

    @Test
    fun testIsValidIdProgramaWithValidStringReturnsTrue() {
        assertTrue(AsignaturasScreen.isValidIdPrograma("1"))
        assertTrue(AsignaturasScreen.isValidIdPrograma("PROG-101"))
        assertTrue(AsignaturasScreen.isValidIdPrograma(" 123 "))
    }

    @Test
    fun testIsValidIdProgramaWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(AsignaturasScreen.isValidIdPrograma(""))
        assertFalse(AsignaturasScreen.isValidIdPrograma("   "))
        assertFalse(AsignaturasScreen.isValidIdPrograma("\t\n"))
    }

    // ==========================================
    // 2. isValidNombre Tests
    // ==========================================

    @Test
    fun testIsValidNombreWithValidStringReturnsTrue() {
        assertTrue(AsignaturasScreen.isValidNombre("Matemáticas I"))
        assertTrue(AsignaturasScreen.isValidNombre(" Programación Orientada a Objetos "))
    }

    @Test
    fun testIsValidNombreWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(AsignaturasScreen.isValidNombre(""))
        assertFalse(AsignaturasScreen.isValidNombre("   "))
        assertFalse(AsignaturasScreen.isValidNombre("\n"))
    }

    // ==========================================
    // 3. isValidCodigo Tests
    // ==========================================

    @Test
    fun testIsValidCodigoWithValidStringReturnsTrue() {
        assertTrue(AsignaturasScreen.isValidCodigo("MAT101"))
        assertTrue(AsignaturasScreen.isValidCodigo(" CS-202 "))
    }

    @Test
    fun testIsValidCodigoWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(AsignaturasScreen.isValidCodigo(""))
        assertFalse(AsignaturasScreen.isValidCodigo("   "))
        assertFalse(AsignaturasScreen.isValidCodigo("\t"))
    }

    // ==========================================
    // 4. isValidPrograma Tests
    // ==========================================

    @Test
    fun testIsValidProgramaWithValidStringReturnsTrue() {
        assertTrue(AsignaturasScreen.isValidPrograma("Ingeniería de Sistemas"))
        assertTrue(AsignaturasScreen.isValidPrograma("5"))
    }

    @Test
    fun testIsValidProgramaWithEmptyOrWhitespaceReturnsFalse() {
        assertFalse(AsignaturasScreen.isValidPrograma(""))
        assertFalse(AsignaturasScreen.isValidPrograma("   "))
        assertFalse(AsignaturasScreen.isValidPrograma("\r\n"))
    }

    // ==========================================
    // 5. isValidAsignatura (4 parameters) Tests
    // ==========================================

    @Test
    fun testIsValidAsignatura4ParamsWithAllValidReturnsTrue() {
        assertTrue(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "1",
                nombre = "Cálculo Diferencial",
                codigo = "MAT201",
                programa = "Ingeniería",
            )
        )
    }

    @Test
    fun testIsValidAsignatura4ParamsWithInvalidIdProgramaReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "  ",
                nombre = "Cálculo Diferencial",
                codigo = "MAT201",
                programa = "Ingeniería",
            )
        )
    }

    @Test
    fun testIsValidAsignatura4ParamsWithInvalidNombreReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "1",
                nombre = "",
                codigo = "MAT201",
                programa = "Ingeniería",
            )
        )
    }

    @Test
    fun testIsValidAsignatura4ParamsWithInvalidCodigoReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "1",
                nombre = "Cálculo Diferencial",
                codigo = "   ",
                programa = "Ingeniería",
            )
        )
    }

    @Test
    fun testIsValidAsignatura4ParamsWithInvalidProgramaReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "1",
                nombre = "Cálculo Diferencial",
                codigo = "MAT201",
                programa = "",
            )
        )
    }

    @Test
    fun testIsValidAsignatura4ParamsWithAllInvalidReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                idPrograma = "",
                nombre = "   ",
                codigo = "",
                programa = "  ",
            )
        )
    }

    // ==========================================
    // 6. isValidAsignatura (3 parameters) Tests
    // ==========================================

    @Test
    fun testIsValidAsignatura3ParamsWithAllValidReturnsTrue() {
        assertTrue(
            AsignaturasScreen.isValidAsignatura(
                nombre = "Física I",
                codigo = "FIS101",
                programa = "Ciencias Básicas",
            )
        )
    }

    @Test
    fun testIsValidAsignatura3ParamsWithInvalidNombreReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                nombre = "  ",
                codigo = "FIS101",
                programa = "Ciencias Básicas",
            )
        )
    }

    @Test
    fun testIsValidAsignatura3ParamsWithInvalidCodigoReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                nombre = "Física I",
                codigo = "",
                programa = "Ciencias Básicas",
            )
        )
    }

    @Test
    fun testIsValidAsignatura3ParamsWithInvalidProgramaReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                nombre = "Física I",
                codigo = "FIS101",
                programa = "   ",
            )
        )
    }

    @Test
    fun testIsValidAsignatura3ParamsWithAllInvalidReturnsFalse() {
        assertFalse(
            AsignaturasScreen.isValidAsignatura(
                nombre = "",
                codigo = "  ",
                programa = "",
            )
        )
    }

    // ==========================================
    // 7. Screen Instantiation Tests
    // ==========================================

    @Test
    fun testAsignaturasScreenInstantiation() {
        val screen = AsignaturasScreen()
        assertNotNull(screen)
    }
}
