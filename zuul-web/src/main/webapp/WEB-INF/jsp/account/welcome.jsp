<%--@elvariable id="principal" type="java.security.Principal"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Welcome</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Welcome</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span6">
        <div class="alert alert-info">
            <p>You have been assigned a user role in the system. This will allow you to view configuration files
                but you will not be able to edit, etc.</p>

            <p>The navigation bar at the top will get you going. If you require more access, please make a request
                and a system administrator will be notified.</p>
        </div>

    </div>
    <div class="span6">
        <a class="btn btn-primary btn-large"  style="margin-top: 40px; margin-left: 50px;"
           href="${pageContext.request.contextPath}/account/permissions">Request Permissions</a>
    </div>
</div>
</body>
</html>