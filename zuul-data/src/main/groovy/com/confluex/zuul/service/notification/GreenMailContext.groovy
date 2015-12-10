package com.confluex.zuul.service.notification

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetupTest
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory

/**
 * Conditionally starts/stops an embedded SMTP server for unit and functional testing based
 * upon application config.<br/><br/>
 *
 * To enable, configure smtp.port=3025 and smtp.host=localhost
 */
@Component("greenMailContext")
class GreenMailContext implements DisposableBean, InitializingBean {

    final def log = LoggerFactory.getLogger(this.class)

    @Value("\${smtp.port}")
    Integer port

    @Value("\${smtp.host}")
    String host

    GreenMail greenMail = new GreenMail()

    void destroy() {
        if (isGreenMailConfigured()) {
            log.info("Stopping embedded test SMTP server")
            greenMail.stop()
        }
    }

    void afterPropertiesSet() {
        if (isGreenMailConfigured()) {
            log.info("Started embedded test SMTP server")
            greenMail.start()
        }
    }

    Boolean isGreenMailConfigured() {
        return port == ServerSetupTest.SMTP.port && host == "localhost"
    }
}
