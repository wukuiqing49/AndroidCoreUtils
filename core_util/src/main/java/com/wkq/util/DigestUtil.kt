package com.wkq.util

import android.util.Base64
import java.security.MessageDigest

object DigestUtil {

    fun md5(text: String): String = digest(text, "MD5")

    fun sha1(text: String): String = digest(text, "SHA-1")

    fun sha256(text: String): String = digest(text, "SHA-256")

    fun base64Encode(text: String): String {
        return Base64.encodeToString(text.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
    }

    fun base64Decode(text: String): String {
        return runCatching {
            String(Base64.decode(text, Base64.NO_WRAP), Charsets.UTF_8)
        }.getOrDefault("")
    }

    private fun digest(text: String, algorithm: String): String {
        val bytes = MessageDigest.getInstance(algorithm).digest(text.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
