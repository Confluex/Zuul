$(function () {
    var toggleEncrypt = function () {
        var link = $(this);
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
        row.fadeOut('slow', function() {
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

    var dialog = $('#editEntryDialog').modal({show:false});
    var link = null;
    $(".descriptive").popover({placement:'bottom'});
    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".encrypt-link").click(toggleEncrypt);
    $(".edit-link").click(function () {
        link = $(this);
        $('#editEntryDialog').modal('show');
        $('#editEntryForm').jsonForm('loadResourceById', link.data('id'));
    });
    $(".delete-link").click(deleteEntry);
    if (window.location.hash) {
        $('ul.nav a[href="' + window.location.hash + '"]').tab('show');
    }
});