$(function () {
    $(".descriptive").tooltip();

    /**
     * Delete role from user
     */
    $(".delete-role").click(function () {
        var link = $(this);
        var row = link.parents("tr");
        var roleId = link.data("role-id");
        var userId = row.data("user-id");
        // note that something appears to be broken with .ajax delete with data. Hard coding
        // url parameters for now.
        $.ajax({
            url:getContextPath() + "/admin/system/user/role?roleId=" + roleId + "&userId=" + userId,
            type:'DELETE',
            success:function (data, status, xhr) {
                var span = link.parents("span");
                span.hide('slow', function () {
                    span.remove();
                })
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the role. Please check the log for more details.");
            }
        });
    });

    /**
     * Add a new role to a user
     */
    $(".add-role").click(function () {
        var link = $(this);
        var roleId = link.data("role-id");
        var userId = link.data("user-id");
        $.ajax({
            url:getContextPath() + "/admin/system/user/role?roleId=" + roleId + "&userId=" + userId,
            type:'POST',
            success:function (data, status, xhr) {
                link.parents("span").remove();
                $("#addRoleDialog").modal('close');
                alert("TODO add role label to user row");


            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the role. Please check the log for more details.");
            }
        });
    });

    /**
     * Display add role form
     */
    $(".add-role-action").click(function () {
        var link = $(this);
        var row = link.parents("tr");
        var userId = row.data("user-id");
        $('#addRoleDialog').modal();
        $("#addRoleDialog .btn-primary").click(function () {
            $.ajax({
                url:getContextPath() + "/admin/system/user/role?roleId=" + roleId + "&userId=" + userId,
                type:'POST',
                success:function (data, status, xhr) {
                    var span = link.parents("span");
                    span.hide('slow', function () {
                        span.remove();
                    })
                },
                error:function (xhr, status, error) {
                    alert("An error has occurred while removing the role. Please check the log for more details.");
                }
            });
        });
    });
});