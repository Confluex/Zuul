$(function () {
    var dialog = $('#editEntryDialog').modal({show:false});
    var link = null;

    function swapPrimary(newKey) {
        var oldButton = $(".btn-group").find(".btn-primary");
        oldButton.removeClass("btn-primary");
        oldButton.find("i").removeClass("icon-check");
        oldButton.find("i").removeClass("icon-white");
        oldButton.find("i").addClass("icon-ok");

        var newButtons = $("[data-key-name='" + newKey + "']").find(".btn-group");
        newButtons.find("button").addClass("btn-primary");
        newButtons.find("i").removeClass("icon-ok");
        newButtons.find("i").addClass("icon-check icon-white");
    }

    var toggleDefaultKey = function () {
        var link = $(this);
        var row = link.parents("tr");
        var keyName = row.data("key-name");
        $.ajax({
            url:getContextPath() + "/system/keys/default.json",
            type:'PUT',
            data:JSON.stringify({name:keyName}),
            contentType:'application/json',
            success:function (data, status, xhr) {
                swapPrimary(data.name);
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the role. Please check the log for more details.");
            }
        });
        return false;
    };

    var onSaveHandler = function (entry) {
        var row = link.parents("tr");
        row.fadeOut('slow', function () {
            row.children(".key-description").text(entry.description);
            row.children(".key-name").text(entry.name);
        });
        row.fadeIn('slow');
    };



    var deleteEntry = function () {
        link = $(this);
        $.ajax({
            url:getContextPath() + '/system/keys/' + link.data('id') + ".json",
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

    var showEditDialog = function () {
        link = $(this);
        var id = link.parents("tr").data("key-name");
        $('#editEntryDialog').modal('show');
        $('#editEntryForm').jsonForm('loadResourceById', id);
    };

    var toggleShowPassword = function () {
        var link = $(this);
        link.toggleClass("btn-danger");
        link.find("i").toggleClass("icon-white");
        var password = link.prev("input");
        // do a little dance around browser security
        var copy = password.clone();
        copy.attr("type", password.attr("type") == "password" ? "text" : "password");
        copy.insertBefore(password);
        password.remove();
        return false;
    };


    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".default-key-action").click(toggleDefaultKey);
    $(".edit-key-action").click(showEditDialog);
    $(".delete-key-action").click(deleteEntry);
    $("#toggleShowPassword").tooltip().click(toggleShowPassword);
    $("#password").popover({placement:'bottom', trigger:'focus'})
});
