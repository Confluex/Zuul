<%--@elvariable id="errorMessage" type="com.confluex.zuul.web.error.HttpErrorMessage"--%>
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
                <small>Someone crossed the streams..</small>
            </h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <div class="alert alert-error">
            <a href="#" class="close" data-dismiss="alert">&times;</a>
            An unhandled error has occurred. This sort of thing shouldn't happen. Please consider
            <a href="https://github.com/mcantrell/Zuul/issues">opening a bug report</a> with as much detail
            as possible and we'll take a closer look.
        </div>
        <form class="form-horizontal">
            <fieldset>
                <legend>Error Details</legend>

                <div class="control-group">
                    <label class="control-label">Date</label>

                    <div class="controls">
                        <input type="text" value='<fmt:formatDate value="${errorMessage.date}" pattern="MM/dd/yy HH:mm:ss"/>' readonly="readonly">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Request URI</label>

                    <div class="controls">
                        <input type="text" class="disabled" value=' ${fn:escapeXml(errorMessage.requestUri)}' readonly="readonly">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Message</label>

                    <div class="controls">
                        <input type="text" class="disabled" value=' ${fn:escapeXml(errorMessage.messages)}' readonly="readonly">
                    </div>
                </div>
            </fieldset>
        </form>
        <strong>StackTrace:</strong>

        <pre class="pre-scrollable well">${errorMessage.stackTrace}</pre>
    </div>
</div>
</body>
</html>