package org.devnull.zuul.web.error

import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest
import org.springframework.web.servlet.HandlerExceptionResolver
import javax.servlet.http.HttpServletResponse
import org.devnull.zuul.service.error.ConflictingOperationException
import org.slf4j.LoggerFactory

class ErrorResolver implements HandlerExceptionResolver  {

    final def log = LoggerFactory.getLogger(this.class)

    ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        String view
        switch (ex) {
            case ConflictingOperationException:
                view = "/error/conflict"
                break;
            default:
                log.error("Unhandled exception", ex)
                view = "/error/default"
        }
        return new ModelAndView(view, [error:ex])
    }
}
