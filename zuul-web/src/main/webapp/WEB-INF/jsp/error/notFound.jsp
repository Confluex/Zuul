<%--@elvariable id="errorMessage" type="com.confluex.zuul.web.error.HttpErrorMessage"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Not Found</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Not Found</h1>
        </div>
        <div class="alert alert-error">
            Sorry, I was not able to find the content you requested: ${errorMessage.messages}
        </div>
    </div>
</div>
</body>
</html>