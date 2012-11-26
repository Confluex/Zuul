<%--@elvariable id="environment" type="java.lang.String"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Copy Existing</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Copy Existing</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span4">
        <c:url var="actionUrl" value="/settings/create/copy"/>
        <form id="copyForm" action="${actionUrl}" method="POST" class="form-inline" enctype="multipart/form-data">
            <input id="searchFiles" name="search" type="text" placeholder="Search.." autocomplete="off"/>
            <input type="hidden" name="name" value="${fn:escapeXml(groupName)}"/>
            <input type="hidden" name="environment" value="${fn:escapeXml(environment)}"/>
            <div class="form-actions">
                <c:url var="backUrl" value="/settings/${groupName}"/>
                <a href="${backUrl}" class="btn">
                    <i class="icon-arrow-left"></i>
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary pull-right">
                    <i class="icon-share-alt icon-white"></i>
                    Copy
                </button>
            </div>
        </form>
    </div>
    <div class="span8">
        <h2>Preview</h2>
        <pre id="preview"></pre>
    </div>
</div>
<script>
    $(function () {
        $.ajax({
            url:getContextPath() + "/settings.json",
            success:function (data) {
                var files = [];
                for (var i = 0; i < data.length; i++) {
                    var group = data[i];
                    files.push("/" + group.environment + "/" + group.name + ".properties");
                }
                $("#searchFiles").typeahead({source:files});
            },
            error:function (xhr, status, error) {
                showAlert("Error fetching existing settings for typeahead dialog: " + error);
            }
        });
        $('#searchFiles').change(function () {
            var path = $(this).val();
            var fields = path.split("/");
            if (fields.length == 3) {
                $.ajax({
                    url:getContextPath() + "/settings" + path,
                    type:'GET',
                    success:function (data) {
                        $("#preview").text(data);
                    },
                    error:function (xhr, status, error) {
                        $("#preview").text("Error previewing file: " + error);
                    }
                });
            }
        });
    });


</script>
</body>
</html>