package com.confluex.zuul.service.security

import org.springframework.beans.factory.InitializingBean

import java.security.Provider
import java.security.Security

class SecurityProvidersRegistrar implements InitializingBean {

    List<Provider> providers = []

    void afterPropertiesSet() throws Exception {
        providers.each {
            Security.addProvider(it)
        }
    }
}
