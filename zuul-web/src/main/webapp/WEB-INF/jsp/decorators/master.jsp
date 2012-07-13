<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title><decorator:title/> | New Project</title>

    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/images/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/ext/bootstrap-2.0.4/css/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/ext/bootstrap-2.0.4/css/bootstrap-responsive.min.css" type="text/css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css" type="text/css">
    <script src="${pageContext.request.contextPath}/assets/ext/jquery-1.7.2.min.js"></script>
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
                <a class="brand" href="#">New Project</a>
                <div class="nav-collapse">
                    <ul class="nav">
                        <li class="active"><a href="#">Home</a></li>
                        <li><a href="#tab1">Tab 1</a></li>
                        <li><a href="#tab2">Tab 2</a></li>
                    </ul>
                    <p class="navbar-text pull-right">
                        <a href="#login"class="btn btn-primary">Login</a>
                    </p>
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
</body>
</html>
