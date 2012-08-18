<%--@elvariable id="environment" type="java.lang.String"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload Properties</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Upload Properties</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span4">
        <c:url var="actionUrl" value="/settings/${environment}/${groupName}.properties"/>
        <form id="uploadForm" action="${actionUrl}" method="POST" class="form-inline" enctype="multipart/form-data">
            <input id="file" name="file" type="file"/>

            <div class="form-actions">
                <c:url var="backUrl" value="/settings/${groupName}"/>
                <a href="${backUrl}" class="btn">
                    <i class="icon-arrow-left"></i>
                    Cancel
                </a>
                <button type="submit" class="btn btn-primary pull-right">
                    <i class="icon-upload icon-white"></i>
                    Upload
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
        var fileApiSupported = window.File && window.FileReader && window.FileList && window.Blob;
        $("#file").change(preview);
        function preview(evt) {
            if (fileApiSupported) {
                var files = evt.target.files;
                var reader = new FileReader();
                reader.onload = function (evt) {
                    $("#preview").text(evt.target.result);
                };
                reader.readAsText(files[0]);
            }
            else {
                $("#preview").text("HTML5 File API is not supported by your browser");
            }
        }
    });
</script>
</body>
</html>