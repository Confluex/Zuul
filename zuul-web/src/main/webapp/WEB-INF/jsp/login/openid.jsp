<%--suppress HtmlUnknownTarget --%>
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="z" tagdir="/WEB-INF/tags/zuul" %>

<html>
<head>
    <title>Login</title>
</head>

<body>
<jsp:include page="_errorCheck.jsp"/>
<div class="row">
    <div class="span6">
        <div class="page-header">
            <h1>Select a Login Provider</h1>
        </div>
        <ul class="thumbnails">
            <li class="span2">
                <z:openIdProvider url="https://www.google.com/accounts/o8/id" icon="google.png" name="Google"/>
            </li>
            <li class="span2">
                <z:openIdProvider url="https://me.yahoo.com/" icon="yahoo.png" name="Yahoo!"/>
            </li>
            <li class="span2">
                <z:openIdProvider url="https://myopenid.com/" icon="myopenid.png" name="My OpenID!"/>
            </li>
        </ul>
    </div>
    <div class="span-6">
        <div class="page-header">
            <h1>OpenID
                <small>(any other provider)</small>
            </h1>
        </div>
        <form class="form-horizontal" action="${pageContext.request.contextPath}/j_spring_openid_security_check"
              method="POST">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="customOpenIdUrl">URL</label>

                    <div class="controls">
                        <div class="input-append">
                            <input id="customOpenIdUrl" name="openid_identifier" class="span3" type="text">
                            <button class="btn btn-primary" type="submit">Login</button>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>

</body>
</html>
