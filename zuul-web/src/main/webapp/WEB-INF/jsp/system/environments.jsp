<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Environments</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/js/system-environments.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="alert alert-warning" style="margin-top: 1em;">
            <strong>Caution</strong> Deleting an environment will also delete any associated settings.
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Environments</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span3">
        <form id="createEnvForm" method="POST" action="${pageContext.request.contextPath}/system/environments">
            <div class="input-append">
                <input id="environmentName" name="name" type="text" maxlength="12" class="input-small" placeholder="Name.."/>
                <button class="btn add-env" type="button">New</button>
            </div>
        </form>
    </div>
    <div id="environments" class="span9">
        <c:forEach var="env" items="${environments}">
                <span class="label label-warning">
                    ${fn:escapeXml(env.name)}
                    <a class="delete-env" data-env-id="${fn:escapeXml(env.name)}" href="#">&times;</a>
                </span>
        </c:forEach>
    </div>
</div>
</body>
</html>