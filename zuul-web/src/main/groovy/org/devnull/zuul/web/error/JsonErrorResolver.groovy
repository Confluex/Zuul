package org.devnull.zuul.web.error

import groovy.json.JsonBuilder
import org.apache.commons.lang.exception.ExceptionUtils
import org.devnull.security.service.SecurityService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.validation.ConstraintViolationException

/**
 * Renders HttpErrorMessages as JSON to HTTP Clients
 */
class JsonErrorResolver implements HandlerExceptionResolver {

    final def log = LoggerFactory.getLogger(this.class)

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def error = new HttpErrorMessage(ExceptionUtils.getRootCause(ex) ?: ex, request)
        response.status = error.statusCode
        def writer = new OutputStreamWriter(response.outputStream)
        new JsonBuilder(error).writeTo(writer)
        writer.close()
        return new ModelAndView()
    }

}
