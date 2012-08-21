<%--@elvariable id="keys" type="java.util.List<org.devnull.zuul.data.model.EncryptionKey>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Key Management</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/js/system-keys.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Key Management</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <table id="keysTable" class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Actions</th>
                <th>Name</th>
                <th>Description</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="key" items="${keys}">
                <tr data-key-name="${fn:escapeXml(key.name)}">
                    <td>
                        <c:choose>
                            <c:when test="${key.defaultKey}">
                                <c:set var="buttonClass" value="btn-primary"/>
                                <c:set var="iconClass" value="icon-check icon-white"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="buttonClass" value=""/>
                                <c:set var="iconClass" value="icon-ok"/>
                            </c:otherwise>
                        </c:choose>

                        <div class="btn-group">
                            <button class="btn ${buttonClass}">
                                <i class="${iconClass}"></i>
                                Edit
                            </button>
                            <button class="btn ${buttonClass} dropdown-toggle" data-toggle="dropdown">
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="#" class="default-key-action">Set Default</a></li>
                                <li class="divider"></li>
                                <li><a href="#">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                    <td>${fn:escapeXml(key.name)}</td>
                    <td>${fn:escapeXml(key.description)}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>