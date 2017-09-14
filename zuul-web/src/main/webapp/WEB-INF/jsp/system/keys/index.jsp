<%--@elvariable id="keys" type="java.util.List<com.confluex.zuul.data.model.EncryptionKey>"--%>
<%--@elvariable id="keyMetaData" type="java.util.Map<java.lang.String, com.confluex.zuul.service.security.KeyConfiguration>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="zfn" uri="/WEB-INF/tld/functions.tld" %>
<!DOCTYPE html>
<html>
<head>
    <title>Key Management</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/ext/binder-0.3.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/system-keys.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/json-form.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Key Management</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12" style="margin-bottom: 1em;">
        <a href="${pageContext.request.contextPath}/system/keys/create" class="btn btn-primary">
            <i class="icon-white icon-plus"></i>
            New Key
        </a>
    </div>
</div>
<div class="row">
    <div class="span12">
        <table id="keysTable" class="table table-bordered table-condensed">
            <thead>
            <tr>
                <th>Actions</th>
                <th>Name</th>
                <th>Algorithm</th>
                <th>Description</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="key" items="${keys}">
                <c:set var="config" value="${keyMetaData[key.algorithm]}"/>
                <tr data-key-name="${fn:escapeXml(key.name)}">
                    <td>
                        <c:choose>
                            <c:when test="${key.defaultKey}">
                                <c:set var="buttonClass" value="btn-primary"/>
                                <c:set var="iconClass" value="icon-check icon-white"/>
                            </c:when>
                            <c:otherwise>
                                <c:set var="buttonClass" value=""/>
                                <c:set var="iconClass" value="icon-ok"/>
                            </c:otherwise>
                        </c:choose>

                        <div class="btn-group">
                            <button class="btn ${buttonClass} edit-key-action" data-key-secret="${config.secret}">
                                <i class="${iconClass}"></i>
                                Edit
                            </button>
                            <button class="btn ${buttonClass} dropdown-toggle" data-toggle="dropdown">
                                <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu">
                                <li><a href="#" class="default-key-action">Set Default</a></li>
                                <li class="divider"></li>
                                <li><a href="#" class="delete-key-action">Delete</a></li>
                            </ul>
                        </div>
                    </td>
                    <td class="key-name">${fn:escapeXml(key.name)}</td>
                    <td class="key-algorithm" title="${fn:escapeXml(key.algorithm)}">
                        ${fn:escapeXml(config.description)}
                    </td>
                    <td class="key-description">${fn:escapeXml(key.description)}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
<div class="modal modal-wide hide" id="editEntryDialog">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal">×</button>
        <h3>Edit Entry</h3>
    </div>
    <div class="modal-body">
        <form id="editEntryForm" action="${pageContext.request.contextPath}/system/keys"
              onsubmit="return false;" data-save-method="PUT" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="keyName">Name</label>

                    <div class="controls">
                        <input id="keyName" name="name" class="span3" type="text" disabled>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="description">Description</label>

                    <div class="controls">
                        <input id="description" name="description" class="span3" type="text">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Algorithm</label>
                    <div class="controls">
                        <div class="btn-group" data-toggle="buttons-radio">
                            <c:forEach var="metaData" items="${keyMetaData}">
                                <label class="radio">
                                    <input type="radio" name="algorithm" value="${fn:escapeXml(metaData.key)}"
                                           data-key-secret="${metaData.value.secret}">
                                        <span title="${fn:escapeXml(metaData.value.algorithm)}">
                                                ${fn:escapeXml(metaData.value.description)}
                                        </span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </div>
                <div id="passwordGroup" class="control-group">
                    <label class="control-label" for="password">Password</label>

                    <div class="controls">
                        <div class="input-append">
                            <input id="password" name="password" class="span3" type="password" title="Change Password"
                                   data-content="If you change the password, this will re-encrypt any existing entries with the new password.">
                            <span id="toggleShowPassword" class="add-on" title="Toggle show password">
                                <i class="icon-lock"></i>
                            </span>
                        </div>
                    </div>
                </div>
                <div id="publicKeyGroup" class="control-group hide">
                    <label class="control-label" for="publicKey">Public Key</label>

                    <div class="controls">
                        <div class="input-append">
                            <textarea id="publicKey" name="password" class="span4 code"
                                      style="width: 470px; min-height: 250px;"
                                      placeholder="Paste ASCII Armored Public Key"></textarea>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div class="modal-footer">
        <a href="#" class="btn btn-danger pull-left">Delete</a>
        <a href="#" class="btn" data-dismiss="modal">Cancel</a>
        <a href="#" class="btn btn-primary">Save changes</a>
    </div>
</div>
</body>
</html>