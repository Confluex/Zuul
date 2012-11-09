<%--@elvariable id="audits" type="java.util.List<org.devnull.security.audit.AuditRevision<org.devnull.zuul.data.model.SettingsEntry>>"--%>
<%--@elvariable id="users" type="java.util.Map<org.devnull.security.model.User,java.lang.String>"--%>
<%--@elvariable id="filters" type="java.util.Map"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
                <span class="label label-warning">
                    Group Filter
                    <a href="${pageContext.request.contextPath}/audit/filter/remove/group">&times;</a>
                </span>
            </c:if>
            <c:if test="${filters.modifiedBy != null}">
                <span class="label label-warning">
                    Modified By Filter
                    <a href="${pageContext.request.contextPath}/audit/filter/remove/modifiedBy">&times;</a>
                </span>
            </c:if>
        </div>
        <table class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Rev</th>
                <th>Date</th>
                <th>Group</th>
                <th>Key</th>
                <th>Value</th>
                <th>Modified By</th>
                <th>Operation</th>
            </tr>
            </thead>
            <tbody>

            <c:forEach var="audit" items="${audits}">
                <c:set var="user" value="${users[audit.revision.modifiedBy]}"/>
                <tr>
                    <td>${audit.revision.id}</td>
                    <td><fmt:formatDate pattern="MM/dd/yy HH:mm" value="${audit.revision.modifiedDate}"/></td>
                    <td>
                        <c:url var="auditUrl" value="/audit/filter/add/group">
                            <c:param name="groupId" value="${audit.entity.group.id}"/>
                        </c:url>
                        <a href="${auditUrl}">
                                ${fn:escapeXml(audit.entity.group.name)} -
                                ${fn:escapeXml(audit.entity.group.environment.name)}
                        </a>
                    </td>
                    <td>${fn:escapeXml(audit.entity.key)}</td>
                    <td>${fn:escapeXml(audit.entity.value)}</td>
                    <td>
                        <c:url var="auditUrl" value="/audit/filter/add/modifiedBy">
                            <c:param name="modifiedBy" value="${audit.revision.modifiedBy}"/>
                        </c:url>
                        <a href="${auditUrl}"> ${fn:escapeXml(user.firstName)} ${fn:escapeXml(user.lastName)}</a>
                    </td>
                    <td>${audit.type}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>


</body>
</html>