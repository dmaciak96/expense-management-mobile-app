package com.expense_management.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.expense_management.core.exception.KeyPairGenerationException
import com.expense_management.domain.model.binary.BinaryValue
import com.expense_management.domain.model.binary.PublicKey
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec

object SecurityUtil {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEYSTORE_ALIAS = "com.expense.management.app.key"
    private const val SIGNATURE_ALGORITHM = "SHA256withECDSA"

    fun getKeyPair(): KeyPair {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
            generateKeyPair()
        }

        val entry = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.PrivateKeyEntry
            ?: throw KeyPairGenerationException("Getting key pair from android keystore failed (keystore is empty)")

        return KeyPair(
            entry.certificate.publicKey,
            entry.privateKey
        )
    }

    fun getPublicKey() = PublicKey.from(getKeyPair().public.encoded)

    fun getPrivateKey(): PrivateKey = getKeyPair().private

    fun sign(payload: ByteArray): ByteArray {
        val privateKey = getPrivateKey()

        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initSign(privateKey)
        signature.update(payload)

        return signature.sign()
    }

    fun verify(
        payload: BinaryValue,
        signatureValue: ByteArray,
        publicKey: PublicKey
    ): Boolean {
        val javaPublicKey = PublicKeyMapper.toJavaPublicKey(publicKey)

        val signature = Signature.getInstance(SIGNATURE_ALGORITHM)
        signature.initVerify(javaPublicKey)
        signature.update(payload.toByteArray())

        return signature.verify(signatureValue)
    }

    private fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            ANDROID_KEYSTORE
        )

        val parameterSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
            .setDigests(
                KeyProperties.DIGEST_SHA256,
                KeyProperties.DIGEST_SHA512
            )
            .build()

        keyPairGenerator.initialize(parameterSpec)
        return keyPairGenerator.generateKeyPair()
    }
}

object PublicKeyMapper {
    fun toJavaPublicKey(publicKey: PublicKey): java.security.PublicKey {
        val keyFactory = KeyFactory.getInstance("EC")
        val keySpec = X509EncodedKeySpec(publicKey.toByteArray())
        return keyFactory.generatePublic(keySpec)
    }
}