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
            <h1>${user.firstName} ${user.lastName}'s Profile</h1>
        </div>
        TODO: verbiage for greeting/instructions/next steps..
    </div>
</div>
</body>
</html>