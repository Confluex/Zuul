<%--@elvariable id="principal" type="java.security.Principal"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>${user.firstName} ${user.lastName}</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <form class="form-horizontal" action="${pageContext.request.contextPath}/account/profile" method="POST">
            <div class="control-group">
                <label class="control-label" for="firstName">First Name</label>

                <div class="controls">
                    <input type="text" id="firstName" name="firstName" value="${fn:escapeXml(user.firstName)}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="lastName">Last Name</label>

                <div class="controls">
                    <input type="text" id="lastName" name="lastName" value="${fn:escapeXml(user.lastName)}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="email">Email</label>

                <div class="controls">
                    <input type="email" id="email" name="email" value="${fn:escapeXml(user.email)}">
                </div>
            </div>
            <div class="form-actions">
                <a class="btn" href="${pageContext.request.contextPath}/">Cancel</a>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>