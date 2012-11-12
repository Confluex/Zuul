<%--@elvariable id="group" type="org.devnull.zuul.data.model.SettingsGroup"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>${fn:escapeXml(group.environment.name)}/${fn:escapeXml(group.name)}</title>
    <meta name="tab" content="settings"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/ext/slickgrid-2.0.2/slick.grid.css"
          type="text/css"/>
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/ext/slickgrid-2.0.2/theme/css/slick.theme.css"
          type="text/css"/>
    <script src="${pageContext.request.contextPath}/assets/ext/jquery.event.drag-2.0.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/ext/jquery-ui-1.9.1/js/jquery-ui-1.9.1.custom.min.js"></script>
    <script src="${pageContext.request.contextPath}/assets/ext/slickgrid-2.0.2/slick.core.js"></script>
    <script src="${pageContext.request.contextPath}/assets/ext/slickgrid-2.0.2/slick.grid.js"></script>

</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>${fn:escapeXml(group.environment.name)}/${fn:escapeXml(group.name)}</h1>
        </div>
        <div id="grid" style="width:600px;height:500px;"></div>
    </div>
</div>
<script>
    var grid;
    var columns = [
        {id:"key", name:"Key", field:"key", sortable:true, width:300},
        {id:"value", name:"Value", field:"value", sortable:true, width:300}
    ];

    var options = {
        enableCellNavigation:true,
        enableColumnReorder:false,
        enableAddRow:true,
    };

    var testData = [
        {id:1, key:'a', value:1},
        {id:2, key:'b', value:2}
    ];

    $(function () {
        $.ajax({
            url:getContextPath() + "/settings/" + encodeURI("${group.environment.name}") + "/" + encodeURI("${group.name}") + ".json",
            success:function (data) {
                grid = new Slick.Grid("#grid", data.entries, columns, options);
            }
        });
//        grid = new Slick.Grid("#grid", testData, columns, options);
    });
</script>
</body>
</html>