<%--@elvariable id="principal" type="java.security.Principal"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Profile</title>
</head>
<body>
<div class="row">
    <h1><sec:authentication property="principal.username" /> </h1>
    <p>Yada yada yada</p>
</div>
</body>
</html>