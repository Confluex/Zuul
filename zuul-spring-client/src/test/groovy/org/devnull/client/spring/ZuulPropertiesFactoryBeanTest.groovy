package org.devnull.client.spring

import org.apache.http.client.HttpClient
import org.apache.http.client.HttpResponseException
import org.apache.http.client.methods.HttpGet
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.impl.client.BasicResponseHandler
import org.devnull.client.spring.cache.PropertiesObjectStore
import org.jasypt.exceptions.EncryptionOperationNotPossibleException
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.core.io.ClassPathResource

import static org.mockito.Mockito.*

class ZuulPropertiesFactoryBeanTest {

    ZuulPropertiesFactoryBean factory

    @Before
    void createClient() {
        factory = new ZuulPropertiesFactoryBean("app-data-config")
        factory.httpClient = mock(HttpClient)
    }

    @Before
    void setEnvironmentPasswordProperty() {
        System.setProperty(ZuulPropertiesFactoryBean.DEFAULT_PASSWORD_VARIABLE, "badpassword1")
    }

    @Test
    void shouldFetchAndDecryptPropertiesFile() {
        mockResponseFromFile()
        def properties = factory.fetchProperties()
        assert properties
        assert properties.getProperty("jdbc.zuul.password").startsWith("ENC(")
        def decrypted = factory.decrypt(properties)
        assert decrypted.getProperty("jdbc.zuul.password") == "supersecure"
    }

    @Test(expected = EncryptionOperationNotPossibleException)
    void shouldFailToDecryptWithIncorrectPassword() {
        System.setProperty(ZuulPropertiesFactoryBean.DEFAULT_PASSWORD_VARIABLE, "foo")
        def mockResponse = new ClassPathResource("/mock-server-response.properties").inputStream.text
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        when(factory.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenReturn(mockResponse)
        def decrypted = factory.decrypt(factory.fetchProperties())
        decrypted.getProperty("jdbc.zuul.password")
    }

    @Test
    void shouldPreferConfiguredPasswordOverSystemEnvironmentVariable() {
        System.setProperty(ZuulPropertiesFactoryBean.DEFAULT_PASSWORD_VARIABLE, "foo")
        factory.password = "badpassword1"
        def mockResponse = new ClassPathResource("/mock-server-response.properties").inputStream.text
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        when(factory.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenReturn(mockResponse)
        def decrypted = factory.decrypt(factory.fetchProperties())
        decrypted.getProperty("jdbc.zuul.password")
    }

    @Test
    void shouldUseHttpIfPortByDefault() {
        def ports = [80, 8080, 9642]
        assert factory.httpProtocol == "http"
        ports.each {
            factory.port = it
            assert factory.httpProtocol == "http"
        }
    }

    @Test
    void shouldUseHttpsIfPortIs_443() {
        factory.port = 443
        assert factory.httpProtocol == "https"
    }

    @Test
    void shouldUseHttpsIfPortIs_8443() {
        factory.port = 8443
        assert factory.httpProtocol == "https"
    }

    @Test
    void shouldBuildUriFromDefaultParameters() {
        assert factory.uri == new URI("http://localhost:80/zuul/settings/dev/app-data-config.properties")
    }

    @Test
    void shouldBuildUriFromConfiguredParameters() {
        factory.port = 8080
        factory.context = ""
        factory.environment = "qa"
        factory.host = "config.devnull.org"
        assert factory.uri == new URI("http://config.devnull.org:8080/settings/qa/app-data-config.properties")
    }

    @Test
    void shouldUseProvidedHttpClient() {
        def mockHttpClient = mock(HttpClient)
        factory.httpClient = mockHttpClient
        factory.afterPropertiesSet()
        assert factory.httpClient.is(mockHttpClient)
    }

    @Test
    void shouldConstructHttpClientIfNotProvided() {
        factory.httpClient = null
        factory.afterPropertiesSet()
        assert factory.httpClient
    }

    @Test
    void shouldShutDownConnectionManagerOnDestroy() {
        def manager = mock(ClientConnectionManager)
        when(factory.httpClient.connectionManager).thenReturn(manager)
        factory.destroy()
        verify(manager).shutdown()
    }

    @Test
    void shouldNotBeSingletonBean() {
        assert !factory.singleton
    }

    @Test
    void shouldHaveCorrectBeanType() {
        assert factory.objectType == Properties
    }


    @Test
    void shouldStorePropertiesIfConfigured() {
        mockResponseFromFile()
        factory.propertiesStore = mock(PropertiesObjectStore)
        def properties = factory.fetchProperties()
        verify(factory.propertiesStore).put(factory.environment, factory.config, properties)
    }

    @Test
    void shouldRetrieveFileFromPropertyStoreIfServiceErrors() {
        mockServerErrorResponse()
        def expected = new Properties()
        factory.propertiesStore = mock(PropertiesObjectStore)
        when(factory.propertiesStore.get(factory.environment, factory.config)).thenReturn(expected)
        def results = factory.fetchProperties()
        verify(factory.propertiesStore).get(factory.environment, factory.config)
        assert results.is(expected)
    }

    @Test(expected=HttpResponseException)
    void shouldErrorIfServiceErrorsAndNoPropertyStoreConfigured() {
        mockServerErrorResponse()
        factory.fetchProperties()
    }


    protected void mockResponseFromFile() {
        def mockResponse = new ClassPathResource("/mock-server-response.properties").inputStream.text
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        when(factory.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenReturn(mockResponse)
    }

    protected void mockServerErrorResponse() {
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        def exception = new HttpResponseException(500, "test error")
        when(factory.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenThrow(exception)
    }

}
