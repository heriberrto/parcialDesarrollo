package resources

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LoginTest {

    @BeforeTest
    fun setup() {
        global.documento = ""
        global.nombre = ""
        global.apellido = ""
        global.email = ""
        global.usuario = ""
        global.clave = ""
        global.perfil = ""
        global.idperfil = 0
        global.origen = ""
        global.nomUsu = ""

        ObjloginLogin.nombre = ""
        ObjloginLogin.apellido = ""
        ObjloginLogin.nomUsu = ""
        ObjloginLogin.idperfil = 0
    }

    // ==========================================
    // 1. Screen Instantiation & Constants Tests
    // ==========================================

    @Test
    fun testLoginInstantiation() {
        val screen = Login()
        assertNotNull(screen)
        
        val typealiasScreen = login()
        assertNotNull(typealiasScreen)
    }

    @Test
    fun testLoginConstants() {
        assertEquals("USUARIO", Login.KEY_USU)
        assertEquals("CLAVE", Login.KEY_CLAV)
        assertEquals("admin@test.com", Login.CORRECT_EMAIL)
        assertEquals("123456", Login.CORRECT_PASSWORD)
    }

    // ==========================================
    // 2. Credential Validation Tests
    // ==========================================

    @Test
    fun testIsValidCredentialsWithCorrectDataReturnsTrue() {
        assertTrue(Login.isValidCredentials("admin@test.com", "123456"))
    }

    @Test
    fun testIsValidCredentialsWithIncorrectEmailReturnsFalse() {
        assertFalse(Login.isValidCredentials("wrong@test.com", "123456"))
    }

    @Test
    fun testIsValidCredentialsWithIncorrectPasswordReturnsFalse() {
        assertFalse(Login.isValidCredentials("admin@test.com", "wrongpassword"))
    }

    @Test
    fun testIsValidCredentialsWithEmptyStringsReturnsFalse() {
        assertFalse(Login.isValidCredentials("", ""))
        assertFalse(Login.isValidCredentials("   ", "   "))
    }

    // ==========================================
    // 3. Email Validation Tests
    // ==========================================

    @Test
    fun testIsValidEmailWithValidFormatReturnsTrue() {
        assertTrue(Login.isValidEmail("usuario@dominio.com"))
        assertTrue(Login.isValidEmail("admin@test.co"))
        assertTrue(Login.isValidEmail("  estudiante@universidad.edu  "))
    }

    @Test
    fun testIsValidEmailWithInvalidFormatReturnsFalse() {
        assertFalse(Login.isValidEmail(""))
        assertFalse(Login.isValidEmail("   "))
        assertFalse(Login.isValidEmail("usuario"))
        assertFalse(Login.isValidEmail("usuario@dominio"))
        assertFalse(Login.isValidEmail("usuario.com"))
    }

    // ==========================================
    // 4. Password Validation Tests
    // ==========================================

    @Test
    fun testIsValidPasswordWithSufficientLengthReturnsTrue() {
        assertTrue(Login.isValidPassword("123456"))
        assertTrue(Login.isValidPassword("miClaveSegura123"))
    }

    @Test
    fun testIsValidPasswordWithShortOrEmptyPasswordReturnsFalse() {
        assertFalse(Login.isValidPassword(""))
        assertFalse(Login.isValidPassword("   "))
        assertFalse(Login.isValidPassword("12345"))
    }

    // ==========================================
    // 5. Profile Determination Tests
    // ==========================================

    @Test
    fun testDeterminePerfilForDocenteKeywords() {
        assertEquals("Docente", Login.determinePerfil("docente_juan"))
        assertEquals("Docente", Login.determinePerfil("yamil_profesor@test.com"))
        assertEquals("Docente", Login.determinePerfil("DOCENTE_ACADEMICO"))
    }

    @Test
    fun testDeterminePerfilForEstudianteKeywords() {
        assertEquals("Estudiante", Login.determinePerfil("estudiante_pedro"))
        assertEquals("Estudiante", Login.determinePerfil("heri_estudiante@test.com"))
        assertEquals("Estudiante", Login.determinePerfil("ESTUDIANTE_123"))
    }

    @Test
    fun testDeterminePerfilForAdminOrGeneralUser() {
        assertEquals("Administrador", Login.determinePerfil("admin@test.com"))
        assertEquals("Administrador", Login.determinePerfil("usuario_general"))
        assertEquals("Administrador", Login.determinePerfil(""))
    }

    // ==========================================
    // 6. Profile ID Determination Tests
    // ==========================================

    @Test
    fun testDetermineIdPerfilForKnownRoles() {
        assertEquals(1, Login.determineIdPerfil("Administrador"))
        assertEquals(1, Login.determineIdPerfil("admin"))
        assertEquals(2, Login.determineIdPerfil("Estudiante"))
        assertEquals(3, Login.determineIdPerfil("Docente"))
    }

    @Test
    fun testDetermineIdPerfilForUnknownRoleReturnsDefaultAdminId() {
        assertEquals(1, Login.determineIdPerfil("Invitado"))
        assertEquals(1, Login.determineIdPerfil(""))
    }

    // ==========================================
    // 7. Format User Name Tests
    // ==========================================

    @Test
    fun testFormatNombreUsuarioWithNombreAndApellido() {
        val result = Login.formatNombreUsuario("Carlos", "Gómez", "cgomez")
        assertEquals("Carlos Gómez", result)
    }

    @Test
    fun testFormatNombreUsuarioWithOnlyNombre() {
        val result = Login.formatNombreUsuario("Ana", "", "anita")
        assertEquals("Ana", result)
    }

    @Test
    fun testFormatNombreUsuarioWithEmptyNamesReturnsFallback() {
        val result = Login.formatNombreUsuario("", "", "admin_user")
        assertEquals("admin_user", result)
    }

    // ==========================================
    // 8. Global & Login State Synchronization Tests
    // ==========================================

    @Test
    fun testUpdateGlobalAndLoginStateExplicitData() {
        Login.updateGlobalAndLoginState(
            usuario = "docente@test.com",
            clave = "secret123",
            nombre = "María",
            apellido = "Rodríguez",
            email = "docente@test.com",
            perfil = "Docente",
            idPerfil = 3,
            documento = "1098765432",
        )

        assertEquals("docente@test.com", global.usuario)
        assertEquals("secret123", global.clave)
        assertEquals("María", global.nombre)
        assertEquals("Rodríguez", global.apellido)
        assertEquals("docente@test.com", global.email)
        assertEquals("Docente", global.perfil)
        assertEquals(3, global.idperfil)
        assertEquals("1098765432", global.documento)
        assertEquals("María Rodríguez", global.nomUsu)

        // Verify ObjloginLogin is synchronized
        assertEquals("María", ObjloginLogin.nombre)
        assertEquals("Rodríguez", ObjloginLogin.apellido)
        assertEquals("María Rodríguez", ObjloginLogin.nomUsu)
        assertEquals(3, ObjloginLogin.idperfil)
    }

    @Test
    fun testUpdateGlobalAndLoginStateImplicitRoleInference() {
        Login.updateGlobalAndLoginState(
            usuario = "estudiante_marcos",
            clave = "pass123",
        )

        assertEquals("estudiante_marcos", global.usuario)
        assertEquals("pass123", global.clave)
        assertEquals("Administrador", global.nombre)
        assertEquals("Estudiante", global.perfil)
        assertEquals(2, global.idperfil)
        assertEquals("Administrador", global.nomUsu)

        assertEquals("Administrador", ObjloginLogin.nombre)
        assertEquals("Administrador", ObjloginLogin.nomUsu)
        assertEquals(2, ObjloginLogin.idperfil)
    }

    // ==========================================
    // 9. Global Objects Initial State Verification
    // ==========================================

    @Test
    fun testGlobalObjectsCanBeModifiedAndReset() {
        global.documento = "12345"
        global.nombre = "Prueba"
        global.perfil = "Docente"
        global.idperfil = 3

        assertEquals("12345", global.documento)
        assertEquals("Prueba", global.nombre)
        assertEquals("Docente", global.perfil)
        assertEquals(3, global.idperfil)

        setup()

        assertEquals("", global.documento)
        assertEquals("", global.nombre)
        assertEquals("", global.perfil)
        assertEquals(0, global.idperfil)
    }
}
