(function ($) {

    var settings = {
        max: 15
    };
    var subMenu = null;
    var methods = {
        init: function(options) {
            settings = $.extend(settings, options);
            subMenu = this;
            $.ajax({
                url:getContextPath() + "/settings.json",
                success:function (data) {
                    var fileNames = distinctGroupNames(data);
                    for (var i = 0; i < fileNames.length && i < settings.max ; i++) {
                        var name = fileNames[i];
                        var path = getContextPath() + "/settings/" + encodeURI(name);
                        methods.createMenuItem(name, path);
                    }
                    if (fileNames.length > settings.max) {
                        methods.createMenuItem("More..", getContextPath() + "/settings");
                    }
                    $("#navSearchInput").typeahead({source:fileNames, minLength:3,
                        updater:function (selected, context) {
                            document.location = getContextPath() + "/settings/" + encodeURI(selected);
                        }});
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
        },
        createMenuItem:function(name, url) {
            var link = $(document.createElement('a'));
            link.text(name);
            link.attr('href', url);
            var subMenuItem = $(document.createElement('li'));
            subMenuItem.append(link);
            subMenu.append(subMenuItem);
        }
    };

    $.fn.settingsMenu = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.settingsMenu');
        }
    };

})(jQuery);
