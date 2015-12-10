package com.confluex.zuul.web.test

import org.springframework.validation.BindingResult

import static org.mockito.Mockito.*

class ControllerTestMixin {
    BindingResult mockSuccessfulBindingResult() {
        def result = mock(BindingResult)
        when(result.hasErrors()).thenReturn(false)
        return result
    }

    BindingResult mockFailureBindingResult() {
        def result = mock(BindingResult)
        when(result.hasErrors()).thenReturn(true)
        return result
    }
}
