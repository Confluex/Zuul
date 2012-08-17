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
                <tr data-user-id="${user.id}">
                    <td>${fn:escapeXml(user.lastName)}, ${fn:escapeXml(user.firstName)}</td>
                    <td>${fn:escapeXml(user.email)}</td>
                    <td>
                        <c:forEach var="role" items="${user.roles}">
                            <span class="label label-warning">${fn:escapeXml(role.description)}
                                <a class="role" data-role-id="${role.id}" href="#">&times;</a>
                            </span>
                        </c:forEach>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<script>
    $(function () {
        $(".role").click(function () {
            var link = $(this);
            var row = link.parents("tr");
            var roleId = link.data("role-id");
            var userId = row.data("user-id");
            // note that something appears to be broken with .ajax delete with data. Hard coding
            // url parameters for now.
            $.ajax({
                url:getContextPath() + "/admin/system/user/role?roleId=" + roleId + "&userId=" + userId,
                type:'DELETE',
                success:function (data, status, xhr) {
                    var span = link.parents("span");
                    span.hide('slow', function() {
                       span.remove();
                    })
                },
                error:function (xhr, status, error) {
                    alert("An error has occurred while removing the role. Please check the log for more details.");
                }
            });
        })
    });
</script>
</body>
</html>