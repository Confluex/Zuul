<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Request Permissions</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Request Permission</h1>
        </div>
        <div>
            <p>Some areas of the site require extra permissions to access. Please select the level of access which you
                require and site administrator will be notified.</p>

            <div class="btn-group">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    Access Level
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li>
                        <a href="${pageContext.request.contextPath}/account/permissions/ROLE_SYSTEM_ADMIN">System Administration</a>
                        <a href="${pageContext.request.contextPath}/account/permissions/ROLE_ADMIN">Edit Settings</a>
                        <a href="${pageContext.request.contextPath}/account/permissions/ROLE_USER">Read Only</a>
                    </li>
                </ul>
            </div>
        </div>

    </div>

</div>

</body>
</html>