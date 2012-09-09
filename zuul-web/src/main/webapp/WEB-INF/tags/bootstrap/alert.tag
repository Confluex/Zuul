<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ attribute name="message" required="false" type="java.lang.String" %>
<%@ attribute name="type" required="false" type="java.lang.String" %>
<c:if test="${fn:length(message) > 0}">
    <c:set var="alertClass" value="alert-${type == null ? 'info' : type}"/>
    <div class="alert ${alertClass}">
        <button type="button" class="close" data-dismiss="alert">&times;</button>
            ${message}
    </div>
</c:if>
