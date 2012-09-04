package org.devnull.client.spring.cache

import org.springframework.core.io.Resource

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class PropertiesObjectFileSystemStore implements PropertiesObjectStore {
    File parent
    Lock lock = new ReentrantLock()

    PropertiesObjectFileSystemStore() {
        this.parent = new File(System.getProperty("java.io.tmpdir"))
    }

    PropertiesObjectFileSystemStore(File parent) {
        this.parent = parent
    }

    void put(String environment, String name, Properties props) {
        doWhileLocked {
            def file = new File(parent, "${environment}-${name}.properties")
            def writer = new FileWriter(file)
            try {
                props.store(writer, "cached copy")
            } finally {
                writer.close()
            }
        }
    }

    Properties get(String environment, String name) {
        doWhileLocked {
            def props = new Properties()
            def file = new File(parent, "${environment}-${name}.properties")
            if (!file.exists()) {
                throw new FileNotFoundException("Unable to find locally cached copy: ${file.absolutePath}")
            }
            def stream = new FileInputStream(file)
            try {
                props.load(stream)
            } finally {
                stream.close()
            }
            return props
        }
    }

    protected def doWhileLocked = { closure ->
        lock.lock()
        try {
            closure()
        }
        finally {
            lock.unlock()
        }
    }
}