package com.palm.connectapp.util

import android.util.Base64
import android.util.Log
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec


class Utility {

        companion object {

            /***
             * decrypt string scanned from qr code
             *
             * @param value - encrypted string
             * @param secretkey - secret key
             * @return decrypted value
             */
            fun decrypt(value: String,secretkey: String) : String? {
                Log.e("value for encryption",value)
                Security.addProvider(BouncyCastleProvider())
                var keyBytes: ByteArray

                try {
                    keyBytes = secretkey.toByteArray(charset("UTF8"))
                    val skey = SecretKeySpec(keyBytes, "AES")
                    val input = org.bouncycastle.util.encoders.Base64
                        .decode(value?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

                    synchronized(Cipher::class.java) {
                        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                        cipher.init(Cipher.DECRYPT_MODE, skey)

                        val plainText = ByteArray(cipher.getOutputSize(input.size))
                        var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                        ptLength += cipher.doFinal(plainText, ptLength)
                        val decryptedString = String(plainText)
                       return decryptedString.trim { it <= ' ' }
                        Log.e("value for decrpytion", decryptedString.trim { it <= ' ' })
                    }
                }
            catch (uee: UnsupportedEncodingException) {
                uee.printStackTrace()
            } catch (ibse: IllegalBlockSizeException) {
                ibse.printStackTrace()
            } catch (bpe: BadPaddingException) {
                bpe.printStackTrace()
            } catch (ike: InvalidKeyException) {
                ike.printStackTrace()
            } catch (nspe: NoSuchPaddingException) {
                nspe.printStackTrace()
            } catch (nsae: NoSuchAlgorithmException) {
                nsae.printStackTrace()
            } catch (e: ShortBufferException) {
                e.printStackTrace()
            }
                return null
            }

            /***
             * encrypt string
             * @param strToEncrypt - text that need to be encrypt
             * @param secret_key - secret key to pass to encrypt
             * @return return encrypted value
             */
            fun encrypt(strToEncrypt: String, secret_key: String) : String?{
                Security.addProvider(BouncyCastleProvider())
                var keyBytes: ByteArray

                try {
                    keyBytes = secret_key.toByteArray(charset("UTF8"))
                    val skey = SecretKeySpec(keyBytes, "AES")
                    val input = strToEncrypt.toByteArray(charset("UTF8"))

                    synchronized(Cipher::class.java) {
                        val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                        cipher.init(Cipher.ENCRYPT_MODE, skey)

                        val cipherText = ByteArray(cipher.getOutputSize(input.size))
                        var ctLength = cipher.update(
                            input, 0, input.size,
                            cipherText, 0
                        )
                        ctLength += cipher.doFinal(cipherText, ctLength)
                        Log.e("value for encryption", String(Base64.encode(cipherText,ctLength)))
                        return String(
                            Base64.encode(cipherText,ctLength)
                        )
                    }
                } catch (uee: UnsupportedEncodingException) {
                    uee.printStackTrace()
                } catch (ibse: IllegalBlockSizeException) {
                    ibse.printStackTrace()
                } catch (bpe: BadPaddingException) {
                    bpe.printStackTrace()
                } catch (ike: InvalidKeyException) {
                    ike.printStackTrace()
                } catch (nspe: NoSuchPaddingException) {
                    nspe.printStackTrace()
                } catch (nsae: NoSuchAlgorithmException) {
                    nsae.printStackTrace()
                } catch (e: ShortBufferException) {
                    e.printStackTrace()
                }

                return null
            }


        }






}