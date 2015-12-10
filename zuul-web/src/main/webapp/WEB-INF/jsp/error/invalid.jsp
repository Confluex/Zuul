<%--@elvariable id="errorMessage" type="com.confluex.zuul.web.error.HttpErrorMessage"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Invalid Data</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Invalid Data Submitted</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <div class="alert alert-error">
            <a href="#" class="close" data-dismiss="alert">&times;</a>

            <p>Oops, it looks like you've submitted invalid data. Here are all of the errors
                which you will need to go back and correct</p>

            <p>This is really ugly. I really should show you the error in the form instead of this page.
                I promise to do better next version! :-)</p>
        </div>
        <h3>Errors</h3>
        <ul>
            <c:forEach var="message" items="${errorMessage.messages}">
                <li><c:out value="${message}"/></li>
            </c:forEach>
        </ul>
    </div>
</div>
</body>
</html>