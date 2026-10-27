package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RolesScreenTest {

    @Test
    fun testRolesScreenInstantiation() {
        val screen = RolesScreen()
        assertNotNull(screen)
    }

    @Test
    fun testIsValidIdRol() {
        assertTrue(RolesScreen.isValidIdRol("1"))
        assertTrue(RolesScreen.isValidIdRol(" 2 "))
        assertFalse(RolesScreen.isValidIdRol(""))
        assertFalse(RolesScreen.isValidIdRol("   "))
    }

    @Test
    fun testIsValidNombre() {
        assertTrue(RolesScreen.isValidNombre("Administrador"))
        assertTrue(RolesScreen.isValidNombre(" Docente "))
        assertFalse(RolesScreen.isValidNombre(""))
        assertFalse(RolesScreen.isValidNombre("   "))
    }

    @Test
    fun testIsValidDescripcion() {
        assertTrue(RolesScreen.isValidDescripcion("Acceso total al sistema"))
        assertTrue(RolesScreen.isValidDescripcion(" Gestión de cursos "))
        assertFalse(RolesScreen.isValidDescripcion(""))
        assertFalse(RolesScreen.isValidDescripcion("   "))
    }

    private fun validarFormulario(v: Map<String, String>, editando: Boolean): String? {
        return when {
            editando && !RolesScreen.isValidIdRol(v["id_rol"].orEmpty()) -> "Por favor complete el ID del rol."
            !RolesScreen.isValidNombre(v["nombre"].orEmpty()) -> "Por favor complete el nombre del rol."
            !RolesScreen.isValidDescripcion(v["descripcion"].orEmpty()) -> "Por favor complete la descripción del rol."
            else -> null
        }
    }

    @Test
    fun testValidarFormularioCrearSuccess() {
        val valores = mapOf(
            "nombre" to "Coordinador",
            "descripcion" to "Coordinación académica de programas"
        )
        assertNull(validarFormulario(valores, editando = false))
    }

    @Test
    fun testValidarFormularioModificarSuccess() {
        val valores = mapOf(
            "id_rol" to "5",
            "nombre" to "Tutor",
            "descripcion" to "Acompañamiento a estudiantes"
        )
        assertNull(validarFormulario(valores, editando = true))
    }

    @Test
    fun testValidarFormularioFailsWhenNombreMissing() {
        val valores = mapOf(
            "nombre" to "",
            "descripcion" to "Descripción válida"
        )
        assertEquals("Por favor complete el nombre del rol.", validarFormulario(valores, editando = false))
    }

    @Test
    fun testValidarFormularioFailsWhenDescripcionMissing() {
        val valores = mapOf(
            "nombre" to "Líder",
            "descripcion" to "   "
        )
        assertEquals("Por favor complete la descripción del rol.", validarFormulario(valores, editando = false))
    }
}
