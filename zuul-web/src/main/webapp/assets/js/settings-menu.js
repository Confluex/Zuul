(function ($) {

    var settings = {
        max: 15
    };
    var subMenu = null;
    var methods = {
        init: function (options) {
            settings = $.extend(settings, options);
            subMenu = this;
            $.ajax({
                url: getContextPath() + "/settings/menu.json",
                success: function (data) {
                    methods.createMenu(data);
                    methods.createSearch(data);

                }
            });

            return this;
        },
        createSearch: function (data) {
            var leafs = $.map(data, function(node) {
               if ($.isArray(node.leafs)) {
                   return $.map(node.leafs, function(leaf) {
                       return leaf.resourceUri;
                   })
               } else {
                   return node.resourceUri;
               }
            });
            $("#navSearchInput").typeahead({
                minLength: 3, source: leafs,
                updater: function (selected) { document.location = selected }
            });
        },
        createMenu: function (data) {
            for (var i = 0; i < data.length && i < settings.max; i++) {
                var entry = data[i];
                if (entry.leafs) {
                    methods.createMenuNode(entry.name, entry.leafs, subMenu)
                }
                else {
                    methods.createMenuItem(entry.name, entry.resourceUri, subMenu);
                }
            }
            if (data.length > settings.max) {
                methods.createMenuItem("More..", getContextPath() + "/settings");
            }
        },
        createMenuItem: function (name, url, container) {
            var item = $(document.createElement('li'));
            var link = $(document.createElement('a')).attr('href', url).addClass("leaf").text(name).appendTo(item);
            item.appendTo(container);
        },
        createMenuNode: function (name, leafs, container) {
            var item = $(document.createElement("li")).addClass("dropdown-submenu");
            var link = $(document.createElement('a')).attr('href', "#").text(name).appendTo(item);
            var list = $(document.createElement("ul")).addClass("dropdown-menu").appendTo(item);
            for (var i = 0; i < leafs.length; i++) {
                var leaf = leafs[i];
                methods.createMenuItem(leaf.name, leaf.resourceUri, list);
            }
            item.appendTo(container);
        }
    };

    $.fn.settingsMenu = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.settingsMenu');
            return false;
        }
    };

})(jQuery);
