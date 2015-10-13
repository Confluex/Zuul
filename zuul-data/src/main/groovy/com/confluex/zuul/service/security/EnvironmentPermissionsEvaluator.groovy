package com.confluex.zuul.service.security

import groovy.util.logging.Slf4j
import org.apache.commons.lang.NotImplementedException
import com.confluex.zuul.data.dao.EnvironmentDao
import com.confluex.zuul.data.model.Environment
import com.confluex.zuul.data.model.SettingsGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import static com.confluex.zuul.data.config.ZuulDataConstants.*

/**
 * <p>
 * Supports permissions evaluations for objects of type com.confluex.zuul.data.model.Environment.
 * If the need arises to support more than just Environments, a DelegatingPermissionsEvaluator
 * should be created which would delegate to the proper implementation based on the correct type.
 * </p>
 *
 * <p>
 * Currently only supports permission {@link com.confluex.zuul.data.config.ZuulDataConstants#PERMISSION_ADMIN}.
 * </p>
 *
 * <strong>Restricted Environments:</strong>
 *  <ul>
 *   <li>{@link com.confluex.zuul.data.config.ZuulDataConstants#ROLE_SYSTEM_ADMIN} </li>
 *  </ul>
 *
 * <strong>Non-Restricted Environments:</strong>
 * <ul>
 *  <li>{@link com.confluex.zuul.data.config.ZuulDataConstants#ROLE_SYSTEM_ADMIN}</li>
 *  <li>{@link com.confluex.zuul.data.config.ZuulDataConstants#ROLE_ADMIN}</li>
 * </ul>
 */
@Slf4j
class EnvironmentPermissionsEvaluator implements PermissionEvaluator {


    RoleHierarchy roleHierarchy


    EnvironmentDao environmentDao


    boolean hasPermission(Authentication authentication, Object entity, Object permission) {
        log.debug("Checking for {} permission on domain object {} for user {}", permission, entity, authentication)
        def roles = roleHierarchy.getReachableGrantedAuthorities(authentication.authorities)
        def env = entity as Environment
        switch (permission) {
            case PERMISSION_ADMIN:
                return isAdminAndEnvironmentIsNotRestricted(env, roles)
            default:
                throw new NotImplementedException("Permission ${permission} is not supported")
        }
    }

    boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (targetType != Environment.class.name) {
            throw new NotImplementedException("Unsupported type ${targetType}. Supported type: ${Environment.class.name}")
        }
        return hasPermission(authentication, environmentDao.findOne(targetId.toString()), permission)
    }

    protected Boolean hasRole(Collection<GrantedAuthority> authorities, String name) {
        authorities.find { it.role == name } != null
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    protected Boolean isAdminAndEnvironmentIsNotRestricted(Environment environment, Collection<? extends GrantedAuthority> roles) {
        if (hasRole(roles, ROLE_SYSTEM_ADMIN)) {
            log.debug("Users is a sysadmin, no checks are enforced")
            return true
        }
        environment.restricted ? false : hasRole(roles, ROLE_ADMIN)
    }

}
