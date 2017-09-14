<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="zfn" uri="/WEB-INF/tld/functions.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ attribute name="modelName" required="true" type="java.lang.String" %>
<%@ attribute name="formId" required="true" type="java.lang.String" %>
<%@ attribute name="placement" required="false" type="java.lang.String" %>
<%@ attribute name="trigger" required="false" type="java.lang.String"  %>

<spring:hasBindErrors name="${modelName}">
    <script>
        $(function () {
            var alert = $(document.createElement('div'));
            alert.addClass('alert alert-error');
            alert.html("<strong>Uh oh..</strong> <p>Looks like we have invalid input. Please correct the errors below:</p>");
            $("#${formId}").prepend(alert);
            <%--@elvariable id="errors" type="org.springframework.validation.Errors"--%>
            <c:set var="errorsByField" value="${zfn:errorsByField(errors)}"/>
            <c:forEach var="error" items="${errorsByField}">
                $("#${formId} input[name=${error.key}]").addClass("error").popover({
                    title:'Validation Errors',
                    content: '${zfn:join(error.value)}',
                    placement: '${placement == null ? 'right' : placement}',
                    trigger: 'focus'
                });
            </c:forEach>
        });
    </script>
</spring:hasBindErrors>