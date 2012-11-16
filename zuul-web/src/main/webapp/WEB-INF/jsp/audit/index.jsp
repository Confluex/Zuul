<%--@elvariable id="audits" type="java.util.List<org.devnull.zuul.data.model.SettingsAudit>"--%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Audit</title>
    <meta name="tab" content="admin"/>
</head>
<body>

<div class="row">
    <div class="span12">
        <div class="page-header">
            <h1>Audit History</h1>
        </div>
        <display:table name="audits" class="table table-bordered table-condensed table-striped" requestURI="/audit" sort="external">
            <display:column property="id" title="Rev" sortable="true" sortProperty="id" />
            <display:column property="modifiedDate" title="Date" sortable="true"/>
            <display:column property="groupEnvironment" title="Environment" sortable="true"/>
            <display:column property="groupName" title="Group" sortable="true"/>
            <display:column property="settingsKey" title="Key" sortable="true"/>
            <display:column property="settingsValue" title="Value" sortable="true"/>
            <display:column property="modifiedBy" title="User" sortable="true"/>
            <display:column property="type" title="Operation" sortable="true"/>
        </display:table>
    </div>
</div>
</body>
</html>