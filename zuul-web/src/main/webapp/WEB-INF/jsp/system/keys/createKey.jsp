<%--@elvariable id="environments" type="java.util.List<com.confluex.zuul.data.model.Environment>"--%>
<%--@elvariable id="keyMetaData" type="java.util.Map<java.lang.String, com.confluex.zuul.service.security.KeyConfiguration>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Key</title>
    <meta name="tab" content="admin"/>
    <script src="${pageContext.request.contextPath}/assets/ext/binder-0.3.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/system-keys.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/json-form.js"></script>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Create a new Encryption Key</h1>
        </div>
    </div>
</div>
<div class="row">
    <div class="span12">
        <form id="createKeyForm" action="${pageContext.request.contextPath}/system/keys/create"
              method="POST" class="form-horizontal">
            <fieldset>
                <div class="control-group">
                    <label class="control-label" for="keyName">Name</label>

                    <div class="controls">
                        <input value="${fn:escapeXml(createKeyForm.name)}" id="keyName" name="name" class="span3"
                               type="text" placeholder="Enter a unique name..">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="description">Description</label>

                    <div class="controls">
                        <input value="${fn:escapeXml(createKeyForm.description)}" id="description" name="description"
                               class="span3" type="text">
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label">Algorithm</label>

                    <div class="controls">
                        <div class="btn-group" data-toggle="buttons-radio">
                            <c:forEach var="config" items="${keyMetaData}">
                                <label class="radio">
                                    <input type="radio" name="algorithm" value="${fn:escapeXml(config.key)}"
                                           data-key-secret="${config.value.secret}">
                                        <span title="${fn:escapeXml(config.value.algorithm)}">
                                                ${fn:escapeXml(config.value.description)}
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
                            <input value="${fn:escapeXml(createKeyForm.password)}" id="password" name="password"
                                   class="span3" type="password" placeholder="Shhh.." autocomplete="off">
                        </div>
                    </div>
                </div>
                <div id="publicKeyGroup" class="control-group hide">
                    <label class="control-label" for="publicKey">Public Key</label>

                    <div class="controls">
                        <div class="input-append">
                            <textarea id="publicKey" name="password" class="span4 code"
                                      style="width: 470px; min-height: 250px;"
                                      placeholder="Paste ASCII Armored Public Key">${fn:escapeXml(createKeyForm.password)}</textarea>
                        </div>
                    </div>
                </div>
                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/system/keys" class="btn">
                        <i class="icon-arrow-left"></i>
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">Create Key</button>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<bootstrap:validate modelName="createKeyForm" formId="createKeyForm"/>
</body>
</html>