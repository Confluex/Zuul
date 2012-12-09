<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<!DOCTYPE html>
<html>
<head>
    <title>Environments</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/js/system-environments.js"></script>
    <style>
        #environments span {
            margin-left: 8px;
        }
    </style>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Environments</h1>
        </div>

        <ul id="environments" class="unstyled">
            <c:forEach var="env" items="${environments}">
                <li class="stacked">
                    <div class="btn-group">
                        <a class="btn  dropdown-toggle ${env.restricted ? 'btn-danger' : 'btn-success'}" data-toggle="dropdown" href="#">
                            <i class="icon-move icon-white"></i>
                                ${fn:escapeXml(env.name)}
                            <span class="caret"></span>
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
</div>
<div class="row">
    <div class="span12">
        <form id="createEnvForm" method="POST" action="${pageContext.request.contextPath}/system/environments/create"
              class="stacked">
            <div class="input-append">
                <input name="name" type="text" maxlength="12" class="input-small"  placeholder="Name.."/>
                <button class="btn add-env" type="submit">New</button>
            </div>
        </form>
    </div>
</div>
<bootstrap:validate formId="createEnvForm" modelName="environment" placement="top"/>
</body>
</html>