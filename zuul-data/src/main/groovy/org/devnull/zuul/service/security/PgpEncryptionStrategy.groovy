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
        def baos = new ByteArrayOutputStream()
        encrypt(new ByteArrayInputStream(value.bytes), baos, key as PGPPublicKey)
        return new String(baos.toByteArray())
    }

    String decrypt(String value, EncryptionKey key) {
        throw new InvalidOperationException("Cannot decrypt data encrypted with public key. No secret key is available.")
    }

    @Override
    Boolean supports(EncryptionKey key) {
        return key?.isPgpKey()
    }

    protected void encrypt(InputStream input, OutputStream output, PGPPublicKey publicKey) {
        def compressedGenerator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP)
        def pgpGenerator = new PGPEncryptedDataGenerator(SYM_ALGORITHM_TYPE, true, new SecureRandom(), PROVIDER)
        pgpGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey))

        def armoredOut = new ArmoredOutputStream(output);
        def pgpOut = pgpGenerator.open(armoredOut, BUFFER_SIZE)
        def compressedOut = compressedGenerator.open(pgpOut)

        writeLiteralData(input, compressedOut)

        [pgpGenerator, compressedGenerator, compressedGenerator, compressedOut, pgpOut, armoredOut]*.close()
    }

    protected void writeLiteralData(InputStream is, OutputStream out) {
        def generator = new PGPLiteralDataGenerator()
        def pgpOut = generator.open(out, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, is.available(), new Date())
        pgpOut << is
        [generator, out, is]*.close()
    }
}
