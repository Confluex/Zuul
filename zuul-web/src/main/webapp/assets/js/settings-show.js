$(function () {
    var dialog = $('#editEntryDialog').modal({show:false});
    var link = null;

    var updateEncryptLink = function(target, encrypted) {
        target.data('encrypted', encrypted);
        target.text(encrypted ? ' Decrypt ' : ' Encrypt ');
        var icon = $(document.createElement('i')).addClass("icon-lock");
        target.prepend(icon);
    };

    var toggleEncrypt = function () {
        link = $(this);
        var operation = link.data('encrypted') ? 'decrypt' : 'encrypt';
        var id = link.data('id');
        $.ajax({
            url:getContextPath() + "/settings/entry/" + operation + ".json",
            data:{id:id},
            contentType:'application/json',
            success:function (data) {
                updateEncryptLink(link, data.encrypted);
                link.parents("tr").children(".value").text(data.value);
            },
            error: showJsonErrors
        });
    };
    var deleteEntry = function () {
        link = $(this);
        $.ajax({
            url:getContextPath() + '/settings/entry/' + link.data('id') + ".json",
            type:'DELETE',
            contentType:'application/json',
            success:function (data, status, xhr) {
                onDeleteHandler();
            },
            error:showJsonErrors
        });
    };
    var onDeleteHandler = function () {
        var row = link.parents("tr");
        row.fadeOut('slow', function () {
            row.remove();
        });
    };
    var onSaveHandler = function (entry) {
        var row = link.parents("tr");
        updateEncryptLink(link.parent().find(".encrypt-link"), entry.encrypted);
        row.fadeOut('slow', function () {
            row.children(".value").text(entry.value);
            row.children(".key").text(entry.key);
        });
        row.fadeIn('slow');
    };
    var showEditDialog = function () {
        link = $(this);
        $('#editEntryDialog').modal('show');
        $('#editEntryForm').jsonForm('loadResourceById', link.data('id'));
    };
    var deleteGroup = function () {
        link = $(this);
        var env = link.data("env");
        var group = link.data("group");
        $.ajax({
            url:getContextPath() + "/settings/" + encodeURI(env) + "/" + encodeURI(group) + ".properties",
            type:'DELETE',
            contentType:'application/json',
            success:function (data, status, xhr) {
                window.location = getContextPath() + "/settings/" + encodeURI(group) + "#" + encodeURI(env);
                window.location.reload();
            },
            error: showJsonErrors
        });
    };
    var loadKeysDropDownMenu = function (data) {
        $(".keys-dropdown-menu").each(function () {
            var menu = $(this);
            var group = menu.data("group");
            var environment = menu.data("environment");
            var currentKey = menu.data("current-key");
            for (var i = 0; i < data.length; i++) {
                var key = data[i];
                var listItem = $(document.createElement("li"));
                var link = $(document.createElement("a"));
                var url = getContextPath() + "/settings/" + encodeURI(environment) + "/" + encodeURI(group) + "/key/change?keyName=" + encodeURI(key.name);
                link.attr("href", url);
                link.text(" " + key.name);
                if (key.name == currentKey) {
                    var icon = $(document.createElement("i"));
                    icon.addClass("icon-check");
                    link.prepend(icon);
                }
                listItem.append(link);
                menu.append(listItem);
            }
        });
    };


    $("#encrypted").popover({placement:'right', trigger:'hover'});
    $(".descriptive").popover({placement:'top', trigger:'hover'});
    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".encrypt-link").click(toggleEncrypt);
    $(".edit-link").click(showEditDialog);
    $(".delete-link").click(deleteEntry);
    $(".delete-group-link").click(deleteGroup);
    if ($(".keys-dropdown-menu").length) {
        $.ajax({
            url:getContextPath() + "/system/keys.json",
            contentType:'application/json',
            success:loadKeysDropDownMenu,
            error: showJsonErrors
        });
    }
    if (window.location.hash) {
        $('ul.nav a[href="' + window.location.hash + '"]').tab('show');
    }
});