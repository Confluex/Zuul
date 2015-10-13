<%--@elvariable id="users" type="java.util.List<com.confluex.security.model.User>"--%>
<%--@elvariable id="roles" type="java.util.List<com.confluex.security.model.Role>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Users</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/js/system-users.js"></script>
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
        <table id="userTable" class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Roles</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="user" items="${users}">
                <tr data-user-id="${user.id}">
                    <td>
                            ${fn:escapeXml(user.lastName)}, ${fn:escapeXml(user.firstName)}
                        <a href="#" title="Delete User"
                           class="btn btn-mini btn-danger pull-right descriptive discoverable delete-user-action">
                            <i class="icon-trash icon-white"></i>
                        </a>
                    </td>
                    <td>${fn:escapeXml(user.email)}</td>
                    <td class="role-column">
                        <c:forEach var="role" items="${user.roles}">
                            <span class="label label-warning">
                                ${fn:escapeXml(role.description)}
                                <a class="delete-role" data-role-id="${role.id}" href="#">&times;</a>
                            </span>
                        </c:forEach>
                        <a href="#" class="btn btn-mini pull-right descriptive discoverable add-role-action"
                           title="Add Role">
                            <i class="icon-plus"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<div class="modal hide" id="addRoleDialog">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h3>Add Role</h3>
    </div>
    <div class="modal-body" style="text-align: center;">
        <c:forEach var="role" items="${roles}">
            <%-- TODO only display roles that the user does not have --%>
            <span class="label label-warning">
                <a class="add-role" data-role-id="${role.id}" data-user-id="${user.id}" href="#">
                        ${fn:escapeXml(role.description)}
                </a>
            </span>
        </c:forEach>
    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Cancel</a>
    </div>
</div>
</body>
</html>