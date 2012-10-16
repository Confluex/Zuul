<%--@elvariable id="environment" type="java.lang.String"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
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
            <h1>
                New Key Value Pair
                <small>${fn:escapeXml(environment)}</small>
            </h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <c:url var="actionUrl" value="/settings/${environment}/${groupName}/create/entry"/>
        <form id="formEntry" action="${actionUrl}" method="POST" class="form-inline">
            <div class="input-append">
                <input value="${fn:escapeXml(formEntry.key)}" id="key" name="key" class="span3" type="text" placeholder="Key..">
                <span class="add-on">=</span>
                <input value="${fn:escapeXml(formEntry.value)}" id="value" name="value" class="span3" type="text" placeholder="Value..">
                <button type="submit" class="btn btn-primary">Add</button>
            </div>
        </form>
    </div>
</div>
<bootstrap:validate formId="formEntry" modelName="formEntry" placement="top"/>
</body>
</html>