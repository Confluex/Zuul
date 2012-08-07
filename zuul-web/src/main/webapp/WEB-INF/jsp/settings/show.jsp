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
            <h1>${fn:escapeXml(groupName)}
                <small>TODO: description</small>
            </h1>
        </div>

        <div class="tabbable tabs-left">
            <ul class="nav nav-tabs">
                <c:forEach var="env" items="${environments}" varStatus="i">
                    <c:set var="activeFlag" value="${i.index == 0 ? 'active' : ''}"/>
                    <li class="${activeFlag}">
                        <a data-toggle="tab" href="#${fn:escapeXml(env.name)}">${fn:escapeXml(env.name)}</a>
                    </li>
                </c:forEach>
            </ul>
            <div class="tab-content">
                <c:forEach var="env" items="${environments}" varStatus="i">
                    <c:set var="activeFlag" value="${i.index == 0 ? 'active' : ''}"/>
                    <c:set var="group" value="${groupsByEnv[env]}" scope="request"/>
                    <c:set var="environment" value="${env}" scope="request"/>
                    <div id="${fn:escapeXml(env.name)}" class="tab-pane ${activeFlag}" style="min-height: 300px;">
                        <c:choose>
                            <c:when test="${group != null}">
                                <jsp:include page="_viewGroup.jsp"/>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info">
                                    You don't have any settings for this environment yet.
                                    Use one of the following options to create a new set of properties for this environment.
                                </div>
                                <div class="btn-group">
                                    <a href="#" class="btn btn-large btn-primary">Create New</a>
                                    <a href="#" class="btn btn-large btn-primary">Upload File</a>
                                    <a href="#" class="btn btn-large btn-primary">Copy Environment</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
        </div>
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