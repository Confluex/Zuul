package org.devnull.zuul.service.error

import org.devnull.error.DevNullException

class InvalidOperationException extends DevNullException {
    InvalidOperationException(String message, Throwable ex) {
        super(message, ex)
    }

    InvalidOperationException(String message) {
        super(message)
    }
}
