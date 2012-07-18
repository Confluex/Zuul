<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%--@elvariable id="SPRING_SECURITY_LAST_EXCEPTION" type="org.springframework.security.core.AuthenticationException"--%>

<html>
<head>
    <title>Login</title>
</head>

<body>


<c:if test="${not empty param.login_error}">
    <div class="row">
        <div class="alert alert-error">
            Your login attempt was not successful, try again.
            <ul>
                <li>
                    <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/></li>
            </ul>
        </div>
    </div>
</c:if>

<div class="row">
    <div class="span6">
        <div class="page-header">
            <h1>Select a Login Provider</h1>
        </div>
        <ul class="thumbnails">
            <li class="span2">
                <div class="thumbnail">
                    <c:url var="openIdUrl" value="j_spring_openid_security_check">
                        <c:param name="openid_identifier" value="https://www.google.com/accounts/o8/id"/>
                    </c:url>
                    <a href="${openIdUrl}">
                        <img src="${pageContext.request.contextPath}/assets/images/logins/google.png" alt="Google">
                    </a>
                </div>
            </li>
            <li class="span2">
                <div class="thumbnail">
                    <c:url var="openIdUrl" value="j_spring_openid_security_check">
                        <c:param name="openid_identifier" value="https://me.yahoo.com/"/>
                    </c:url>
                    <a href="${openIdUrl}">
                        <img src="${pageContext.request.contextPath}/assets/images/logins/yahoo.png" alt="Yahoo">
                    </a>
                </div>
            </li>
            <li class="span2">
                <div class="thumbnail">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/logins/facebook.png" alt="Facebook">
                    </a>
                </div>
            </li>
            <li class="span2">
                <div class="thumbnail">
                    <a href="#">
                        <img src="${pageContext.request.contextPath}/assets/images/logins/twitter.png" alt="Twitter">
                    </a>
                </div>
            </li>
            <li class="span2">
                <div class="thumbnail">
                    <a href="#"><img src="${pageContext.request.contextPath}/assets/images/logins/live.png" alt="Live"></a>
                </div>
            </li>
        </ul>
    </div>
    <div class="span-6">
        <div class="page-header">
            <h1>OpenID
                <small>(any other provider)</small>
            </h1>
        </div>

        <form class="form-horizontal" action="<c:url value='j_spring_openid_security_check'/>" method="POST">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="customOpenIdUrl">URL</label>

                    <div class="controls">
                        <div class="input-append">
                            <input id="customOpenIdUrl" name="openid_identifier" class="span3" type="text">
                            <button class="btn" type="button">Login</button>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>

</body>
</html>
