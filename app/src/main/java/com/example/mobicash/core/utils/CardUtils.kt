package com.example.mobicash.core.utils

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CardUtils {

    fun sha256(value: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(value.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    fun generateCardNumber(): String {
        val prefix = listOf("4111", "5508").random()
        val middle = (1000..9999).random().toString()
        val end = (100000..999999).random().toString()
        return prefix + middle + end
    }

    // encryptamos
    fun encryptAES(value: String): Pair<String, String> {

        val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val ivBytes = ByteArray(16)
        SecureRandom().nextBytes(ivBytes)

        val ivSpec = IvParameterSpec(ivBytes)
        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)

        val encryptedBytes = cipher.doFinal(value.toByteArray())

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT) to
                Base64.encodeToString(ivBytes, Base64.DEFAULT)
    }

    //desencryptamos
    fun decryptAES(encryptedBase64: String, ivBase64: String): String {
        val secretKey = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        val ivBytes = Base64.decode(ivBase64, Base64.DEFAULT)
        val encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT)

        val ivSpec = IvParameterSpec(ivBytes)

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)

        val decryptedBytes = cipher.doFinal(encryptedBytes)

        return String(decryptedBytes)
    }

    // enmascaramos la tarjeta
    fun maskCardNumber(cardNumber: String): String {
        return cardNumber.replaceRange(0, cardNumber.length - 4, "*".repeat(cardNumber.length - 4))
    }

}