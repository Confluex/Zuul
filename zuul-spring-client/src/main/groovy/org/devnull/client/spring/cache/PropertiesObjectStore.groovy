package org.devnull.client.spring.cache

public interface PropertiesObjectStore {
    void put(String environment, String name, Properties properties)
    Properties get(String environment, String name)
}