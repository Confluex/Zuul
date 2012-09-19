package org.devnull.zuul.web.error

import org.apache.commons.lang.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerExceptionResolver
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static javax.servlet.http.HttpServletResponse.*

/**
 * Renders HttpErrorMessages as HTML to HTTP clients
 */
class DefaultErrorResolver implements HandlerExceptionResolver {


    final def log = LoggerFactory.getLogger(this.class)
    Map<Integer, String> statusToViewMappings = [
            (SC_FORBIDDEN): "/error/denied",
            (SC_CONFLICT): "/error/conflict",
            (SC_NOT_ACCEPTABLE): "/error/invalid",
            (SC_NOT_FOUND): "/error/notFound",
            (SC_FORBIDDEN): "/error/denied",

    ]
    String defaultView = "/error/default"

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        def root = ExceptionUtils.getRootCause(ex) ?: ex
        def errorMessage = new HttpErrorMessage(root, request)
        response.status = errorMessage.statusCode
        def view = statusToViewMappings[errorMessage.statusCode] ?: defaultView
        return new ModelAndView(view, [errorMessage: errorMessage])
    }

}
