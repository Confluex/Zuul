<%--@elvariable id="settings" type="com.confluex.zuul.data.model.Settings"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit ${fn:escapeXml(settings.name)}</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12 page-header">
        <h1>${fn:escapeXml(settings.name)}
            <small>edit</small>
        </h1>
    </div>
</div>
<div class="row">
    <div class="span5">
        <form action="${pageContext.request.contextPath}/settings/${fn:escapeXml(settings.name)}/edit"
              id="editSettingsForm" method="POST" class="form-horizontal">
            <input type="hidden" name="id" value="${settings.id}"/>

            <div class="control-group">
                <label class="control-label" for="name">Name</label>

                <div class="controls">
                    <input type="text" id="name" name="name" size="255" value="${fn:escapeXml(settings.name)}">
                </div>
            </div>
            <div class="control-group">
                <label class="control-label" for="folder">Folder</label>

                <div class="controls">
                    <div class="input-append">
                        <input type="text" id="folder" name="folder"
                               value="${fn:escapeXml(settings.folder)}">
                        <span class="add-on help"
                              title="Folder Organization"
                              data-content="Used to organize the top navigation menu. Has no effect on client URLs">?</span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <security:authorize access="hasRole('ROLE_SYSTEM_ADMIN')">
                    <a href="${pageContext.request.contextPath}/settings/${fn:escapeXml(settings.name)}/delete"
                       class="btn btn-danger pull-left">Delete</a>
                </security:authorize>
                <a href="${pageContext.request.contextPath}/settings/${fn:escapeXml(settings.name)}"
                   class="btn">Cancel</a>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </form>
    </div>
</div>
<bootstrap:validate formId="editSettingsForm" modelName="settings"/>
<script>
    $(function() {
        $(".help").popover();
    })
</script>
</body>
</html>