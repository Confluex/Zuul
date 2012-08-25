<%--@elvariable id="stackTrace" type="java.lang.String"--%>
<%--@elvariable id="error" type="java.lang.Exception"--%>
<%--@elvariable id="requestUri" type="java.lang.String"--%>
<%--@elvariable id="date" type="java.util.Date"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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

        <form class="form-horizontal">
            <fieldset>
                <legend>Error Details</legend>

                <div class="control-group">
                    <label class="control-label">Date</label>

                    <div class="controls">
                        <input type="text" value='<fmt:formatDate value="${date}" timeStyle="short"/>' readonly="readonly">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Request URI</label>

                    <div class="controls">
                        <input type="text" class="disabled" value=' ${fn:escapeXml(requestUri)}' readonly="readonly">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Message</label>

                    <div class="controls">
                        <input type="text" class="disabled" value=' ${fn:escapeXml(error.message)}' readonly="readonly">
                    </div>
                </div>
            </fieldset>
        </form>
        <strong>StackTrace:</strong>

        <p class="pre-scrollable well">${stackTrace}</p>
    </div>
</div>
</body>
</html>