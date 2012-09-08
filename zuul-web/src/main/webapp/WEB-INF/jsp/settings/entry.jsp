<%--@elvariable id="environment" type="java.lang.String"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="zfn" uri="/WEB-INF/tags/functions.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>New Key Value Pair</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>New Key Value Pair</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <c:url var="actionUrl" value="/settings/${environment}/${groupName}/create/entry"/>
        <form action="${actionUrl}" method="POST" class="form-inline">
            <div class="input-append">
                <input id="key" name="key" class="span3" type="text" placeholder="Key..">
                <span class="add-on">=</span>
                <input id="value" name="value" class="span3" type="text" placeholder="Value..">
                <button type="submit" class="btn btn-primary">Add</button>
            </div>
        </form>
    </div>
</div>

<spring:hasBindErrors name="formEntry">
    <script>
        $(function () {
            <c:set var="errorsByField" value="${zfn:errorsByField(errors)}"/>
            <c:forEach var="error" items="${errorsByField}">
                $("input[name=${error.key}]").addClass("error").popover({
                    title:'Validation Errors',
                    content: '${zfn:join(error.value)}',
                    placement: 'top',
                    trigger: 'hover'
                });
            </c:forEach>
        });
    </script>

</spring:hasBindErrors>
</body>
</html>