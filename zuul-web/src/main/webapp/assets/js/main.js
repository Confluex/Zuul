(function ($) {
    $.fn.settingsMenu = function (options) {
        var settings = $.extend({  url:'/' }, options);
        var menuItem = this;
        $.ajax({
            url:settings.url,
            success:function (data) {
                var subMenu = $(document.createElement('ul'));
                subMenu.addClass('dropdown-menu');
                menuItem.append(subMenu);
                for (var i = 0; i < data.length; i++) {
                    var env = data[i];
                    var envLink = $(document.createElement('a'));
                    envLink.text(env.name);
                    envLink.attr('href', '#');
                    var envMenuItem = $(document.createElement('li'));
                    envMenuItem.append(envLink);
                    subMenu.append(envMenuItem);
                }
            }
        });
        return this;
    };
})(jQuery);