function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
}

function clearFormValidationAlerts(form) {
    form.find('.alert').remove();
    form.find('input.error').removeData('content');
    form.find('input.error').attr('title', '');
    form.find('input.error').popover('disable');
    form.find('input.error').removeClass('error');
}

function createFormValidationAlerts(form, globalErrors, fieldErrors) {
    clearFormValidationAlerts(form);
    var alertDiv = $(document.createElement('div'));
    alertDiv.addClass('alert alert-error').html("<strong>Validation Errors</strong>");
    if (globalErrors) {
        var messageList = $(document.createElement("ul"));
        for (var i = 0; i < globalErrors.length; i++) {
            var item = $(document.createElement("li"));
            item.text(globalErrors[i]);
            messageList.append(item);
        }
    }
    else {
        alertDiv.text("Please correct the fields below.")
    }
    form.prepend(alertDiv);
    if (fieldErrors) {
        $.each(fieldErrors, function (name, errors) {
            var input = form.find("input[name=" + name + "]");
            if (input.length <= 0) {
                alert("No input by name: " + name);
            }
            input.addClass('error');
            input.attr('title', 'Validation Errors');
            input.data('content', errors.join("<br/>"));
            input.popover({trigger:'focus', placement:'bottom'});
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

