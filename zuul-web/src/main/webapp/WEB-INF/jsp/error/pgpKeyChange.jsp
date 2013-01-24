<%--@elvariable id="groupName" type="String"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>PGP Key Change Error</title>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>PGP Key Change Error</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <div class="alert alert-error">
            <a href="#" class="close" data-dismiss="alert">&times;</a>
            <p>
                Unable to change key for settings which have been encrypted with PGP.Please delete or reset
                the encrypted values before attempting to change the key.
            </p>
            <c:url var="backUrl"  value="/settings/${fn:escapeXml(groupName)}#${fn:escapeXml(environment)}"/>
            <a href="${backUrl}" class="btn">Back</a>
        </div>
    </div>
</div>
</body>
</html>