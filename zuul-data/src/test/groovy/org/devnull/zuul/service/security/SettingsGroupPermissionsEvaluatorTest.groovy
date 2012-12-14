package org.devnull.zuul.service.security

import org.devnull.security.model.Role
import org.devnull.security.model.User
import org.devnull.zuul.data.config.ZuulDataConstants
import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsGroup
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication

import static org.devnull.zuul.data.config.ZuulDataConstants.*
import org.apache.commons.lang.NotImplementedException

class SettingsGroupPermissionsEvaluatorTest {

    SettingsGroupPermissionsEvaluator evaluator
    SettingsGroup restrictedGroup
    SettingsGroup nonRestrictedGroup

    @Before
    void createMocks() {
        def roleHierarchy = new RoleHierarchyImpl(hierarchy: "${ROLE_SYSTEM_ADMIN} > ${ROLE_ADMIN} > ${ROLE_USER} > ${ROLE_GUEST}")
        evaluator = new SettingsGroupPermissionsEvaluator(roleHierarchy: roleHierarchy)
        restrictedGroup = new SettingsGroup(environment: new Environment(restricted: true))
        nonRestrictedGroup = new SettingsGroup(environment: new Environment(restricted: false))
    }

    @Test(expected=NotImplementedException)
    void shouldErrorOnUnrecognizedPermission() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        evaluator.hasPermission(authentication, nonRestrictedGroup, 'notapermission')
    }

    @Test(expected=NotImplementedException)
    void shouldErrorWhenCheckingDomainIdentifiers() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        evaluator.hasPermission(authentication, 123, 'DomainEntity', PERMISSION_ADMIN)
    }

    // --------- PERMISSION_ADMIN
    @Test
    void shouldAllowAdminPermIfRestrictedAndUserIsSysAdmin() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        assert evaluator.hasPermission(authentication, restrictedGroup, PERMISSION_ADMIN)
    }

    @Test
    void shouldAllowAdminPermIfNotRestrictedAndUserIsSysAdmin() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        assert evaluator.hasPermission(authentication, nonRestrictedGroup, PERMISSION_ADMIN)
    }

    @Test
    void shouldNotAllowAdminPermIfRestrictedAndUserIsAdmin() {
        def authentication = createAuthentication([ROLE_ADMIN])
        assert !evaluator.hasPermission(authentication, restrictedGroup, PERMISSION_ADMIN)
    }

    @Test
    void shouldAllowAdminPermIfNotRestrictedAndUserIsAdmin() {
        def authentication = createAuthentication([ROLE_ADMIN])
        assert evaluator.hasPermission(authentication, nonRestrictedGroup, PERMISSION_ADMIN)
    }

    @Test
    void shouldNotAllowAdminPermIfUserIsNotAnAdmin() {
        def authentication = createAuthentication([ROLE_USER])
        assert !evaluator.hasPermission(authentication, nonRestrictedGroup, PERMISSION_ADMIN)
        assert !evaluator.hasPermission(authentication, restrictedGroup, PERMISSION_ADMIN)
    }


    protected Authentication createAuthentication(List<String> roles) {
        return new TestingAuthenticationToken("test user", "********", roles as String[])
    }
}
