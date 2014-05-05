<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ attribute name="url" required="true" type="java.lang.String"  %>
<%@ attribute name="icon" required="true" type="java.lang.String"  %>
<%@ attribute name="name" required="true" type="java.lang.String"  %>
<div class="thumbnail">
    <c:set var="securityCheck" value="j_spring_openid_security_check"/>
    <c:url var="openIdUrl" value="${securityCheck}">
        <c:param name="openid_identifier" value="${url}"/>
    </c:url>
    <a href="${openIdUrl}">
        <img src="${pageContext.request.contextPath}/assets/images/logins/${icon}" alt="${name}">
    </a>
</div>
