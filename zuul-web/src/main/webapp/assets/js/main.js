function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
}

function createAlert(message) {
    var row = $(document.createElement("div"));
    var span = $(document.createElement("div"));
    var alertDiv = $(document.createElement('div'));

    row.addClass("row generated");
    span.addClass("span12");
    alertDiv.addClass('alert alert-error');
    alertDiv.html(message);

    alertDiv.appendTo(span);
    span.appendTo(row);
    row.hide().insertAfter("#topNav").fadeIn();
}

function clearLastAlert() {
    $("#topNav").next(".generated").remove();
}

function clearFormValidationAlerts(form) {
    clearLastAlert();
    form.find('input.error').removeData('content');
    form.find('input.error').attr('title', '');
    form.find('input.error').popover('disable');
    form.find('input.error').removeClass('error');
}

function createFormValidationAlerts(form, globalErrors, fieldErrors) {
    clearFormValidationAlerts(form);
    if (globalErrors.length > 0) {
        createAlert(globalErrors.join("<br/>"));
    }
    else {
        createAlert("Please correct the fields below.");
    }
    if (fieldErrors) {
        $.each(fieldErrors, function (name, errors) {
            var input = form.find("input[name=" + name + "]");
            if (input.length <= 0) {
                alert("No input by name: " + name);
            }
            input.addClass('error');
            input.attr('title', 'Validation Errors');
            input.data('content', errors.join("<br/>"));
            input.popover({trigger:'focus', placement:'bottom'}).popover('enable');
        });
    }
}

(function ($) {
    $.fn.settingsMenu = function (options) {
        var settings = $.extend({  nothing:'' }, options);
        var subMenu = this;
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

