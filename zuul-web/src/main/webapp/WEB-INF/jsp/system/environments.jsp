<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Environments</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/js/system-environments.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Environments</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span9">
        <a href="#addEnvironmentDialog" data-toggle="modal" class="btn btn-primary">
            <i class="icon-plus icon-white"></i> New
        </a>

        <p style="margin-top: 1em;">
            <c:forEach var="env" items="${environments}">
                <span class="label label-warning">
                    ${fn:escapeXml(env.name)}
                    <a class="delete-env" data-env-id="${fn:escapeXml(env.name)}" href="#">&times;</a>
                </span>
            </c:forEach>
        </p>
    </div>
    <div class="span3">
        <div class="alert alert-warning" style="margin-top: 1em;">
            <h3>Caution</h3> Deleting an environment will also delete any associated settings.
        </div>
    </div>
</div>
<div class="modal hide" id="addEnvironmentDialog">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h3>Add Environment</h3>
    </div>
    <div class="modal-body" style="text-align: center;">
        TODO: FORM
    </div>
    <div class="modal-footer">
        <a href="#" class="btn" data-dismiss="modal">Cancel</a>
    </div>
</div>
</body>
</html>