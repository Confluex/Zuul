$(function () {
    var dialog = $('#editEntryDialog').modal({show:false});
    var link = null;

    var toggleEncrypt = function () {
        link = $(this);
        var operation = link.data('encrypted') ? 'decrypt' : 'encrypt';
        var id = link.data('id');
        $.ajax({
            url:getContextPath() + "/settings/entry/" + operation + ".json",
            data:{id:id},
            success:function (data) {
                link.data('encrypted', data.encrypted);
                link.text(data.encrypted ? 'Decrypt ' : 'Encrypt ');
                var icon = $(document.createElement('i'));
                icon.addClass("icon-lock");
                link.prepend(icon);
                link.parents("tr").children(".value").text(data.value);

            },
            error:function (jqXHR, textStatus, errorThrown) {
                showAlert("Error encrypting value: " + errorThrown);
            }
        });
    };
    var deleteEntry = function () {
        link = $(this);
        $.ajax({
            url:getContextPath() + '/settings/entry/' + link.data('id') + ".json",
            type:'DELETE',
            success:function (data, status, xhr) {
                onDeleteHandler();
            },
            error:function (xhr, status, error) {
                showAlert("An error has occurred while deleting the record. Please check the log for more details.");
            }
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
            success:function (data, status, xhr) {
                var location = getContextPath() + "/settings/" + encodeURI(group) + "#" + encodeURI(env);
                window.location = location;
                window.location.reload();
            },
            error:function (xhr, status, error) {
                showAlert("An error has occurred while deleting the record. Please check the log for more details." + status);
            }
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


    $(".descriptive").popover({placement:'top', trigger:'hover'});
    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".encrypt-link").click(toggleEncrypt);
    $(".edit-link").click(showEditDialog);
    $(".delete-link").click(deleteEntry);
    $(".delete-group-link").click(deleteGroup);
    if ($(".keys-dropdown-menu").length) {
        $.ajax({
            url:getContextPath() + "/system/keys.json",
            success:loadKeysDropDownMenu,
            error: function() { showAlert("Error loading key data. Please check the logs for details.") }
        });
    }
    if (window.location.hash) {
        $('ul.nav a[href="' + window.location.hash + '"]').tab('show');
    }
});