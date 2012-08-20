$(function () {
    $(".descriptive").popover({placement:'right'});

    function swapButtons(oldKey, newKey) {
        oldKey.removeClass("btn-primary");
        oldKey.find("i").removeClass("icon-check icon-white");
        oldKey.find("i").addClass("icon-ok");

        newKey.addClass("btn-primary");
        newKey.find("i").removeClass("icon-ok");
        newKey.find("i").addClass("icon-check icon-white");


        //TODO update the popover title/description.. this isn't working
//        var oldTitle = oldKey.data("original-title");
//        var newTitle = newKey.data("original-title");
//        var oldContent = oldKey.data("content");
//        var newContent = newKey.data("content");
//        $(oldKey).popover({content:newContent, title:newTitle});
//        $(newKey).popover({content:oldContent, title:oldTitle});
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
                var oldKey = $(".select-key-action").filter(".btn-primary");
                swapButtons(oldKey, link);
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the role. Please check the log for more details.");
            }
        });
        return false;
    };
    $(".select-key-action").click(toggleDefaultKey);
});
