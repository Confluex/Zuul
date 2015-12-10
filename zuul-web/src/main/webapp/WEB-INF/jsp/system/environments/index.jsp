<%--@elvariable id="environments" type="java.util.List<com.confluex.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<!DOCTYPE html>
<html>
<head>
    <title>Environments</title>
    <meta name="tab" content="admin"/>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css"/>
    <script src="http://code.jquery.com/ui/1.9.2/jquery-ui.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/system-environments.js"></script>
    <style>
        #environments span {
            margin-left: 8px;
        }

        #environments .btn-block {
            width: 150px;
            padding-left: 5px;
            text-align: left;
        }

        #environments .caret {
            margin-right: 5px;
        }
    </style>
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
    <div class="span3">
        <form id="createEnvForm" method="POST" action="${pageContext.request.contextPath}/system/environments/create">
            <div class="input-append">
                <input name="name" type="text" maxlength="12" class="input-small" placeholder="Name.."/>
                <button class="btn add-env" type="submit">New</button>
            </div>
        </form>
        <ul id="environments" class="unstyled sortable">
            <c:forEach var="env" items="${environments}">
                <li class="stacked environment" data-name="${fn:escapeXml(env.name)}">
                    <div class="btn-group">
                        <a class="btn btn-block dropdown-toggle ${env.restricted ? 'btn-danger' : 'btn-info'}"
                           data-toggle="dropdown" href="#">
                            <i class="icon-resize-vertical icon-white"></i>
                                ${fn:escapeXml(env.name)}
                            <span class="caret pull-right"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li>
                                <c:url var="toggleUrl" value="/system/environments/restrict/toggle">
                                    <c:param name="name" value="${env.name}"/>
                                </c:url>
                                <a href="${toggleUrl}" class="descriptive" title="Restrictions"
                                   data-content="Restricting an environment will require the sysadmin role in order to perform administrative actions.">
                                    <i class="icon-lock"></i>
                                        ${env.restricted ? 'Unrestrict' : 'Restrict'}
                                </a>
                            </li>
                            <li class="divider"></li>
                            <li>
                                <c:url var="deleteUrl" value="/system/environments/delete">
                                    <c:param name="name" value="${env.name}"/>
                                </c:url>
                                <a href="${deleteUrl}" class="delete-env descriptive" title="Caution"
                                   data-content="Deleting an environment will remove any associated settings">
                                    <i class="icon-trash"></i>
                                    Delete
                                </a>
                            </li>
                        </ul>
                    </div>

                </li>
            </c:forEach>
        </ul>
    </div>
    <div class="span6">
        <h4 class="media-heading">Scoping Your Settings</h4>
        <blockquote class="stacked">
            Environments are utilized to separate your settings into different groups. They appear in the
            settings pages as tabs on the left hand side.
        </blockquote>
        <h4>Actions</h4>
        <ul>
            <li>Create new environments</li>
            <li>Delete existing environments</li>
            <li>Restrict environments to system administrators</li>
            <li>Change the order in which the environments are presented</li>
        </ul>
    </div>
    <div class="span3">
        <img class="media-object img-polaroid descriptive" title="Example"
             data-content="The environments are presented as tabs on the settings page"
             src="${pageContext.request.contextPath}/assets/images/help/environments.png">
    </div>

</div>
<bootstrap:validate formId="createEnvForm" modelName="environment" placement="top"/>
</body>
</html>