package org.devnull.client.spring.cache

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Test

import java.util.concurrent.locks.Lock

import static org.mockito.Mockito.*

class PropertiesObjectFileSystemStoreTest {

    PropertiesObjectFileSystemStore store
    File parent
    Properties properties

    @Before
    void createStore() {
        properties = new Properties()
        properties.setProperty("a.b.c", "def")
        parent = new File("${System.getProperty("java.io.tmpdir")}/test-data")
        parent.exists() ? FileUtils.cleanDirectory(parent) : parent.mkdirs()
        store = new PropertiesObjectFileSystemStore(parent)
    }

    @Test
    void shouldCreateNewPropertiesFileInParentDir() {
        store.put("dev", "test-config", properties)
        def result = getStoredProperties("dev-test-config.properties")
        assert result == properties
    }

    @Test
    void shouldReplaceExistingPropertiesFile() {
        store.put("dev", "test-config", properties)
        def newProperties = new Properties()
        newProperties.setProperty("foo", "bar")
        store.put("dev", "test-config", newProperties)
        def result = getStoredProperties("dev-test-config.properties")
        assert result.size() == 1
        assert result.getProperty("foo") == "bar"
    }

    @Test
    void shouldReadStoredFilesFromParent() {
        store.put("dev", "test-config", properties)
        def result = store.get("dev", "test-config")
        assert result == properties
        assert !result.is(properties)
    }

    @Test
    void shouldLockAndUnlock() {
        store.lock = mock(Lock)
        store.put("dev", "test-config", properties)
        store.get("dev", "test-config")
        verify(store.lock, times(2)).lock()
        verify(store.lock, times(2)).unlock()
    }

    @Test
    void shouldReleaseLocksUponException() {
        store.lock = mock(Lock)
        def exception = null
        try {
            store.put("dev", "test-config", null)
        } catch (Exception e) {
            exception = e
        }
        assert exception
        verify(store.lock).unlock()
    }

    @Test
    void shouldUseTmpDirAsParentByDefault() {
        def expected = new File(System.getProperty("java.io.tmpdir"))
        assert expected.exists()
        assert expected.canWrite()
        def store = new PropertiesObjectFileSystemStore()
        assert store.parent == expected
    }

    @Test(expected=FileNotFoundException)
    void shouldErrorIfGetForFileDoesNotExist() {
        store.get("dev", "should-not-exist")
    }

    protected Properties getStoredProperties(String name) {
        def files = parent.listFiles()
        assert files.size() == 1
        def file = files.first()
        assert file.name == name
        def properties = new Properties()
        def reader = new FileReader(file)
        properties.load(reader)
        reader.close()
        return properties
    }
}
