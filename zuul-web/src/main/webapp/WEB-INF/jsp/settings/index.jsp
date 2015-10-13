<%--@elvariable id="settings" type="java.util.List<com.confluex.zuul.data.model.Settings>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Settings Groups</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Settings Groups</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <ul>
            <c:forEach var="i" items="${settings}">
                <li>
                    <a href="${pageContext.request.contextPath}/settings/${fn:escapeXml(i.name)}">
                            ${fn:escapeXml(i.name)}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </div>
</div>
<script>
    $(function () {
        $("#navSearchInput").popover({
            title:'Tip: Search for Groups',
            content:'You can use this search input to easily navigate to settings groups by name.',
            placement:'bottom'
        }).popover('show').focus(
                function () {
                    $(this).popover('destroy');
                }
        )
    });
</script>
</body>
</html>