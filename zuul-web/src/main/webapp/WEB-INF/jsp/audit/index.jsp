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
                            <c:param name="value" value="${audit.entity.group.id}"/>
                        </c:url>
                        <a href="${auditUrl}">
                                ${fn:escapeXml(audit.entity.group.name)} -
                                ${fn:escapeXml(audit.entity.group.environment.name)}
                        </a>
                    </td>
                    <td>
                        <c:url var="auditUrl" value="/audit/filter/add/key">
                            <c:param name="value" value="${audit.entity.key}"/>
                        </c:url>
                        <a href="${auditUrl}">${fn:escapeXml(audit.entity.key)}</a>
                    </td>
                    <td>${fn:escapeXml(audit.entity.value)}</td>
                    <td>
                        <c:url var="auditUrl" value="/audit/filter/add/modifiedBy">
                            <c:param name="value" value="${audit.revision.modifiedBy}"/>
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
<script>
    $(function() {
        $(".filter").popover({trigger:'hover'});
    });
</script>

</body>
</html>