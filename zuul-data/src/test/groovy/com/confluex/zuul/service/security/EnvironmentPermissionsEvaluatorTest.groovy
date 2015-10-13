package com.confluex.zuul.service.security

import org.apache.commons.lang.NotImplementedException
import com.confluex.zuul.data.dao.EnvironmentDao
import com.confluex.zuul.data.model.Environment
import org.junit.Before
import org.junit.Test
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.Authentication

import static com.confluex.zuul.data.config.ZuulDataConstants.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.verify

class EnvironmentPermissionsEvaluatorTest {

    EnvironmentPermissionsEvaluator evaluator
    Environment restrictedEnv
    Environment nonRestrictedEnv

    @Before
    void createMocks() {
        def roleHierarchy = new RoleHierarchyImpl(hierarchy: "${ROLE_SYSTEM_ADMIN} > ${ROLE_ADMIN} > ${ROLE_USER} > ${ROLE_GUEST}")
        evaluator = new EnvironmentPermissionsEvaluator(roleHierarchy: roleHierarchy, environmentDao: mock(EnvironmentDao))
        restrictedEnv = new Environment(restricted: true)
        nonRestrictedEnv = new Environment(restricted: false)
    }

    @Test(expected = NotImplementedException)
    void shouldErrorOnUnrecognizedPermission() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        evaluator.hasPermission(authentication, nonRestrictedEnv, 'notapermission')
    }

    @Test(expected = NotImplementedException)
    void shouldErrorWhenCheckingDomainIdentifiersForInvalidClasses() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        evaluator.hasPermission(authentication, 123, 'DomainEntity', PERMISSION_ADMIN)
    }

    @Test
    void shouldQueryForEnvironmentWhenIdentifierIsSupplied() {
        def authentication = createAuthentication([ROLE_SYSTEM_ADMIN])
        assert evaluator.hasPermission(authentication, "dev", Environment.class.name, PERMISSION_ADMIN)
        verify(evaluator.environmentDao).findOne("dev")
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
