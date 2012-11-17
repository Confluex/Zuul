<%--@elvariable id="audits" type="java.util.List<org.devnull.zuul.data.model.SettingsAudit>"--%>
<%--@elvariable id="users" type="java.util.Map<java.lang.String, org.devnull.security.model.User>"--%>
<%--@elvariable id="user" type="org.devnull.security.model.User"--%>
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
</head>
<body>

<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Audit History</h1>
        </div>
        <div class="stacked">
            <c:if test="${filters.group != null}">
                <span class="label label-warning filter" title="Filter"
                      data-content="The results are filtered by settings group. Click the the close icon to remove the filter.">
                    Group Filter
                    <a href="${pageContext.request.contextPath}/audit/filter/remove/group">&times;</a>
                </span>
            </c:if>
            <c:if test="${filters.modifiedBy != null}">
                <span class="label label-warning filter" title="Filter"
                      data-content="The results are filtered by the user who modified the revision. Click the the close icon to remove the filter.">
                    Modified By Filter
                    <a href="${pageContext.request.contextPath}/audit/filter/remove/modifiedBy">&times;</a>
                </span>
            </c:if>
            <c:if test="${filters.key != null}">
                <span class="label label-warning filter" title="Filter"
                      data-content="The results are filtered by the user who modified the revision. Click the the close icon to remove the filter.">
                    Key Filter
                    <a href="${pageContext.request.contextPath}/audit/filter/remove/key">&times;</a>
                </span>
            </c:if>
        </div>
        <display:table name="audits" id="row" class="table table-bordered table-condensed table-striped"
                       requestURI="/audit">
            <display:column property="id" title="Rev" sortable="true" sortProperty="id"/>
            <display:column property="modifiedDate" title="Date" sortable="true"/>
            <display:column property="groupEnvironment" title="Environment" sortable="true"/>
            <display:column sortProperty="groupName" title="Group" sortable="true">
                <c:url var="groupUrl" value="/audit/filter/add/group">
                    <c:param name="value" value="${row.groupName}"/>
                </c:url>
                <a href="${groupUrl}">${fn:escapeXml(row.groupName)}</a>
            </display:column>
            <display:column sortProperty="settingsKey" title="Key" sortable="true">
                <c:url var="keyUrl" value="/audit/filter/add/key">
                    <c:param name="value" value="${row.settingsKey}"/>
                </c:url>
                <a href="${keyUrl}">${fn:escapeXml(row.settingsKey)}</a>
            </display:column>
            <display:column property="settingsValue" title="Value" sortable="true"/>
            <display:column sortProperty="modifiedBy" title="User" sortable="true" >
                <c:set var="user" value="${users[row.modifiedBy]}"/>
                <c:url var="modifiedByUrl" value="/audit/filter/add/modifiedBy">
                    <c:param name="value" value="${row.modifiedBy}"/>
                </c:url>
                <a href="${modifiedByUrl}" class="user" title="${fn:escapeXml(row.modifiedBy)}">
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