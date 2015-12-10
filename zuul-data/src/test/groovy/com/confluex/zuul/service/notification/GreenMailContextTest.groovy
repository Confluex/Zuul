package com.confluex.zuul.service.notification

import com.icegreen.greenmail.util.GreenMail
import org.junit.Before
import org.junit.Test

import static org.mockito.Mockito.*

class GreenMailContextTest {

    GreenMailContext context

    @Before
    void createContext() {
        context = new GreenMailContext(greenMail: mock(GreenMail))
    }

    @Test
    void shouldDetectWhenGreenMailIsConfigured() {
        configureForTest()
        assert context.isGreenMailConfigured()
    }

    @Test
    void shouldDetectWhenGreenMailIsNotConfigured() {
        configureForProd()
        assert !context.isGreenMailConfigured()
    }

    @Test
    void shouldStartGreenmailWhenInTestEnvironment() {
        configureForTest()
        context.afterPropertiesSet()
        verify(context.greenMail).start()
    }

    @Test
    void shouldNotStartGreenmailWhenInOtherEnvironment() {
        configureForProd()
        context.afterPropertiesSet()
        verify(context.greenMail, never()).start()
    }

    @Test
    void shouldSopGreenmailWhenInTestEnvironment() {
        configureForTest()
        context.destroy()
        verify(context.greenMail).stop()
    }

    @Test
    void shouldNotStopGreenmailWhenInOtherEnvironment() {
        configureForProd()
        context.destroy()
        verify(context.greenMail, never()).stop()
    }

    @Test
    void shouldDoNothingIfNotConfigured() {
        context.afterPropertiesSet()
        context.destroy()
        verify(context.greenMail, never()).start()
        verify(context.greenMail, never()).stop()
    }


    protected void configureForTest() {
        context.host = "localhost"
        context.port = 3025
    }

    protected void configureForProd() {
        context.host = "mailserver"
        context.port = 25
    }

}
