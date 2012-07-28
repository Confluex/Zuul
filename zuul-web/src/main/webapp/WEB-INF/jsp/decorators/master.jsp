<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><decorator:title/> | Zuul</title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/ext/bootstrap-2.0.4/css/bootstrap.min.css"
          type="text/css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/ext/bootstrap-2.0.4/css/bootstrap-responsive.min.css"
          type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css" type="text/css">
    <script src="${pageContext.request.contextPath}/assets/ext/jquery-1.7.2.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/ext/bootstrap-2.0.4/js/bootstrap.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    <!--[if lt IE 9]>
    <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <decorator:head/>
</head>

<body>
<div class="container">
    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
            <div class="container">
                <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </a>
                <a class="brand" href="${pageContext.request.contextPath}/">Zuul</a>

                <div class="nav-collapse">
                    <ul class="nav">
                        <li class="active"><a href="${pageContext.request.contextPath}/">Home</a></li>
                        <li id="settingsMenu" class="dropdown">
                            <a class="dropdown-toggle" data-toggle="dropdown" href="#">Settings <b class="caret"></b></a>
                        </li>
                    </ul>
                    <security:authorize access="isAuthenticated()">
                        <ul class="nav pull-right">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                                    <security:authentication property="principal.firstName"/>
                                    <security:authentication property="principal.lastName"/>
                                    <b class="caret"></b>
                                </a>
                                <ul class="dropdown-menu">
                                    <li><a href="${pageContext.request.contextPath}/profile">My Profile</a></li>
                                    <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                                </ul>
                            </li>
                        </ul>
                    </security:authorize>
                    <security:authorize access="isAnonymous()">
                        <p class="navbar-text pull-right">
                            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Login</a>
                        </p>
                    </security:authorize>
                </div>
            </div>
        </div>
    </div>
    <decorator:body/>
    <hr>

    <footer>
        <em>Lorem tortor aenean vehicula sapien feugiat nibh taciti suscipit himenaeos lacus,
            congue potenti conubia justo eu diam vestibulum mattis mauris curae id, etiam lacinia maecenas
            tristique enim suscipit conubia molestie iaculis.</em>
    </footer>
</div>
<script>
    $(function() {
       $("#settingsMenu").settingsMenu({context:"${pageContext.request.contextPath}"});
    });
</script>
</body>
</html>
