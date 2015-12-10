<%--@elvariable id="user" type="com.confluex.security.model.User"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>New User Registration</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span4">
        <div class="page-header">
            <h1>Hello!</h1>
        </div>
        <div class="alert alert-info">
            <p>
                I see that you are a new user. We need to collect just a bit more information in order
                to complete your account. Please complete the <em>Registration Form</em> and we'll finish
                up the rest.
            </p>

            <p>
                Once you are done, you will have basic access to the site. An administrator can grant you
                more privileges.
            </p>
        </div>
    </div>
    <div class="span8">
        <div class="page-header">
            <h1>Registration Form</h1>
        </div>
        <form id="user" action="${pageContext.request.contextPath}/account/register" method="POST" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="email">Email</label>

                    <div class="controls">
                        <input id="email" name="email" class="span3" type="email" value="${user.email}">
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

</div>
<bootstrap:validate formId="user" modelName="user" placement="top"/>
</body>
</html>