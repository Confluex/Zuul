(function ($) {
    var form = null;
    var resourceId = -1;
    var resourceUri = '/';
    var dialog = null;
    var onSave = null;
    var onDelete = null;
    var onError = function(xhr, status, error) {
        try {
            var json = $.parseJSON(xhr.responseText);
            switch (xhr.status) {
                case 422:
                    createFormValidationAlerts(form, json.messages, json.fieldMessages);
                    break;
                default:
                    var errors = json.messages;
                    if (errors[0] == "Unhandled server error") errors.splice(0, 1);
                    createAlert(errors.join(".")).prependTo(form).fadeIn();

            }
        } catch (e) {
            if (window.console) {
                console.log("Error handling error: " + e);
            }
            alert("An unhandled error has occurred. Please check the log for more details.");
        }
    };

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
                url:resourceUri + "/" + encodeURI(resourceId) + ".json",
                type:'GET',
                contentType:'application/json',
                success:function (data, status, xhr) {
                    var binder = Binder.FormBinder.bind(form.get(0), data);
                    binder.deserialize();
                },
                error: onError
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
        var method = form.data('save-method');
        delete data['']; // TODO weird empty key being generated for some reason. Figure out where it's coming from..
        $.ajax({
            url:resourceUri + '/' + encodeURI(resourceId) + ".json",
            type: method ? method : form.attr('method'),
            data:JSON.stringify(data),
            contentType:'application/json',
            success:function (data, status, xhr) {
                dialog.modal('hide');
                if (onSave) {
                    onSave(data)
                }
            },
            error:onError
        });
        return false;
    }

    function deleteRecord() {
        $.ajax({
            url:resourceUri + '/' + encodeURI(resourceId) + ".json",
            type:'DELETE',
            contentType:'application/json',
            success:function (data, status, xhr) {
                dialog.modal('hide');
                if (onDelete) {
                    onDelete()
                }
            },
            error:onError
        });
    }

    function resetForm() {
        clearFormValidationAlerts(form, true);
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