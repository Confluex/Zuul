<%--@elvariable id="users" type="java.util.Map<java.lang.String,org.devnull.security.model.User>"--%>
<%--@elvariable id="audits" type="java.util.List<org.devnull.zuul.data.model.SettingsAudit>"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zuul" uri="/WEB-INF/tags/zuul/zuul.tld" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <script src="${pageContext.request.contextPath}/assets/ext/date.format-1.0.js"></script>
    <script src="https://www.google.com/jsapi"></script>
    <script src="${pageContext.request.contextPath}/assets/js/news.js"></script>
</head>
<body>


<div class="row">
    <div class="span6">
        <div class="page-header">
            <h2>Activity</h2>
        </div>
        <c:choose>
            <c:when test="${fn:length(audits) > 0}">
                <c:forEach var="audit" items="${audits}">
                    <c:set var="user" value="${users[audit.modifiedBy]}"/>
                    <div class="media">
                        <a class="pull-left profile" href="#" title="${fn:escapeXml(audit.modifiedBy)}">
                            <zuul:gravatar user="${user}" size="32" cssClass="media-object"/>
                        </a>

                        <div class="media-body">
                            <h4 class="media-heading">
                                    ${fn:escapeXml(user.firstName)} ${fn:escapeXml(user.lastName)}
                                <small class="muted"> - <fmt:formatDate value="${audit.modifiedDate}"/></small>
                            </h4>
                            <c:url var="settingsUrl" value="/settings/${audit.groupName}#${audit.groupEnvironment}"/>
                                ${audit.type.action} key ${fn:escapeXml(audit.settingsKey)} on
                            <a href="${settingsUrl}">${fn:escapeXml(audit.groupEnvironment)}/${fn:escapeXml(audit.groupName)}</a>
                        </div>

                    </div>
                </c:forEach>

                <security:authorize access="hasRole('ROLE_ADMIN')">
                    <a class="btn stacked" href="${pageContext.request.contextPath}/audit?sort=modifiedDate&dir=desc">More..</a>
                </security:authorize>
            </c:when>
            <c:otherwise>
                <div class="alert alert-info">
                    No recent activity..
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="span6">
        <div class="page-header">
            <h2>News &amp; Announcements</h2>
        </div>
        <div id="newsFeed" data-url="http://news.devnull.org/feeds/posts/default/-/zuul" data-max-results="2">
            Loading..
        </div>
    </div>
</div>
<script>
    $(function() {
        $(".profile").tooltip();
        $("#newsFeed").newsFeed();
    });
</script>
</body>
</html>