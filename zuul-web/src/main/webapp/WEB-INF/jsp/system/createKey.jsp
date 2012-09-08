<%--@elvariable id="environments" type="java.util.List<org.devnull.zuul.data.model.Environment>"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Key</title>
    <meta name="tab" content="admin"/>
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
                    <label class="control-label" for="password">Password</label>

                    <div class="controls">
                        <div class="input-append">
                            <input value="${fn:escapeXml(createKeyForm.password)}" id="password" name="password"
                                   class="span3" type="password" placeholder="Shhh.." autocomplete="off">
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