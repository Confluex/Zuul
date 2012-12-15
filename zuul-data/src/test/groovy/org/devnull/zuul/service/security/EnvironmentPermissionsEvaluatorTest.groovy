package org.devnull.zuul.service.security

import org.devnull.zuul.data.model.Environment
import org.devnull.zuul.data.model.SettingsGroup
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication

import static org.devnull.zuul.data.config.ZuulDataConstants.*
import org.apache.commons.lang.NotImplementedException

class EnvironmentPermissionsEvaluatorTest {

    EnvironmentPermissionsEvaluator evaluator
    Environment restrictedEnv
    Environment nonRestrictedEnv

    @Before
    void createMocks() {
        def roleHierarchy = new RoleHierarchyImpl(hierarchy: "${ROLE_SYSTEM_ADMIN} > ${ROLE_ADMIN} > ${ROLE_USER} > ${ROLE_GUEST}")
        evaluator = new EnvironmentPermissionsEvaluator(roleHierarchy: roleHierarchy)
        restrictedEnv = new Environment(restricted: true)
        nonRestrictedEnv = new Environment(restricted: false)
    }

    @Test(expected=NotImplementedException)
    void shouldErrorOnUnrecognizedPermission() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        evaluator.hasPermission(authentication, nonRestrictedEnv, 'notapermission')
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
        assert evaluator.hasPermission(authentication, restrictedEnv, PERMISSION_ADMIN)
    }

    @Test
    void shouldAllowAdminPermIfNotRestrictedAndUserIsSysAdmin() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        assert evaluator.hasPermission(authentication, nonRestrictedEnv, PERMISSION_ADMIN)
    }

    @Test
    void shouldNotAllowAdminPermIfRestrictedAndUserIsAdmin() {
        def authentication = createAuthentication([ROLE_ADMIN])
        assert !evaluator.hasPermission(authentication, restrictedEnv, PERMISSION_ADMIN)
    }

    @Test
    void shouldAllowAdminPermIfNotRestrictedAndUserIsAdmin() {
        def authentication = createAuthentication([ROLE_ADMIN])
        assert evaluator.hasPermission(authentication, nonRestrictedEnv, PERMISSION_ADMIN)
    }

    @Test
    void shouldNotAllowAdminPermIfUserIsNotAnAdmin() {
        def authentication = createAuthentication([ROLE_USER])
        assert !evaluator.hasPermission(authentication, nonRestrictedEnv, PERMISSION_ADMIN)
        assert !evaluator.hasPermission(authentication, restrictedEnv, PERMISSION_ADMIN)
    }


    protected Authentication createAuthentication(List<String> roles) {
        return new TestingAuthenticationToken("test user", "********", roles as String[])
    }
}
