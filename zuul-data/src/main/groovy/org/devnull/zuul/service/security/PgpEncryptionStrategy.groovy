package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.apache.commons.lang.NotImplementedException
import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.PGPKeyEncryptionMethodGenerator
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator
import org.devnull.zuul.data.model.EncryptionKey

import java.security.SecureRandom
import java.security.Security

@Slf4j
class PgpEncryptionStrategy implements EncryptionStrategy {

    private final static int BUFFER_SIZE = 1 << 16;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    String encrypt(String value, EncryptionKey key) {
        def publicKey = readPublicKeyFromCollection(new ByteArrayInputStream(new String(key.password).bytes))
        def generator = new PGPEncryptedDataGenerator(PGPEncryptedData.CAST5, true, new SecureRandom(), "BC")
        generator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey))

        def baos = new ByteArrayOutputStream()
        def armoredOutputStream = new ArmoredOutputStream(baos);
        def encryptedOut = generator.open(armoredOutputStream, new byte[BUFFER_SIZE]);
        def compressedDataGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP);
        def compressedOut = compressedDataGenerator.open(encryptedOut);

        writeLiteralData(new ByteArrayInputStream(value.bytes), compressedOut)

        compressedOut.close()
        encryptedOut.close()
        armoredOutputStream.close()
        baos.close()

        return new String(baos.toByteArray())
    }


    String decrypt(String value, EncryptionKey key) {
        log.error("Cannot decrypt {} because key {} is public", value, key)
        return null
    }

    void writeLiteralData(InputStream is, OutputStream out) {
        PGPLiteralDataGenerator generator = new PGPLiteralDataGenerator()
        OutputStream pgpOut = generator.open(out, PGPLiteralData.BINARY, "test.pgp", is.available(), new Date())
        out << is
        generator.close()
        pgpOut.close()
        is.close()
    }


    protected PGPPublicKey readPublicKeyFromCollection(InputStream input) throws Exception {
        def ring = new PGPPublicKeyRing(PGPUtil.getDecoderStream(input))
        return ring.publicKeys?.find { it.encryptionKey } as PGPPublicKey
    }

}
