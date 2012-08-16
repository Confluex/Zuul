<%--@elvariable id="users" type="java.util.List<org.devnull.security.model.User>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Users</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Users</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <table class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Roles</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr>
                    <td>${fn:escapeXml(user.lastName)}, ${fn:escapeXml(user.firstName)}</td>
                    <td>${fn:escapeXml(user.email)}</td>
                    <td>
                        <c:forEach var="role" items="${user.roles}">
                            <span class="label label-warning">${fn:escapeXml(role.description)} <a style="color:white;" href="#">&times;</a></span>
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>