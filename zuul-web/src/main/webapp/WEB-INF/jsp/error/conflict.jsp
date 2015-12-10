<%--@elvariable id="errorMessage" type="com.confluex.zuul.web.error.HttpErrorMessage"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Unhandled Error</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Conflict
                <small>You cannot do that</small>
            </h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <div class="alert alert-error">
            You performed a conflicting operation: <br/>
            <br/>
            <strong>${errorMessage.messages}</strong>
        </div>
    </div>

</div>
</body>
</html>