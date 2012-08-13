<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
</head>
<body>
<div class="row">
    <div class="hero-unit span9 offset1">
        <h1>Beta..</h1>

        <p>No, it's not beta to be cool. This really is a work in progress.</p>

        <p><a href="http://github.com/mcantrell/zuul" class="btn btn-primary btn-large">Help Out &raquo;</a></p>
    </div>
</div>

<div id="features" class="row">
    <div class="span10 offset1">
        <h2>Features</h2>
        <br>

        <div class="row">
            <div class="span3">
                <h4>Centralized Management</h4>
                <hr>
                <p>
                    Provision your application configuration files by environment and configure them in a
                    single location. Let your support and operations team view and configure important
                    resources such as database URLs and credentials.
                </p>
            </div>
            <div class="span3">
                <h4>Encryption</h4>
                <hr>
                <p>
                    Encrypt sensitive values using the popular <a href="http://www.jasypt.org/">Jasypt library</a>
                </p>
            </div>
            <div class="span3">
                <h4>RESTful Services</h4>
                <hr>
                <p>
                    Easily access the configuration data from within your application via RESTful services. Content
                    can be rendered as JSON or Java Properties files.
                </p>
            </div>
        </div>
        <div style="margin-top: 9px;" class="row">
            <div class="span3">
                <h4>Role Based Access Controls</h4>
                <hr>
                <p>
                    System administrators can easily control who has access to which features with user
                    to role mappings.
                </p>
            </div>

            <div class="span3">
                <h4>Single Sign On</h4>
                <hr>
                <p>
                    Integrates with popular single sign on providers such as Google and Yahoo via OpenID.
                </p>
            </div>
            <div class="span3">
                <h4>Spring Client</h4>
                <hr>
                <p>
                    Seamlessly integrate your <a href="http://springframework.org/">Spring applications</a> with
                    our custom client and spring XML namespace.
                </p>
            </div>
        </div>
        <br>

        <div class="well">
            <h3>More to Come.. <a target="_blank" href="http://github.com/mcantrell/zuul" >Visit the GitHub site</a>
                to become more involved or monitor progress.
            </h3>
        </div>


    </div>
</div>


</body>
</html>