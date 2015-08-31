package org.devnull.zuul.web

import org.junit.Before
import org.junit.Test;

public class OpenIdLoginControllerTest {
    OpenIdLoginController controller

    @Before
    void createController() {
        controller = new OpenIdLoginController()
    }
    
    @Test
    void shouldHaveLoginPage() {
        def modelView = controller.login()
        assert modelView.viewName == "/login/openid"
        verifyProviderMap(modelView.model.providers)
    }

    @Test
    void shouldLoadOpenIdProviders(){
        def providers = controller.getProviders()
        verifyProviderMap(providers)
    }

    private void verifyProviderMap(providers) {
        assert providers != null
        assert providers.size() == 4
        assert providers[0].name == "Google"
        assert providers[0].openIdUrl == "https://www.google.com/accounts/o8/id"
        assert providers[0].iconLocation == "google.png"
        assert providers[1].name == "Yahoo!"
        assert providers[1].openIdUrl == "https://me.yahoo.com/"
        assert providers[1].iconLocation == "yahoo.png"
        assert providers[2].name == "Verisign"
        assert providers[2].openIdUrl == "https://pip.verisignlabs.com/"
        assert providers[2].iconLocation == "verisign.png"
        assert providers[3].name == "Aol"
        assert providers[3].openIdUrl == "https://openid.aol.com"
        assert providers[3].iconLocation == "aol.png"
    }
}
