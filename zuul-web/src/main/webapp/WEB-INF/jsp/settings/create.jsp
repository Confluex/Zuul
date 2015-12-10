<%--@elvariable id="environments" type="java.util.List<com.confluex.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create New</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Give your configuration a name..</h1>
        </div>
    </div>
</div>
<div class="row" style="min-height: 200px;">
    <div class="span12">
        <form action="#" id="newSettingsForm" method="GET" style="padding-left: 5em;">
            <div class="input-append">
                <input id="groupName" name="name" type="text" class="input-large"
                       autocomplete="off" placeholder="Configuration Name.." title="Configuration Name"
                       data-content="Provide a unique name for your configuration and we'll get started creating a new
                   settings group. In the next step, we'll define the environments and the content of the configuration.">
                <button type="submit" class="btn btn-primary">
                    <i class="icon-arrow-right icon-white"></i>
                    Next
                </button>
            </div>
        </form>
    </div>
</div>
<script>
    $(function () {
        $("#groupName").popover({placement:'bottom'}).popover('show');
        $("#newSettingsForm").submit(function () {
            var field = $("#groupName");
            var name = field.val();
            if (name && name.match(/^[a-zA-Z0-9\-_]+$/)) {
                document.location = "${pageContext.request.contextPath}/settings/" + encodeURI(name);
            }
            else {
                if ($("#newSettingsForm div.alert").length <= 0) {
                    var alert = $(document.createElement('div'));
                    alert.addClass('alert alert-error');
                    alert.html("<strong>Invalid Name</strong> <p>Value may only contain numbers, letters, underscores and dashes.</p>");
                    field.addClass("error").parents("form").prepend(alert);
                }
            }
            return false;
        })
    });
</script>
</body>
</html>