<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Unhandled Error</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Yikes!
                <small>This is unexpected</small>
            </h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span3">
        An unhandled error has occurred. This sort of thing shouldn't happen. Please consider
        <a href="https://github.com/mcantrell/Zuul/issues">opening a bug report</a> with as much detail
        as possible and we'll take a closer look.
    </div>
    <div class="span9">
        <h3>Error Details</h3>

        <p class="well pre-scrollable">${error}</p>
    </div>
</div>
</body>
</html>