package com.confluex.zuul.service.security

import org.junit.Test

import java.security.Provider
import java.security.Security

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class SecurityProvidersRegistrarTest {
    @Test
    void shouldRegistrarConfiguredProviders() {
        def provider = mock(Provider)
        when(provider.getName()).thenReturn("testProvider")
        new SecurityProvidersRegistrar(providers: [provider]).afterPropertiesSet()
        assert Security.providers.contains(provider)

    }
}
