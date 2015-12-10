<%--@elvariable id="roles" type="java.util.List<com.confluex.security.model.Role>"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Request Permissions</title>
    <meta name="tab" content="account"/>
</head>
<body>
<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Request Permission</h1>
        </div>
        <div class="alert alert-info">Some areas of the site require extra permissions to access. Please select the
            level of access which you
            require and site administrator will be notified.
        </div>
    </div>
</div>
<div class="row">
       <div class="span12">
           <ul class="thumbnails">
               <li class="span4">
                   <div class="thumbnail">
                       <div class="caption">
                           <h3>System Administrator</h3>

                           <p>
                               This role will allow you complete control over the application. You can manage
                               users, encryption keys, as well as settings.
                           </p>

                           <p>
                               <c:url var="url" value="/account/permissions/ROLE_SYSTEM_ADMIN"/>
                               <a class="btn btn-primary" href="${url}">Request Access</a>
                           </p>
                       </div>
                   </div>
               </li>
               <li class="span4">
                   <div class="thumbnail">
                       <div class="caption">
                           <h3>Administrator</h3>

                           <p>
                               This role will allow you to create and modify settings groups as well as
                               encrypt and decrypt values. It does not grant access to managing users
                               and keys.
                           </p>

                           <p>
                               <c:url var="url" value="/account/permissions/ROLE_ADMIN"/>
                               <a class="btn btn-primary" href="${url}">Request Access</a>
                           </p>
                       </div>
                   </div>
               </li>
               <li class="span4">
                   <div class="thumbnail">
                       <div class="caption">
                           <h3>User</h3>

                           <p>
                               Users with this role will have read only access to settings. They will
                               not be able to encrypt/decrypt or modify settings in any way.
                           </p>

                           <p>
                               <c:url var="url" value="/account/permissions/ROLE_USER"/>
                               <a class="btn btn-primary" href="${url}">Request Access</a>
                           </p>
                       </div>
                   </div>
               </li>
           </ul>
       </div>
   </div>

</body>
</html>