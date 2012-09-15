$(function () {
    var deleteEnvironment = function () {
        var link = $(this);
        var envId = link.data("env-id");
        $.ajax({
            url:getContextPath() + "/system/environments/" + encodeURI(envId) + ".json",
            type:'DELETE',
            success:function (data, status, xhr) {
                var span = link.parents("span");
                span.hide('slow', function () {
                    span.remove();
                })
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the environment. Please check the log for more details.");
            }
        });
    };


    $(".delete-env").click(deleteEnvironment);
});