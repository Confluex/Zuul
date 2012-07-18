<%--@elvariable id="user" type="org.devnull.security.model.User"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>New User Registration</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>New User Registration</h1>
        </div>
        I see that you are a new user. Please complete the rest of your profile to continue
        <form action="${pageContext.request.contextPath}/register" method="POST" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="email">Email</label>

                    <div class="controls">
                        <input id="email" name="email" class="span3" type="text" value="${user.email}">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="firstName">First Name</label>

                    <div class="controls">
                        <input id="firstName" name="firstName" class="span3" type="text" value="${user.firstName}">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="lastName">Last Name</label>

                    <div class="controls">
                        <input id="lastName" name="lastName" class="span3" type="text" value="${user.lastName}">
                    </div>
                </div>
                <div class="form-actions">
                    <button class="btn btn-primary" type="submit">Finish</button>
                </div>
            </fieldset>
        </form>
    </div>
</body>
</html>