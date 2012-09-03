package org.devnull.client.spring

import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = ['classpath:test-zuul-file-store-context.xml'])
class ZuulFileStoreIntegrationTest extends ZuulClientIntegrationTest {

    @BeforeClass
    static void cleanTestFile() {
        def testFile = getTestFile()
        if (testFile.exists()) {
            testFile.delete()
        }
    }

    @Test
    void shouldSaveFileToFilesystem() {
        def testFile = getTestFile()
        assert testFile.exists()
    }

    @Test
    void shouldStillBeEncrypted() {
        def properties = new Properties()
        def reader = new FileReader(testFile)
        properties.load(reader)
        reader.close()
        assert properties.getProperty("jdbc.zuul.password").startsWith("ENC(")
    }

    static protected File getTestFile() {
        def tmp = new File(System.getProperty("java.io.tmpdir"))
        return new File(tmp, "prod-app-data-config.properties")
    }

}
