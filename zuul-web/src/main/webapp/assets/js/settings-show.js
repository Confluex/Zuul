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
                // TODO this feels a little excessive... should be simpler
                link.parents("tr").children(".value").text(data.value);

            },
            error:function (jqXHR, textStatus, errorThrown) {
                alert("Error encrypting value: " + errorThrown);
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
                alert("An error has occurred while deleting the record. Please check the log for more details.");
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
            url: getContextPath() + "/settings/" + encodeURI(env) + "/" + encodeURI(group) + ".properties",
            type:'DELETE',
            success:function (data, status, xhr) {
                var location = getContextPath() + "/settings/" + encodeURI(group) + "#" + encodeURI(env);
                window.location = location;
                window.location.reload();
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while deleting the record. Please check the log for more details." + status);
            }
        });
    };


    $(".descriptive").popover({placement:'bottom'});
    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".encrypt-link").click(toggleEncrypt);
    $(".edit-link").click(showEditDialog);
    $(".delete-link").click(deleteEntry);
    $(".delete-group-link").click(deleteGroup);
    if (window.location.hash) {
        $('ul.nav a[href="' + window.location.hash + '"]').tab('show');
    }
});