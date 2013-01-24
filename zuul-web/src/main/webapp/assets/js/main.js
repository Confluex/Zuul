function getContextPath() {
    return $('meta[name=contextPath]').attr('content');
}

function createCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    }
    document.cookie = encodeURI(name) + "=" + encodeURI(value) + expires + "; path=/";
}

function createAlert(message) {
    var closeButton = $(document.createElement("button"));
    closeButton.addClass("close");
    closeButton.attr("data-dismiss", "alert");
    closeButton.html("&times;");

    var alertDiv = $(document.createElement("div"));
    alertDiv.addClass('alert alert-error');
    alertDiv.html(message);
    alertDiv.prepend(closeButton);
    return alertDiv;
}

function showAlert(message) {
    var alertDiv = createAlert(message);
    alertDiv.insertAfter("#topNav");
}

function showJsonErrors(xhr, status, error) {
    try {
        var json = $.parseJSON(xhr.responseText);
        var errors = json.messages;
        if (errors[0] == "Unhandled server error") errors.splice(0, 1);
        showAlert(errors.join(". "));

    } catch (e) {
        if (window.console) {
            console.log("Error handling error: " + e);
        }
        alert("An unhandled error has occurred. Please check the log for more details.");
    }
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
                console.log("No input by name: " + name);
            }
            input.addClass('error');
            input.attr('title', 'Validation Errors');
            input.data('content', errors.join("<br/>"));
            input.popover({trigger:'focus', placement:'bottom'}).popover('enable');
        });
    }
}

