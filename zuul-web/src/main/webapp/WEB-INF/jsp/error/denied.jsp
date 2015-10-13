<%--@elvariable id="errorMessage" type="com.confluex.zuul.web.error.HttpErrorMessage"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Access Denied</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Access Denied</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span-6">
        <div class="alert alert-error">
            You do not appear to have the appropriate level of access to this resource.
        </div>
    </div>
    <div class="span-6">
        <a class="btn btn-primary btn-large" href="${pageContext.request.contextPath}/account/permissions">Request
            Permissions</a>
    </div>
</div>
</body>
</html>