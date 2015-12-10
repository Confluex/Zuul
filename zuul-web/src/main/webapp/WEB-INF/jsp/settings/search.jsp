<%--@elvariable id="results" type="java.util.Map<com.confluex.zuul.data.model.SettingsGroup, java.util.List<com.confluex.zuul.data.model.SettingsEntry>>"--%>
<%--@elvariable id="query" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Search Results</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Search Results
                <small>${fn:escapeXml(query)}</small>
            </h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <c:forEach var="i" items="${results}">
            <c:url var="viewUrl" value="/settings/${i.key.name}#${i.key.environment.name}"/>
            <h3><a href="${viewUrl}">${i.key.environment.name}/${i.key.name}</a></h3>
            <div class="well">
                <c:forEach var="entry" items="${i.value}">
                    ${fn:escapeXml(entry.key)}=${fn:escapeXml(entry.value)}<br/>
                </c:forEach>
            </div>

        </c:forEach>
    </div>
</div>
</body>
</html>