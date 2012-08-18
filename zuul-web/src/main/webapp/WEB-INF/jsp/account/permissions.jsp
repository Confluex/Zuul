<%--@elvariable id="roles" type="java.util.List<org.devnull.security.model.Role>"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Request Permissions</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Request Permission</h1>
        </div>
        <div>
            <p>Some areas of the site require extra permissions to access. Please select the level of access which you
                require and site administrator will be notified.</p>

            <div class="btn-group">
                <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
                    Access Level
                    <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <c:forEach var="role" items="${roles}">
                        <li>
                            <c:url var="url" value="/account/permissions/${role.name}"/>
                            <a href="${url}">${fn:escapeXml(role.description)}</a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

    </div>

</div>

</body>
</html>