<%--@elvariable id="audits" type="java.util.List<org.devnull.zuul.data.model.SettingsAudit>"--%>
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
                <tr>
                    <td>${audit.id}</td>
                    <td><fmt:formatDate pattern="MM/dd/yy HH:mm" value="${audit.modifiedDate}"/></td>
                    <td>${fn:escapeXml(audit.groupName)}</td>
                    <td>${fn:escapeXml(audit.settingsKey)}</td>
                    <td>${fn:escapeXml(audit.settingsValue)}</td>
                    <td>${fn:escapeXml(audit.modifiedBy)}</td>
                    <td>${audit.type}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>