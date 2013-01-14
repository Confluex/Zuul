<%--@elvariable id="groupName" type="java.lang.String"--%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="bootstrap" tagdir="/WEB-INF/tags/bootstrap" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>PGP Key Confirmation</title>
    <meta name="tab" content="settings"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>
                PGP Key Confirmation
                <small>There's no going back!</small>
            </h1>
        </div>
    </div>
    <div class="span12">
        <div class="alert">
            <strong>Considerations:</strong>
            You are about to enable PGP encryption. This is very secure but you should know a few things first.
            <label class="checkbox stacked">
                <input type="checkbox" id="remember"> I know what I'm doing. Don't bug me anymore.
            </label>
        </div>
        <ul class="stacked">
            <li>You will only be able to encrypt values in the Zuul user interface. Decryption will not be possible.
            </li>
            <li>Once PGP encrypted, the key cannot be changed again unless all values are un-encrypted.</li>
            <li>Decryption is only available to the clients with the private key.</li>
            <li>Existing encrypted values will be encrypted with the new key.</li>
        </ul>
        <div class="form-actions">
            <c:url var="backUrl"  value="/settings/${fn:escapeXml(groupName)}#${fn:escapeXml(environment)}"/>
            <a href="${backUrl}" class="btn">
                <i class="icon-ban-circle"></i>
                Cancel
            </a>
            <a href="#" id="confirm" class="btn btn-primary">
                <i class="icon-ok icon-white"></i>
                Change It!
            </a>

        </div>
    </div>
</div>
<script>
    $("#confirm").click(function() {
        var remember = $("#remember").is(':checked');
        createCookie('PGP_KEY_CHANGE_CONFIRMED', "true", remember ? 1000 : 1);
        location.reload();
    });
</script>
</body>
</html>