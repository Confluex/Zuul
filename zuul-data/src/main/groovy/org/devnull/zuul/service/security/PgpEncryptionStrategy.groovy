package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.PGPCompressedData
import org.bouncycastle.openpgp.PGPCompressedDataGenerator
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator
import org.bouncycastle.openpgp.PGPLiteralData
import org.bouncycastle.openpgp.PGPLiteralDataGenerator
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.PGPPublicKeyRing
import org.bouncycastle.openpgp.PGPUtil
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import org.devnull.zuul.data.model.EncryptionKey
import org.devnull.zuul.service.error.InvalidOperationException
import org.springframework.stereotype.Component

import java.security.SecureRandom

@Component("pgpEncryptionStrategy")
@Slf4j
class PgpEncryptionStrategy implements EncryptionStrategy {

    static final int BUFFER_SIZE = 1024;
    static final String PROVIDER = BouncyCastleProvider.PROVIDER_NAME
    static final int SYM_ALGORITHM_TYPE = PGPEncryptedData.CAST5

    String encrypt(String value, EncryptionKey key) {
        def publicKey = readPublicKeyFromCollection(new ByteArrayInputStream(key.password.bytes))
        def generator = new PGPEncryptedDataGenerator(SYM_ALGORITHM_TYPE, true, new SecureRandom(), PROVIDER)
        generator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey))
        def baos = new ByteArrayOutputStream()
        writeLiteralData(new ByteArrayInputStream(value.bytes), wrapOutputStreamForEncryption(baos, generator))
        generator.close()
        return new String(baos.toByteArray())
    }

    String decrypt(String value, EncryptionKey key) {
        throw new InvalidOperationException("Cannot decrypt data encrypted with public key. No secret key is available.")
    }

    @Override
    Boolean supports(EncryptionKey key) {
        return key?.isPgpKey()
    }

    void writeLiteralData(InputStream is, OutputStream out) {
        def generator = new PGPLiteralDataGenerator()
        def pgpOut = generator.open(out, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, is.available(), new Date())
        pgpOut << is
        generator.close()
        out.close()
        is.close()
    }


    protected PGPPublicKey readPublicKeyFromCollection(InputStream input) throws Exception {
        def ring = new PGPPublicKeyRing(PGPUtil.getDecoderStream(input))
        return ring.publicKeys?.find { it.encryptionKey } as PGPPublicKey
    }

    protected OutputStream wrapOutputStreamForEncryption(OutputStream out, PGPEncryptedDataGenerator generator) {
        def armoredOutputStream = new ArmoredOutputStream(out)
        def encryptedOut = generator.open(armoredOutputStream, new byte[BUFFER_SIZE])
        def compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP)
        return compressedDataGenerator.open(encryptedOut)
    }


}
