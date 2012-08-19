<%--@elvariable id="keys" type="java.util.List<org.devnull.zuul.data.model.EncryptionKey>"--%>
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
            <h1>Encryption Keys</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <table id="userTable" class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Name</th>
                <th>Description</th>
                <th>Default</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="key" items="${keys}">
                <tr>
                    <td>${key.name}</td>
                    <td>${key.description}</td>
                    <td>
                        <button class="btn btn-mini pull-right ${key.defaultKey ? 'btn-primary' : ''}">
                            <i class="icon-check ${key.defaultKey ? 'icon-white' : '' }"></i>
                        </button>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

</html>