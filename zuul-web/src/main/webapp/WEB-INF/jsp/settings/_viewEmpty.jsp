<%--@elvariable id="environment" type="org.devnull.zuul.data.model.Environment"--%>
<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<div class="hero-unit">
    <h1>New File - ${fn:escapeXml(environment.name)}</h1>

    <p>
        You don't have any settings for this environment yet. Use one of the following
        options to create a new set of properties for this environment.
    </p>

    <div class="btn-group">
        <c:url var="scratchUrl" value="/settings/create/scratch">
            <c:param name="name" value="${groupName}"/>
            <c:param name="environment" value="${environment.name}"/>
        </c:url>
        <a id="scratchLink" href="${scratchUrl}" class="btn btn-large btn-primary">
            <i class="icon-plus icon-white"></i>
            From Scratch
        </a>
        <c:url var="uploadUrl" value="/settings/create/upload">
            <c:param name="name" value="${groupName}"/>
            <c:param name="environment" value="${environment.name}"/>
        </c:url>
        <a id="uploadLink" href="${uploadUrl}" class="btn btn-large btn-primary">
            <i class="icon-upload icon-white"></i>
            Upload File
        </a>
        <c:url var="copyUrl" value="/settings/create/copy">
            <c:param name="name" value="${groupName}"/>
            <c:param name="environment" value="${environment.name}"/>
        </c:url>
        <a href="${copyUrl}" class="btn btn-large btn-primary">
            <i class="icon-share-alt icon-white"></i>
            Copy Existing
        </a>
    </div>
</div>