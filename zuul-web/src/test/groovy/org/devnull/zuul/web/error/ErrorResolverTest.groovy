package org.devnull.zuul.web.error

import org.devnull.zuul.service.error.ConflictingOperationException
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class ErrorResolverTest {

    ErrorResolver resolver
    MockHttpServletRequest request
    MockHttpServletResponse response

    @Before
    void createResolver() {
        resolver = new ErrorResolver()
    }

    @Before
    void createMocks() {
        request = new MockHttpServletRequest()
        response = new MockHttpServletResponse()
    }

    @Test
    void shouldHaveCorrectViewForConflictExceptions() {
        def ex = new ConflictingOperationException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/conflict"
        assert mv.model.error == ex
    }

    @Test
    void shouldHaveCorrectViewForUnhandledExceptions() {
        def ex = new RuntimeException("test")
        def mv = resolver.resolveException(request, response, null, ex)
        assert mv.viewName == "/error/default"
        assert mv.model.error == ex
    }
}
