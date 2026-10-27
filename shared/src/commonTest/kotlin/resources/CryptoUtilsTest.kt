package resources

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CryptoUtilsTest {

    @Test
    fun testSha256EmptyInput() {
        assertEquals("", CryptoUtils.sha256(""))
    }

    @Test
    fun testSha256KnownValue() {
        // Known SHA-256 hash of "123456"
        val expected = "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92"
        assertEquals(expected, CryptoUtils.sha256("123456"))
    }

    @Test
    fun testSha256DifferentInputsProduceDifferentHashes() {
        val hash1 = CryptoUtils.sha256("password123")
        val hash2 = CryptoUtils.sha256("password124")
        assertNotEquals(hash1, hash2)
        assertEquals(64, hash1.length)
        assertEquals(64, hash2.length)
    }

    @Test
    fun testEncryptFieldAndFormat() {
        val plainText = "miContraseña123"
        val encrypted = CryptoUtils.encryptField(plainText)

        assertNotEquals(plainText, encrypted)
        assertTrue(encrypted.isNotBlank())
        // Hex representation is twice the length of input bytes
        assertEquals(plainText.encodeToByteArray().size * 2, encrypted.length)
    }

    @Test
    fun testEncryptFieldEmptyInput() {
        assertEquals("", CryptoUtils.encryptField(""))
    }
}
