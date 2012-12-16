<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>

<c:if test="${not empty param.login_error}">
    <div class="row">
        <div class="span12">
            <div class="alert alert-error">
                <strong>Your login attempt was not successful.</strong>
                <p class="stacked">
                    <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>
                </p>
            </div>
        </div>
    </div>
</c:if>