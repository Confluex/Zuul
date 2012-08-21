$(function () {
    $(".descriptive").popover({placement:'right'});

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
    $(".default-key-action").click(toggleDefaultKey);
});
