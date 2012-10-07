package org.devnull.zuul.web.error

import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.http.MediaType

/**
 * Delegates to the ErrorResolver configured for a given media type.
 */
class MediaTypeErrorResolver implements HandlerExceptionResolver {
    Map<String, HandlerExceptionResolver> mappings = [:]

    HandlerExceptionResolver defaultResolver = new DefaultErrorResolver()
    @Override
    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def mediaType = request.contentType ? MediaType.parseMediaType(request.contentType) : null
        def resolver = mappings[mediaType?.subtype] ?: defaultResolver
        return resolver.resolveException(request, response, handler, ex)
    }
}
