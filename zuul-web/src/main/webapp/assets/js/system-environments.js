$(function () {
    $(".sortable").sortable({
        update: function( event, ui ) {
            var listItems = $("li.environment");
            var environments = [];
            listItems.each(function(i, val) {environments.push($(val).data("name"))});
            $.ajax({
                url:getContextPath() + "/system/environments/sort.json",
                data:JSON.stringify(environments),
                contentType:'application/json',
                type:'PUT',
                success:function (data) {
                    // TODO notification
                },
                error:function (jqXHR, textStatus, errorThrown) {
                    showAlert("Error saving order: " + errorThrown);
                }
            });
        }
    });
    $(".descriptive").popover({trigger:'hover', placement: 'bottom'});
});