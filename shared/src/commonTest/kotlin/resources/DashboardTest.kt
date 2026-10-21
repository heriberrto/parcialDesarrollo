package resources

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DashboardTest {

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
        global.nomUsu = ""

        ObjloginLogin.nombre = ""
        ObjloginLogin.apellido = ""
        ObjloginLogin.nomUsu = ""
        ObjloginLogin.idperfil = 0
    }

    @Test
    fun testDashboardInstantiation() {
        val screen = dashboard()
        assertNotNull(screen)
    }

    @Test
    fun testObtenerNombreUsuarioConectadoDefaultWhenNoUserLogged() {
        val nombre = dashboard.obtenerNombreUsuarioConectado()
        assertEquals("Usuario Conectado", nombre)
    }

    @Test
    fun testObtenerNombreUsuarioConectadoFromObjloginLogin() {
        ObjloginLogin.nomUsu = "Heriberto Pérez"
        val nombre = dashboard.obtenerNombreUsuarioConectado()
        assertEquals("Heriberto Pérez", nombre)
    }

    @Test
    fun testObtenerNombreUsuarioConectadoFromGlobalNombreAndApellido() {
        global.nombre = "Manuel"
        global.apellido = "Tinjaca"
        val nombre = dashboard.obtenerNombreUsuarioConectado()
        assertEquals("Manuel Tinjaca", nombre)
    }

    @Test
    fun testObtenerNombreUsuarioConectadoFromGlobalUsuario() {
        global.usuario = "admin@test.com"
        val nombre = dashboard.obtenerNombreUsuarioConectado()
        assertEquals("admin@test.com", nombre)
    }

    @Test
    fun testObtenerNombreUsuarioConectadoFromGlobalEmail() {
        global.email = "estudiante@universidad.edu.co"
        val nombre = dashboard.obtenerNombreUsuarioConectado()
        assertEquals("estudiante@universidad.edu.co", nombre)
    }

    // ==========================================
    // Role-Based Access Control (RBAC) Tests
    // ==========================================

    @Test
    fun testRolEstudiantePermisos() {
        global.perfil = "Estudiante"
        global.idperfil = 2

        assertEquals(UserRole.ESTUDIANTE, dashboard.obtenerRolActual())
        assertEquals("Estudiante", dashboard.obtenerNombreRolActual())

        // Estudiantes solo tienen acceso a "Evidencias" y "Equipos de estudiantes"
        assertTrue(dashboard.esOpcionPermitidaParaRol("Evidencias", UserRole.ESTUDIANTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Equipos de estudiantes", UserRole.ESTUDIANTE))

        assertFalse(dashboard.esOpcionPermitidaParaRol("Usuarios y roles", UserRole.ESTUDIANTE))
        assertFalse(dashboard.esOpcionPermitidaParaRol("Programas académicos", UserRole.ESTUDIANTE))
        assertFalse(dashboard.esOpcionPermitidaParaRol("Asignaturas", UserRole.ESTUDIANTE))
        assertFalse(dashboard.esOpcionPermitidaParaRol("Proyectos", UserRole.ESTUDIANTE))
        assertFalse(dashboard.esOpcionPermitidaParaRol("Actividades", UserRole.ESTUDIANTE))
        assertFalse(dashboard.esOpcionPermitidaParaRol("Docentes tutores", UserRole.ESTUDIANTE))
    }

    @Test
    fun testRolDocentePermisos() {
        global.perfil = "Docente"
        global.idperfil = 3

        assertEquals(UserRole.DOCENTE, dashboard.obtenerRolActual())
        assertEquals("Docente", dashboard.obtenerNombreRolActual())

        // Docentes tienen acceso a todo excepto "Usuarios y roles"
        assertFalse(dashboard.esOpcionPermitidaParaRol("Usuarios y roles", UserRole.DOCENTE))

        assertTrue(dashboard.esOpcionPermitidaParaRol("Programas académicos", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Asignaturas", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Proyectos", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Actividades", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Evidencias", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Equipos de estudiantes", UserRole.DOCENTE))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Docentes tutores", UserRole.DOCENTE))
    }

    @Test
    fun testRolAdministradorPermisos() {
        global.perfil = "Administrador"
        global.idperfil = 1

        assertEquals(UserRole.ADMINISTRADOR, dashboard.obtenerRolActual())
        assertEquals("Administrador", dashboard.obtenerNombreRolActual())

        // Administradores tienen acceso a todo
        assertTrue(dashboard.esOpcionPermitidaParaRol("Usuarios y roles", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Programas académicos", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Asignaturas", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Proyectos", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Actividades", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Evidencias", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Equipos de estudiantes", UserRole.ADMINISTRADOR))
        assertTrue(dashboard.esOpcionPermitidaParaRol("Docentes tutores", UserRole.ADMINISTRADOR))
    }

    @Test
    fun testCerrarSesionLimpiaVariablesGlobalesYRol() {
        global.nombre = "Juan"
        global.apellido = "Pérez"
        global.usuario = "juanp"
        global.email = "juan@test.com"
        global.perfil = "Estudiante"
        global.idperfil = 2
        ObjloginLogin.nomUsu = "Juan Pérez"

        // Simulamos cerrar sesión limpiando variables globales
        global.documento = ""
        global.nombre = ""
        global.apellido = ""
        global.email = ""
        global.usuario = ""
        global.clave = ""
        global.perfil = ""
        global.idperfil = 0
        global.nomUsu = ""
        ObjloginLogin.nombre = ""
        ObjloginLogin.apellido = ""
        ObjloginLogin.nomUsu = ""
        ObjloginLogin.idperfil = 0

        assertEquals("Usuario Conectado", dashboard.obtenerNombreUsuarioConectado())
        assertEquals(UserRole.ESTUDIANTE, dashboard.obtenerRolActual())
    }
}
