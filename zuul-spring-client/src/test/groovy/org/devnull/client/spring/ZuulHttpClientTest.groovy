package org.devnull.client.spring

import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.junit.Before
import org.junit.Test
import org.mockito.Matchers
import org.springframework.core.io.ClassPathResource

import static org.mockito.Mockito.*
import org.jasypt.exceptions.EncryptionOperationNotPossibleException

class ZuulHttpClientTest {

    ZuulHttpClient client

    @Before
    void createClient() {
        client = new ZuulHttpClient("app-data-config")
        client.httpClient = mock(HttpClient)
    }

    @Before
    void setEnvironmentPasswordProperty() {
        System.setProperty(ZuulHttpClient.DEFAULT_PASSWORD_VARIABLE, "badpassword1")
    }

    @Test
    void shouldFetchAndDecryptPropertiesFile() {
        def mockResponse = new ClassPathResource("/mock-server-response.properties").inputStream.text
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        when(client.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenReturn(mockResponse)
        def properties = client.fetchProperties()
        assert properties
        assert properties.getProperty("jdbc.zuul.password").startsWith("ENC(")
        def decrypted = client.decrypt(properties)
        assert decrypted.getProperty("jdbc.zuul.password") == "supersecure"
    }

    @Test(expected=EncryptionOperationNotPossibleException)
    void shouldFailToDecryptWithIncorrectPassword() {
        System.setProperty(ZuulHttpClient.DEFAULT_PASSWORD_VARIABLE, "foo")
        def mockResponse = new ClassPathResource("/mock-server-response.properties").inputStream.text
        def httpGet = Matchers.any(HttpGet)
        def handler = Matchers.any(BasicResponseHandler)
        when(client.httpClient.execute(httpGet as HttpGet, handler as BasicResponseHandler)).thenReturn(mockResponse)
        def decrypted = client.decrypt(client.fetchProperties())
        decrypted.getProperty("jdbc.zuul.password")
    }

    @Test
    void shouldUseHttpIfPortByDefault() {
        def ports = [80, 8080, 9642]
        assert client.httpProtocol == "http"
        ports.each {
            client.port = it
            assert client.httpProtocol == "http"
        }
    }

    @Test
    void shouldUseHttpsIfPortIs_443() {
        client.port = 443
        assert client.httpProtocol == "https"
    }

    @Test
    void shouldUseHttpsIfPortIs_8443() {
        client.port = 8443
        assert client.httpProtocol == "https"
    }

    @Test
    void shouldBuildUriFromDefaultParameters() {
        assert client.uri == new URI("http://localhost:80/zuul/settings/dev/app-data-config.properties")
    }

    @Test
    void shouldBuildUriFromConfiguredParameters() {
        client.port = 8080
        client.context = ""
        client.environment = "qa"
        client.host = "config.devnull.org"
        assert client.uri == new URI("http://config.devnull.org:8080/settings/qa/app-data-config.properties")
    }


}
