function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
}

function createAlert(message) {
    var alertDiv = $(document.createElement("div"));
    alertDiv.addClass('alert alert-error');
    alertDiv.html(message);
    return alertDiv;
}

function clearFormValidationAlerts(form, reset) {
    if (reset) {
        form.get(0).reset();
    }
    form.find(".alert").remove();
    form.find('input.error').removeData('content');
    form.find('input.error').attr('title', '');
    form.find('input.error').popover('disable');
    form.find('input.error').removeClass('error');
}

function createFormValidationAlerts(form, globalErrors, fieldErrors) {
    clearFormValidationAlerts(form);
    if (globalErrors.length > 0) {
        createAlert(globalErrors.join("<br/>")).prependTo(form).fadeIn();
    }
    else {
        createAlert("Please correct the fields below.").prependTo(form).fadeIn();
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

