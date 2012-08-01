<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%--@elvariable id="groupsByEnv" type="java.util.Map<String, org.devnull.zuul.data.model.SettingsGroup>"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>${fn:escapeXml(groupName)}</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>${fn:escapeXml(groupName)}</h1>
        </div>

        <c:forEach var="env" items="${groupsByEnv}">
            <h1>
                <c:url var="downloadUrl" value="/settings/${env.key}/${env.value[0].name}.properties"/>
                <a href="${downloadUrl}">${fn:escapeXml(env.key)}</a>
                <small>TODO: description</small>
            </h1>

            <table class="table table-bordered table-condensed">
                <thead>
                <tr>
                    <th style="width: 30%;">Key</th>
                    <th style="width: 60%;">Value</th>
                    <th style="width: 10%;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:set var="entries" value="${env.value[0].entries}"/>
                <c:forEach var="e" items="${entries}">
                    <tr>
                        <td>${e.key}</td>
                        <td>${e.value}</td>
                        <td>
                            <div class="btn-group">
                                <a class="btn btn-small btn-inverse dropdown-toggle" data-toggle="dropdown" href="#">
                                    Action
                                    <span class="caret"></span>
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a href="#">Encrypt</a></li>
                                    <li><a href="#">Edit</a></li>
                                    <li><a href="#">Delete</a></li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:forEach>
    </div>
</div>
</body>
</html>