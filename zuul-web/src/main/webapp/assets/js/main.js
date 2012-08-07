function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
}
(function ($) {
    $.fn.settingsMenu = function (options) {
        var settings = $.extend({  nothing:'' }, options);
        var subMenu = this;
        $('#create-settings-group').click(function() {
           var newGroupName = prompt('Name:');
            // TODO this should be a modal dialog form
            document.location = getContextPath() + '/settings/' + encodeURI(newGroupName)
        });
        $.ajax({
            url:getContextPath() + "/settings.json",
            success:function (data) {
                var fileNames = distinctGroupNames(data);
                for (var i = 0; i < fileNames.length; i++) {
                    var name = fileNames[i];
                    var link = $(document.createElement('a'));
                    link.text(name);
                    link.attr('href', getContextPath() + "/settings/" + encodeURI(name));
                    var subMenuItem = $(document.createElement('li'));
                    subMenuItem.append(link);
                    subMenu.append(subMenuItem);
                }
            }
        });
        function distinctGroupNames(data) {
            var names = [];
            for (var i = 0; i < data.length; i++) {
                var group = data[i];
                if ($.inArray(group.name, names) == -1) {
                    names.push(group.name);
                }
            }
            return names
        }

        return this;
    };
})(jQuery);

