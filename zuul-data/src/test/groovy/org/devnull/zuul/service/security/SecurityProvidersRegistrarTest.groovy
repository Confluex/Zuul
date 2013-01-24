package org.devnull.zuul.service.security

import org.junit.Test

import java.security.Provider
import java.security.Security

import static org.mockito.Mockito.*

class SecurityProvidersRegistrarTest {
    @Test
    void shouldRegistrarConfiguredProviders() {
        def provider = mock(Provider)
        when(provider.getName()).thenReturn("testProvider")
        new SecurityProvidersRegistrar(providers: [provider]).afterPropertiesSet()
        assert Security.providers.contains(provider)

    }
}
