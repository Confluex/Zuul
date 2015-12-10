<%--@elvariable id="audits" type="java.util.List<com.confluex.zuul.data.model.SettingsAudit>"--%>
<%--@elvariable id="users" type="java.util.Map<java.lang.String, com.confluex.security.model.User>"--%>
<%--@elvariable id="filters" type="java.util.Map"--%>
<%--@elvariable id="user" type="com.confluex.security.model.User"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Audit</title>
    <meta name="tab" content="admin"/>
    <style>
        td.ellipsis {
            max-width: 250px;
        }
    </style>
</head>
<body>

<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Audit History</h1>
        </div>
        <div class="stacked">
            <c:forEach var="filter" items="${filters}">
                <span class="label label-warning filter" title="Filter"
                      data-content="The results are filtered by this value. Click the the close icon to remove the filter.">
                    <c:url var="removeFilterUrl" value="/audit/filter/remove">
                        <c:param name="field" value="${filter.key}"/>
                    </c:url>
                    ${fn:escapeXml(filter.value)}
                    <a href="${removeFilterUrl}">&times;</a>
                </span>
            </c:forEach>
        </div>
        <display:table name="audits" id="row" class="table table-bordered table-condensed table-striped"
                       requestURI="/audit">
            <display:column property="id" title="Rev" sortable="true" sortProperty="id"/>
            <display:column property="modifiedDate" title="Date" sortable="true"/>
            <display:column sortProperty="groupEnvironment" title="Environment" sortable="true">
                <c:url var="filterUrl" value="/audit/filter/add">
                    <c:param name="field" value="groupEnvironment"/>
                    <c:param name="value" value="${row.groupEnvironment}"/>
                </c:url>
                <a href="${filterUrl}">${fn:escapeXml(row.groupEnvironment)}</a>
            </display:column>
            <display:column sortProperty="groupName" title="Group" sortable="true" class="ellipsis">
                <c:url var="filterUrl" value="/audit/filter/add">
                    <c:param name="field" value="groupName"/>
                    <c:param name="value" value="${row.groupName}"/>
                </c:url>
                <a href="${filterUrl}">${fn:escapeXml(row.groupName)}</a>
            </display:column>
            <display:column sortProperty="settingsKey" title="Key" sortable="true" class="ellipsis">
                <c:url var="filterUrl" value="/audit/filter/add">
                    <c:param name="field" value="settingsKey"/>
                    <c:param name="value" value="${row.settingsKey}"/>
                </c:url>
                <a href="${filterUrl}">${fn:escapeXml(row.settingsKey)}</a>
            </display:column>
            <display:column property="settingsValue" title="Value" sortable="true" escapeXml="true" class="ellipsis"/>
            <display:column sortProperty="modifiedBy" title="User" sortable="true" class="ellipsis">
                <c:set var="user" value="${users[row.modifiedBy]}"/>
                <c:url var="filterUrl" value="/audit/filter/add">
                    <c:param name="field" value="modifiedBy"/>
                    <c:param name="value" value="${row.modifiedBy}"/>
                </c:url>
                <a href="${filterUrl}" class="user" title="${fn:escapeXml(row.modifiedBy)}">
                        ${fn:escapeXml(user.firstName)} ${fn:escapeXml(user.lastName)}
                </a>
            </display:column>
            <display:column property="type" title="Operation" sortable="true"/>
        </display:table>
    </div>
</div>
<script>
    $(function () {
        $(".user").tooltip();
        $(".filter").popover({trigger:'hover'})
    });
</script>
</body>
</html>