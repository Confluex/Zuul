package com.confluex.zuul.web

import com.confluex.zuul.data.model.EncryptionKey
import com.confluex.zuul.service.ZuulService
import com.confluex.zuul.service.security.KeyConfiguration
import com.confluex.zuul.web.test.ControllerTestMixin
import org.junit.Before
import org.junit.Test
import org.springframework.web.servlet.mvc.support.RedirectAttributes

import static com.confluex.zuul.web.config.ZuulWebConstants.FLASH_ALERT_MESSAGE
import static com.confluex.zuul.web.config.ZuulWebConstants.FLASH_ALERT_TYPE
import static org.mockito.Mockito.*

@Mixin(ControllerTestMixin)
class KeyControllerTest {

    KeyController controller

    @Before
    void createController() {
        def keyMetaData = [
                'PBE-ABC': new KeyConfiguration(algorithm: "PBE-ABC", description: "ABC Test"),
                'PBE-DEF': new KeyConfiguration(algorithm: "PBE-DEF", description: "DEF Test"),
                'PBE-XYZ': new KeyConfiguration(algorithm: "PBE-XYZ", description: "XYZ Test")
        ]
        controller = new KeyController(
                zuulService: mock(ZuulService),
                keyMetaData: keyMetaData
        )
    }

    @Test
    void shouldListEncryptionKeysWithCorrectModelAndView() {
        def expected = [new EncryptionKey(name: "test")]
        when(controller.zuulService.listEncryptionKeys()).thenReturn(expected)
        def mv = controller.listKeys()
        assert mv.model.keys == expected
        assert mv.model.keyMetaData == controller.keyMetaData
        assert mv.viewName == "/system/keys/index"
    }

    @Test
    void shouldRenderCreateKeyForm() {
        def mv = controller.displayCreateKeyForm()
        assert mv.viewName == "/system/keys/createKey"
        assert mv.model.keyMetaData == controller.keyMetaData
    }

    @Test
    void shouldCreateNewKey() {
        def redirectAttributes = mock(RedirectAttributes)
        def key = new EncryptionKey(name: "test")
        def mv = controller.createKey(key, mockSuccessfulBindingResult(), redirectAttributes)
        assert mv.viewName == "redirect:/system/keys"
        verify(controller.zuulService).saveKey(key)
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_MESSAGE, "Key ${key.name} Created")
        verify(redirectAttributes).addFlashAttribute(FLASH_ALERT_TYPE, "success")
    }

    @Test
    void shouldDisplayErrorFormWhenCreatingInvalidKey() {
        def redirectAttributes = mock(RedirectAttributes)
        def key = new EncryptionKey(name: "test")
        def mv = controller.createKey(key, mockFailureBindingResult(), redirectAttributes)
        assert mv.viewName == "/system/keys/createKey"
        assert mv.model.keyMetaData == controller.keyMetaData
    }
    
    @Test
    void shouldRenderKeyMetaData() {
        assert controller.renderKeyMetaData() == controller.keyMetaData
    }

}
