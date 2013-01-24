package org.devnull.zuul.data.test

class DataUnitTestMixin {
    def stopWatch  = { closure ->
        def start = System.currentTimeMillis()
        closure()
        return System.currentTimeMillis() - start
    }
}
