package com.confluex.zuul.service.error

import com.confluex.error.DevNullException

class InvalidOperationException extends DevNullException {
    InvalidOperationException(String message, Throwable ex) {
        super(message, ex)
    }

    InvalidOperationException(String message) {
        super(message)
    }
}
