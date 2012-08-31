package org.devnull.client.spring

import org.springframework.beans.factory.xml.NamespaceHandlerSupport

class ZuulSpringClientNamespaceHandler extends NamespaceHandlerSupport  {
    void init() {
        registerBeanDefinitionParser("properties", new ZuulPropertiesBeanDefinitionParser());
    }
}
