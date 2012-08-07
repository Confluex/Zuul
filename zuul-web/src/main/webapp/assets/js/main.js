function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
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

(function ($) {
    var form = null;
    var resourceId = -1;
    var resourceUri = '/';
    var dialog = null;
    var onSave = null;
    var onDelete = null;
    var methods = {
        init:function (options) {
            form = this;
            resourceUri = form.attr("action");
            resourceId = -1;
            dialog = options.dialog;
            onSave = options.onSave;
            onDelete = options.onDelete;
            if (dialog) {
                registerButtonHandlers();
            }
            return this;
        },
        loadResourceById:function (id) {
            resourceId = id;
            $.ajax({
                url:resourceUri + "/" + resourceId + ".json",
                type:'GET',
                success:function (data, status, xhr) {
                    var binder = Binder.FormBinder.bind(form.get(0), data);
                    binder.deserialize();
                }
            });
            return this;
        }
    };


    function registerButtonHandlers() {
        dialog.find('.btn-primary').click(saveRecord);
        dialog.find('.btn-danger').click(deleteRecord);
        dialog.on('hidden', resetForm);
    }

    function saveRecord() {
        var binder = Binder.FormBinder.bind(form.get(0));
        var data = binder.serialize();
        delete data['']; // TODO weird empty key being generated for some reason. Figure out where it's coming from..
        $.ajax({
            url:resourceUri + '/' + resourceId + ".json",
            type:form.attr('method'),
            data:JSON.stringify(data),
            contentType:'application/json',
            success:function (data, status, xhr) {
                dialog.modal('hide');
                if (onSave) {
                    onSave(data)
                }
            },
            statusCode:{
                406:function (xhr, status, error) {
                    var message = 'Some fields on the form were invalid. Please fix them and try again.';
                    createAlert(message, JSON.parse(xhr.responseText));
                }
            },
            error:function (xhr, status, error) {
                createAlert("An error has occurred while saving the record. Please check the log for more details.");
            }
        });
        return false;
    }

    function deleteRecord() {
        $.ajax({
            url:resourceUri + '/' + resourceId + ".json",
            type: 'DELETE',
            success:function (data, status, xhr) {
                dialog.modal('hide');
                if (onDelete) { onDelete() }
            },
            error:function (xhr, status, error) {
                createAlert("An error has occurred while deleting the record. Please check the log for more details.");
            }
        });
    }

    function resetForm() {
        form.get(0).reset();
        form.find('.alert').remove();
        form.find('.control-group').removeClass('error');
        form.find('input').attr('title', '');
    }

    function createAlert(message, errors) {
        var alert = form.find('.alert');
        if (!alert.length) {
            alert = $(document.createElement('div'));
            alert.addClass('alert alert-error').text(message);
            form.prepend(alert);
        }
        else {
            alert.text(message);
        }
        if (errors) {
            for (var i = 0; i < errors.length; i++) {
                var fieldError = errors[i];
                var input = form.find("input[name=" + fieldError.field + "]");
                input.parent().parent().addClass('error');
                input.attr('title', fieldError.error);
                input.tooltip({trigger:'focus', placement:'right'});
            }
        }
    }


    $.fn.jsonForm = function (method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.init.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist on jQuery.jsonForm');
        }
    };

})(jQuery);