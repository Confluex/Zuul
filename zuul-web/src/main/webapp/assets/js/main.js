(function ($) {
    $.fn.settingsMenu = function (options) {
        var settings = $.extend({  context:'/' }, options);
        var menuItem = this;
        $.ajax({
            url:settings.context + "/settings.json",
            success:function (data) {
                var fileNames = distinctGroupNames(data);
                var subMenu = $(document.createElement('ul'));
                subMenu.addClass('dropdown-menu');
                menuItem.append(subMenu);
                for (var i = 0; i < fileNames.length; i++) {
                    var name = fileNames[i];
                    var link = $(document.createElement('a'));
                    link.text(name);
                    link.attr('href', settings.context + "/settings/" + encodeURI(name));
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

    $.fn.jsonForm = function (options) {
        var settings = $.extend({  dialog:null, resourceId:-1 }, options);
        var form = this;
        var resourceUri = this.attr("action");
        loadFormFromJson();
        if (settings.dialog) {
            registerButtonHandlers();
        }
        function loadFormFromJson() {
            $.ajax({
                url:resourceUri + "/" + settings.resourceId + ".json",
                type:'GET',
                success:function (data, status, xhr) {
                    var binder = Binder.FormBinder.bind(form.get(0), data);
                    binder.deserialize();
                }
            });
        }

        function registerButtonHandlers() {
            settings.dialog.find('.btn-primary').click(saveRecord);
            settings.dialog.find('.btn-danger').click(deleteRecord);
            settings.dialog.on('hidden', resetForm);
        }

        function saveRecord() {
            var binder = Binder.FormBinder.bind(form.get(0));
            var data = binder.serialize();
            $.ajax({
                url:resourceUri + '/' + settings.resourceId + ".json",
                type:form.attr('method'),
                data:JSON.stringify(data),
                success:function (data, status, xhr) {
                    settings.dialog.modal('hide');
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
            alert("TODO: deleteRecord");
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

        return this;
    };


})(jQuery);
