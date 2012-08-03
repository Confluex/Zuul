package org.devnull.zuul.service.error

abstract class ZuulServiceException extends RuntimeException {
    ZuulServiceException(String message, Throwable cause = null) {
        super(message, cause)
    }

    abstract Integer getCode()
}
