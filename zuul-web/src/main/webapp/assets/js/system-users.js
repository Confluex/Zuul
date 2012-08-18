$(function () {
    var deleteRole = function () {
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
    };

    var addRoleToUser = function () {
        var link = $(this);
        var roleId = link.data("role-id");
        var userId = addRoleDialog.data("user-id");
        $.ajax({
            url:getContextPath() + "/admin/system/user/role?roleId=" + roleId + "&userId=" + userId,
            type:'POST',
            success:function (data, status, xhr) {
                link.parents("span").remove();
                addRoleDialog.modal('hide');
                var row = $("#userTable").find("[data-user-id='" + userId + "']");
                var td = row.find(".role-column");
                var roleLabel = $(document.createElement("span"));
                roleLabel.addClass("label");
                roleLabel.addClass("label-warning");
                roleLabel.text(link.text());
                var closeLink = $(document.createElement('a'));
                closeLink.attr('href', '#');
                closeLink.text('×');
                closeLink.data("role-id", roleId);
                closeLink.click(deleteRole);
                roleLabel.append(closeLink);
                td.append(roleLabel).show('slow');
            },
            error:function (xhr, status, error) {
                alert("An error has occurred while removing the role. Please check the log for more details.");
            }
        });
    };

    var showAddRoleDialog = function () {
        var link = $(this);
        var row = link.parents("tr");
        var userId = row.data("user-id");
        addRoleDialog.data("user-id", userId);
        addRoleDialog.modal('show');
    };



    var addRoleDialog = $('#addRoleDialog').modal({show:false});
    $(".descriptive").tooltip();
    $(".delete-role").click(deleteRole);
    $(".add-role").click(addRoleToUser);
    $(".add-role-action").click(showAddRoleDialog);
});