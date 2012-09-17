package org.devnull.zuul.web.error

import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.web.servlet.HandlerExceptionResolver

import static org.mockito.Mockito.*

class MediaTypeErrorResolverTest {
    MediaTypeErrorResolver resolver
    HandlerExceptionResolver jsonResolver
    HandlerExceptionResolver defaultResolver

    @Before
    void createResolver() {
        jsonResolver = mock(HandlerExceptionResolver)
        defaultResolver = mock(HandlerExceptionResolver)
        resolver = new MediaTypeErrorResolver(
                mappings: ["application/json": jsonResolver],
                defaultResolver: defaultResolver
        )
    }

    @Test
    void shouldUseUseResolverByMediaType() {
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        def ex = new RuntimeException("test")

        request.contentType = "application/json"
        resolver.resolveException(request, response, null, ex)
        verify(jsonResolver).resolveException(request, response, null, ex)
    }

    @Test
    void shouldUseDefaultResolverForUnsupportedMediaType() {
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        def ex = new RuntimeException("test")

        request.contentType = "text/html"
        resolver.resolveException(request, response, null, ex)
        verify(defaultResolver).resolveException(request, response, null, ex)
    }
}
