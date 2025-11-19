package com.example.mobicash.core.security


object HashUtils {

    /**
     * Genera un hash SHA-256 del texto recibido.
     */
    fun sha256(input: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifica si el texto recibido coincide con el hash almacenado.
     */
    fun verify(input: String, hashed: String): Boolean {
        return sha256(input) == hashed
    }
}
