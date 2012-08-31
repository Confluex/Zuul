package org.devnull.client.spring

import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser
import org.w3c.dom.Element

class ZuulPropertiesBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return ZuulHttpClient
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        def url = element.getAttribute("url")
        bean.addConstructorArg(url)
        def password = element.getAttribute("password")
        if (password) {
            bean.addPropertyValue("password", password)
        }
    }
}
