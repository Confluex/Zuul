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
                switch (xhr.status) {
                    case 404:
                        alert(envId + " is no longer available. Please try reloading the page.");
                        break;
                    default:
                        alert("An error has occurred while removing the environment. Please check the log for more details.");
                }
            }
        });
    };


    var createEnvironment = function () {
        var input = $("#environmentName");
        if (input.val()) {
            $.ajax({
                url:getContextPath() + "/system/environments/" + encodeURI(input.val()) + ".json",
                type:'POST',
                contentType: "application/json",
                success:function (data, status, xhr) {
                    var environments = $("#environments");
                    var span = $(document.createElement("span"));
                    var deleteLink = $(document.createElement("a"));
                    span.addClass("label label-warning hide");
                    span.text(input.val() + " ");
                    deleteLink.data("env-id", input.val());
                    deleteLink.html("&times;");
                    deleteLink.addClass("delete-env");
                    deleteLink.attr("href", "#");
                    deleteLink.click(deleteEnvironment);
                    span.append(deleteLink).appendTo(environments).show('slow');
                    input.val("");
                },
                error:function (xhr, status, error) {
                    switch (xhr.status) {
                        case 406:
                            alert("Invalid data: " + error);
                            break;
                        default:
                            alert("An error has occurred while creating the environment. Please check the log for more details.");
                    }
                }
            });
        }
    };

    $(".delete-env").click(deleteEnvironment);
    $(".add-env").click(createEnvironment);
});