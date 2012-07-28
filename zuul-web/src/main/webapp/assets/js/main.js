(function ($) {
    $.fn.settingsMenu = function (options) {
        var settings = $.extend({  context:'/' }, options);
        var menuItem = this;
        $.ajax({
            url: settings.context + "/settings.json",
            success:function (data) {
                var fileNames = distinctGroupNames(data);
                var subMenu = $(document.createElement('ul'));
                subMenu.addClass('dropdown-menu');
                menuItem.append(subMenu);
                for each (var name in fileNames) {
                    var link = $(document.createElement('a'));
                    link.text(name);
                    link.attr('href', settings.context + "/" + name);
                    var subMenuItem = $(document.createElement('li'));
                    subMenuItem.append(link);
                    subMenu.append(subMenuItem);
                }
            }
        });

        function distinctGroupNames(data) {
            var names = [];
            for each (var group in data) {
                if ($.inArray(group.name, names) == -1) {
                    names.push(group.name);
                }
            }
            return names
        }

        return this;
    };
})(jQuery);