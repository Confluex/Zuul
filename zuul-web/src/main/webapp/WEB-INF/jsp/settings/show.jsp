<%--@elvariable id="name" type="java.lang.String"--%>
<%--@elvariable id="environments" type="java.util.List<com.confluex.zuul.data.model.Environment>"--%>
<%--@elvariable id="settings" type="com.confluex.zuul.data.model.Settings"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>${fn:escapeXml(name)}</title>
    <meta name="tab" content="settings"/>
    <script src="${pageContext.request.contextPath}/assets/ext/binder-0.3.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/json-form.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/settings-show.js"></script>
    <style>
        td.value {
            max-width: 500px;
        }
    </style>
</head>
<body>
<security:authorize var="canEdit" access="hasRole('ROLE_ADMIN')"/>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <c:choose>
                <c:when test="${canEdit}">
                    <h1>
                        <a href="${pageContext.request.contextPath}/settings/${fn:escapeXml(name)}/edit">${fn:escapeXml(name)}</a>
                    </h1>
                </c:when>
                <c:otherwise>
                    <h1>${fn:escapeXml(name)}</h1>
                </c:otherwise>
            </c:choose>
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
                    <c:set var="group" value="${settings.getAt(env)}" scope="request"/>
                    <c:set var="environment" value="${env}" scope="request"/>
                    <div id="${fn:escapeXml(env.name)}" class="tab-pane ${activeFlag}" style="min-height: 300px;">
                        <c:choose>
                            <c:when test="${group != null}">
                                <jsp:include page="_viewGroup.jsp"/>
                            </c:when>
                            <c:otherwise>
                                <jsp:include page="_viewEmpty.jsp"/>
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
              onsubmit="return false;" data-save-method="PUT" class="form-horizontal">
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
                <div class="control-group">
                    <label class="control-label" for="encrypted">Encrypted</label>

                    <div class="controls">
                        <input id="encrypted" name="encrypted" type="checkbox" value="true" title="Encrypted Flag"
                               data-content="Use only to manually encrypted state. Can be useful for resetting values, etc.">
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