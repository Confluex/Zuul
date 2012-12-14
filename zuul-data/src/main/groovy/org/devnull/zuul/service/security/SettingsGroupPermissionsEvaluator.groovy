package org.devnull.zuul.service.security

import groovy.util.logging.Slf4j
import org.apache.commons.lang.NotImplementedException
import org.devnull.zuul.data.model.SettingsGroup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

import static org.devnull.zuul.data.config.ZuulDataConstants.*

/**
 * <p>
 * Supports permissions evaluations for classes of type org.devnull.zuul.data.model.SettingsGroup.
 * If the need arises to support more than just SettingsGroups, a DelegatingPermissionsEvaluator
 * should be created which would delegate to the proper implementation based on the correct type.
 * </p>
 *
 * <p>
 * Currently only supports permission {@link org.devnull.zuul.data.config.ZuulDataConstants#PERMISSION_ADMIN}.
 * </p>
 *
 * <strong>Restricted Groups:</strong>
 *  <ul>
 *   <li>{@link org.devnull.zuul.data.config.ZuulDataConstants#ROLE_SYSTEM_ADMIN} </li>
 *  </ul>
 *
 * <strong>Non-Restricted Groups:</strong>
 * <ul>
 *  <li>{@link org.devnull.zuul.data.config.ZuulDataConstants#ROLE_SYSTEM_ADMIN}</li>
 *  <li>{@link org.devnull.zuul.data.config.ZuulDataConstants#ROLE_ADMIN}</li>
 * </ul>
 */
@Slf4j
class SettingsGroupPermissionsEvaluator implements PermissionEvaluator {

    @Autowired
    RoleHierarchy roleHierarchy


    boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        log.debug("Checking for {} permission on domain object {} for user {}", permission, targetDomainObject, authentication)
        def roles = roleHierarchy.getReachableGrantedAuthorities(authentication.authorities)
        def group = targetDomainObject as SettingsGroup
        switch (permission) {
            case PERMISSION_ADMIN:
                return isAdminAndGroupIsNotRestricted(group, roles)
            default:
                throw new NotImplementedException("Permission ${permission} is not supported")
        }
    }

    boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new NotImplementedException("Permission checks for domain identifiers are not supported")
    }

    protected Boolean hasRole(Collection<GrantedAuthority> authorities, String name) {
        authorities.find { it.role == name } != null
    }


    @SuppressWarnings("GroovyAssignabilityCheck")
    protected Boolean isAdminAndGroupIsNotRestricted(SettingsGroup group, Collection<? extends GrantedAuthority> roles) {
        if (hasRole(roles, ROLE_SYSTEM_ADMIN)) {
            log.debug("Users is a sysadmin, no checks are enforced")
            return true
        }
        group.environment.restricted ? false : hasRole(roles, ROLE_ADMIN)
    }

}
