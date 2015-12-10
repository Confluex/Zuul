package com.confluex.zuul.service.security

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
import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.service.error.InvalidOperationException
import org.springframework.stereotype.Component

import java.security.SecureRandom

@Component("pgpEncryptionStrategy")
@Slf4j
class PgpEncryptionStrategy implements EncryptionStrategy {

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
        return key?.isPgpKey
    }

    protected void encrypt(InputStream input, OutputStream output, PGPPublicKey publicKey) {
        def pgpGenerator = new PGPEncryptedDataGenerator(SYM_ALGORITHM_TYPE, true, new SecureRandom(), PROVIDER)
        pgpGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(publicKey))
        def armoredOut = new ArmoredOutputStream(output)
        def pgpOut = pgpGenerator.open(armoredOut, new byte[1024])
        compress(input, pgpOut)
        [pgpGenerator, pgpOut, armoredOut]*.close()
    }

    void compress(InputStream input, OutputStream out) {
        def literalGenerator = new PGPLiteralDataGenerator()
        def generator = new PGPCompressedDataGenerator(PGPCompressedData.ZIP)
        def compressedOut = generator.open(out)
        def literalOut = literalGenerator.open(compressedOut, PGPLiteralData.BINARY, PGPLiteralData.CONSOLE, input.available(), new Date())
        literalOut << input
        [literalOut, compressedOut, generator, literalGenerator]*.close()
    }


}
