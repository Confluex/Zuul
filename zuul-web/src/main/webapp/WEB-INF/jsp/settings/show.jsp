<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%--@elvariable id="groupsByEnv" type="java.util.Map<String, org.devnull.zuul.data.model.SettingsGroup>"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>${fn:escapeXml(groupName)}</title>
    <script src="${pageContext.request.contextPath}/assets/ext/binder-0.3.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/settings-show.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>${fn:escapeXml(groupName)}</h1>
        </div>

        <c:forEach var="env" items="${groupsByEnv}">
            <h1>
                <c:url var="downloadUrl" value="/settings/${env.key}/${env.value[0].name}.properties"/>
                <a href="${downloadUrl}">${fn:escapeXml(env.key)}</a>
                <small>TODO: description</small>
            </h1>

            <table class="table table-bordered table-condensed">
                <thead>
                <tr>
                    <th style="width: 30%;">Key</th>
                    <th style="width: 60%;">Value</th>
                    <th style="width: 10%;">Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:set var="entries" value="${env.value[0].entries}"/>
                <c:forEach var="e" items="${entries}">
                    <tr class="entry">
                        <td class="key">${e.key}</td>
                        <td class="value">${e.value}</td>
                        <td>
                            <div class="btn-group">
                                <a class="btn btn-small dropdown-toggle" data-toggle="dropdown" href="#">
                                    <i class="icon-cog"></i>
                                    Action
                                    <span class="caret"></span>
                                </a>
                                <ul class="settings-entry dropdown-menu">
                                    <li>
                                        <a href="javascript:void(0);" class="encrypt-link" data-id="${e.id}"
                                           data-encrypted="${e.encrypted}">
                                            <i class="icon-lock"></i>
                                                ${e.encrypted ? 'Decrypt' : 'Encrypt'}
                                        </a>
                                    </li>
                                    <li>
                                        <a href="#" class="edit-link" data-id="${e.id}">
                                            <i class="icon-edit"></i>
                                            Edit
                                        </a>
                                    </li>
                                    <li class="divider"></li>
                                    <li>
                                        <a href="#" data-id="${e.id}">
                                            <i class="icon-trash"></i>
                                            Delete
                                        </a>
                                    </li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:forEach>
    </div>
</div>
<div class="modal hide" id="editEntryDialog">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Edit Entry</h3>
    </div>
    <div class="modal-body">
        <form id="editEntryForm" action="${pageContext.request.contextPath}/settings/entry"
              onsubmit="return false;" method="PUT" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="key">Key</label>

                    <div class="controls">
                        <input id="key" name="key" class="span3" type="text">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="value">Value</label>

                    <div class="controls">
                        <input id="value" name="value" class="span3" type="text">
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="modal-footer">
        <a href="#" class="btn btn-danger pull-left">Delete</a>
        <a href="#" class="btn" data-dismiss="modal">Close</a>
        <a href="#" class="btn btn-primary">Save changes</a>
    </div>
</div>
</body>
</html>