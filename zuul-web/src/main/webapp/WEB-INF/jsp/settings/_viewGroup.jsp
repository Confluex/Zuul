<%--@elvariable id="group" type="com.confluex.zuul.data.model.SettingsGroup"--%>
<%--@elvariable id="environment" type="com.confluex.zuul.data.model.Environment"--%>
<%--@elvariable id="keys" type="java.util.List<com.confluex.zuul.data.model.EncryptionKey>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<security:authorize var="isAdmin" access="hasPermission(#group.environment, 'admin')"/>
<c:url var="downloadUrl"
       value="/settings/${fn:escapeXml(environment.name)}/${fn:escapeXml(group.name)}.properties"/>
<c:url var="addUrl"
       value="/settings/${fn:escapeXml(environment.name)}/${fn:escapeXml(group.name)}/create/entry"/>
<c:url var="auditUrl" value="/audit/filter/group/${fn:escapeXml(environment.name)}/${fn:escapeXml(group.name)}"/>

<div class="navbar">
    <div class="navbar-inner">
        <div class="container">
            <a class="btn btn-navbar" data-toggle="collapse" data-target=".group-actions-${group.id}">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <div class="group-actions-${group.id} nav-collapse collapse">
                <ul class="nav">
                    <li>
                        <a class="descriptive" href="${downloadUrl}" title="Download"
                           data-content="Use this URL in your application.">
                            <i class="icon-download-alt"></i>
                            ${fn:escapeXml(environment.name)}
                        </a>
                    </li>
                    <c:if test="${isAdmin}">
                        <li>
                            <a class="descriptive" href="${addUrl}" title="Add Entry"
                               data-content="Create a new key value pair">
                                <i class="icon-plus"></i>
                                Create Entry
                            </a>
                        </li>
                        <li>
                            <a class="descriptive" href="${auditUrl}" title="Audit Log"
                               data-content="View the changes performed to this group of settings.">
                                <i class="icon-eye-open"></i>
                                Audit Log
                            </a>
                        </li>
                        <security:authorize access="hasRole('ROLE_SYSTEM_ADMIN')">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle descriptive" data-toggle="dropdown"
                                   title="Change Encryption Key"
                                   data-content="Change the encryption key for this group. This will re-encrypt any existing entries with the new key.">
                                    <i class="icon-lock"></i>
                                        ${fn:escapeXml(group.key.name)}
                                    <b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu keys-dropdown-menu" data-group="${fn:escapeXml(group.name)}"
                                    data-environment="${fn:escapeXml(environment.name)}"
                                    data-current-key="${fn:escapeXml(group.key.name)}">
                                </ul>
                            </li>
                        </security:authorize>
                    </c:if>
                </ul>
                <c:if test="${isAdmin}">
                    <ul class="nav pull-right">
                        <li>
                            <a class="delete-group-link descriptive" href="#" title="Warning"
                               data-env="${fn:escapeXml(environment.name)}" data-group="${fn:escapeXml(group.name)}"
                               data-content="This will delete the ${environment.name} settings group. This operation cannot be undone.">
                                <i class="icon-trash"></i>
                                Delete
                            </a>
                        </li>
                    </ul>
                </c:if>
            </div>
        </div>

    </div>
</div>


<table class="table table-bordered table-condensed" style="margin-bottom: 10em; margin-top: 1em;">
    <thead>
    <tr>
        <th style="width: 15%; white-space: nowrap;">Actions</th>
        <th style="width: 30%">Key</th>
        <th style="width: 55%">Value</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="e" items="${group.entries}">
        <tr class="entry">
            <td>
                <c:if test="${isAdmin}">
                    <div class="btn-group">
                        <button class="btn edit-link" data-id="${e.id}">
                            <i class="icon-edit"></i>
                            Edit
                        </button>
                        <button class="btn dropdown-toggle" data-toggle="dropdown">
                            <span class="caret"></span>
                        </button>
                        <ul class="settings-entry dropdown-menu">
                            <li>
                                <a href="javascript:void(0);" class="encrypt-link" data-id="${e.id}"
                                   data-encrypted="${e.encrypted}">
                                    <i class="icon-lock"></i>
                                        ${e.encrypted ? 'Decrypt' : 'Encrypt'}
                                </a>
                            </li>
                            <li class="divider"></li>
                            <li>
                                <a href="#" class="delete-link" data-id="${e.id}">
                                    <i class="icon-trash"></i>
                                    Delete
                                </a>
                            </li>
                        </ul>
                    </div>
                </c:if>
                <c:if test="${!isAdmin}">
                    <a class="btn btn-small disabled">Disabled</a>
                </c:if>
            </td>
            <td class="key ellipsis">${fn:escapeXml(e.key)}</td>
            <td class="value ellipsis">${fn:escapeXml(e.value)}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>