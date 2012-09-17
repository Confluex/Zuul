package org.devnull.zuul.web.error

import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MediaTypeErrorResolver implements HandlerExceptionResolver {
    Map<String, HandlerExceptionResolver> mappings = [:]

    HandlerExceptionResolver defaultResolver = new ErrorResolver()
    @Override
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def resolver = mappings[request.contentType] ?: defaultResolver
        return resolver.resolveException(request, response, handler, ex)
    }
}
