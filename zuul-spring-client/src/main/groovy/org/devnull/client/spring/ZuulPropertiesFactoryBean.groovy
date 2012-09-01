package org.devnull.client.spring

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.cache.CacheConfig
import org.apache.http.impl.client.cache.CachingHttpClient
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor
import org.jasypt.encryption.pbe.config.EnvironmentPBEConfig
import org.jasypt.properties.EncryptableProperties
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.FactoryBean

class ZuulPropertiesFactoryBean implements InitializingBean, DisposableBean, FactoryBean<Properties> {

    final def log = LoggerFactory.getLogger(this.class)
    static final String DEFAULT_PASSWORD_VARIABLE = "ZUUL_PASSWORD"
    static final List<String> OPTIONAL_ATTRIBUTES = ["host", "port", "context", "environment"]

    HttpClient httpClient
    String host = "localhost"
    Integer port = 80
    String context = "/zuul"
    String environment = "dev"
    String config

    ZuulPropertiesFactoryBean(String config) {
        this.config = config
    }

    Properties fetchProperties() {
        def handler = new BasicResponseHandler();
        def get = new HttpGet(uri)
        log.info("Fetching properties from {}", get)
        def responseBody = httpClient.execute(get, handler);
        def properties = new Properties()
        properties.load(new StringReader(responseBody))
        log.debug("Loading properties: {}", properties)
        return properties
    }


    Properties decrypt(Properties properties) {
        def config = new EnvironmentPBEConfig(passwordSysPropertyName: DEFAULT_PASSWORD_VARIABLE)
        def encryptor = new StandardPBEStringEncryptor(config: config)
        return new EncryptableProperties(properties, encryptor)
    }

    URI getUri() {
        return new URI("${httpProtocol}://${host}:${port}${context}/settings/${environment}/${config}.properties")
    }

    String getHttpProtocol() {
        def isSecure = port == 443 || port == 8443
        return isSecure ? "https" : "http"
    }

    void afterPropertiesSet() {
        if (!httpClient) {
            def cache = new CacheConfig();
            cache.maxCacheEntries = 100
            cache.maxObjectSizeBytes = 1024 * 1024
            httpClient = new CachingHttpClient(new DefaultHttpClient(), cache);
        }
    }

    void destroy() {
        httpClient.connectionManager.shutdown()
    }

    Properties getObject() {
        return decrypt(fetchProperties())
    }

    Class<?> getObjectType() {
        return Properties
    }

    boolean isSingleton() {
        return false
    }
}
