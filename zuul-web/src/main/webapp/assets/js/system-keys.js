$(function () {
    var dialog = $('#editEntryDialog').modal({show:false});
    var link = null;
    var keyMetaData = {};

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
            error:showJsonErrors
        });
        return false;
    };

    var onSaveHandler = function (entry) {
        var row = link.parents("tr");
        var keyConfig = keyMetaData[entry.algorithm];
        link.data("key-secret", keyConfig.secret);
        row.fadeOut('slow', function () {
            row.children(".key-description").text(entry.description);
            row.children(".key-name").text(entry.name);
            row.children(".key-algorithm").text(keyConfig.description).attr('title', entry.algorithm);
        });
        row.fadeIn('slow');
    };



    var deleteEntry = function () {
        link = $(this);
        var button = link.parents("div.btn-group").find(".edit-key-action");
        var id = link.parents("tr").data("key-name");
        $.ajax({
            url:getContextPath() + '/system/keys/' + encodeURI(id) + ".json",
            type:'DELETE',
            contentType:'application/json',
            success:function (data, status, xhr) {
                onDeleteHandler();
            },
            error:showJsonErrors
        });
        return true;
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
        toggleSecretKeyInput(link.data("key-secret"));
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


    var toggleSecretKeyInput = function(isSecret) {
        if (isSecret == true) {
            $('#passwordGroup').show();
            $('#publicKeyGroup').hide();
            $("#publicKey").prop('disabled', true);
            $("#password").prop('disabled', false);
        }
        else {
            $('#passwordGroup').hide();
            $('#publicKeyGroup').show();
            $("#publicKey").prop('disabled', false);
            $("#password").prop('disabled', true);
        }
    };

    var keyAlgorithmClickHandler = function() {
        toggleSecretKeyInput($(this).data('key-secret'));
    };

    $("#editEntryForm").jsonForm({ dialog:dialog, onSave:onSaveHandler, onDelete:onDeleteHandler });
    $(".default-key-action").click(toggleDefaultKey);
    $(".edit-key-action").click(showEditDialog);
    $(".delete-key-action").click(deleteEntry);
    $("#toggleShowPassword").tooltip().click(toggleShowPassword);
    $("#password").popover({placement:'bottom', trigger:'focus'});
    $.ajax({
        url:getContextPath() + "/system/keys/metadata.json",
        contentType:'application/json',
        type:'GET',
        success:function (data) {
            keyMetaData = data;
        },
        error:showJsonErrors
    });
    $("input[name='algorithm']").click(keyAlgorithmClickHandler);
});
