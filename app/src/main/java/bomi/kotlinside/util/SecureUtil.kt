package bomi.kotlinside.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Base64
import bomi.kotlinside.R
import com.google.android.gms.common.util.Base64Utils
import java.io.File
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.random.Random

@Suppress("unused")
object SecureUtil {
    @JvmStatic
    fun validateAppSignature(context: Context): List<String> {
        val signatureList: List<String>
        val algorithm = context.getString(R.string.hash_key_algorithm)
        val hexArray = context.getString(R.string.hash_key_char_array).toCharArray()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // New signature
            val sig = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo
            signatureList = if (sig.hasMultipleSigners()) {
                // Send all with apkContentsSigners
                sig.apkContentsSigners.map {
                    getSHA1(it.toByteArray(), hexArray, algorithm)
                }
            } else {
                // Send one with signingCertificateHistory
                sig.signingCertificateHistory.map {
                    getSHA1(it.toByteArray(), hexArray, algorithm)
                }
            }
        } else {
            @Suppress("DEPRECATION")
            @SuppressLint("PackageManagerGetSignatures")
            val sig = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            ).signatures
            signatureList = sig.map {
                getSHA1(it.toByteArray(), hexArray, algorithm)
            }
        }

        return signatureList
    }

    @JvmStatic
    fun encrypt(context:Context, text:String?): String {
        var cipherText = ""

        if(!TextUtils.isEmpty(text)) {
            try {
                val secretKey = getSHA256(context, context.getString(R.string.encrypt_secret_key))
                val ivKey = ByteArray(16)
                val iv = IvParameterSpec(Random.Default.nextBytes(ivKey))

                cipherText = Base64.encodeToString(iv.iv + getCipher(context, Cipher.ENCRYPT_MODE, secretKey, iv).doFinal(text?.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)

            } catch (e: Exception) { }
        }

        return cipherText
    }

    @JvmStatic
    fun decrypt(context:Context, text:String?): String? {
        var decodeText : String? = null

        if(!TextUtils.isEmpty(text)) {
            try {
                val byteText = Base64Utils.decode(text ?: "")
                val secretKey = getSHA256(context, context.getString(R.string.encrypt_secret_key))
                val iv = IvParameterSpec(byteText.slice(IntRange(0, 15)).toByteArray())

                decodeText = String(
                    getCipher(context, Cipher.DECRYPT_MODE, secretKey, iv).doFinal(byteText.slice(IntRange(16, byteText.size-1)).toByteArray()),
                    Charsets.UTF_8
                )
            } catch (e: Exception) { }
        }

        return decodeText
    }

    private fun getCipher(context:Context, opMode:Int, secretKey:ByteArray?, iv:IvParameterSpec): Cipher {
        return Cipher.getInstance(context.getString(R.string.encrypt_transformation)).apply {
            init(opMode
                , SecretKeySpec(secretKey, context.getString(R.string.encrypt_algorithm))
                , iv)
        }
    }

    private fun getSHA256(context:Context, str: String) : ByteArray? {
        var sha : ByteArray? = null

        if(!TextUtils.isEmpty(str)) {
            try {
                sha = MessageDigest.getInstance(context.getString(R.string.hash_key_algorithm)).
                    digest(str.toByteArray(StandardCharsets.UTF_8))

            } catch(e: Exception) {}
        }

        return sha
    }

    //computed the sha1 hash of the signature
    @Throws(NoSuchAlgorithmException::class)
    private fun getSHA1(sig: ByteArray, hexArray: CharArray, algorithm: String): String {
        val digest = MessageDigest.getInstance(algorithm)
        digest.update(sig)
        return bytesToHex(hexArray, digest.digest())
    }

    //util method to convert byte array to hex string
    private fun bytesToHex(hexArray: CharArray, bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        var v: Int
        for (j in bytes.indices) {
            v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v.ushr(4)]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        //Log.e("TAG", "TAG::: ${String(hexChars)}")
        return String(hexChars)
    }

    @JvmStatic
    fun isRooting(context: Context): Boolean {
        return doesSuperuserApkExist(context.getString(R.string.rooting_file_path)) || doesSuExits(
            context
        )
    }

    private fun doesSuExits(context: Context): Boolean {
        return try {
            Runtime.getRuntime().exec(context.getString(R.string.rooting_test1))
            Runtime.getRuntime().exec(context.getString(R.string.rooting_test2))
            true
        } catch (e: Exception) {
            false
        }

    }

    private fun doesSuperuserApkExist(s: String): Boolean {
        val rootFile = File(s)
        return rootFile.exists()
    }

    //example:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00:00
    private fun convertHexStringToSha1(context: Context, hexSha: String) {
        if (hexSha.isEmpty()) return

        val hexArray = context.getString(R.string.hash_key_char_array).toCharArray()

        val key = hexSha.replace(":", "")
        val testKey2 = ByteArray(key.length / 2)
        var j = 0
        while (j < key.length) {
            testKey2[j / 2] = ((Character.digit(key[j], 16) shl 4)
                    + Character.digit(key[j + 1], 16)).toByte()
            j += 2
        }

        bytesToHex(hexArray, testKey2)
    }
}