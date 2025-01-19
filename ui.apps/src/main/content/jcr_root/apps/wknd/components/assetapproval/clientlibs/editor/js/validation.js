(function ($, Coral) {
    "use strict";
    const errorMessageHTML = `
        <div class="container-error-message" style="color: red; font-size: 14px; margin-top: 10px; text-align: center;">
            Duplicate paths detected! Please resolve the duplicates to proceed.
        </div>
    `;

     $(document).on("click", "coral-dialog-footer button.cq-dialog-submit", function (e)  {
        // Locate the multifield with the custom-validation attribute
       const multifield = $("coral-multifield").has("[data-custom-validation='checkDuplicatePaths']");

       if (multifield.length > 0) {
            // Get all input fields within the multifield items
          const inputs = multifield.find("coral-multifield-item foundation-autocomplete input[is='coral-textfield']");
            const values = [];
            let hasDuplicates = false;
            let emptyfield = false;
            // Remove existing error styles
            inputs.css("border", "");
            multifield.find(".container-error-message").remove();

            // Check for duplicate values
            inputs.each(function () {
                const value = $(this).val();
                if(value == "") {
                    emptyfield = true;
                    $(this).css("border", "1px solid red");
                    multifield.append(errorMessageHTML);
                    multifield.find(".container-error-message").text("Field cannot be empty");
                }
                if (value && values.includes(value)) {
                    hasDuplicates = true;
                     $(this).css("border", "1px solid red");
                       if (!$(this).next(".error-message").length) {
                        multifield.find(".container-error-message").remove();
                      multifield.append(errorMessageHTML);
                    }
                } else if (value) {
                    values.push(value);
                }
            });

            // Display error if duplicates are found
            if (hasDuplicates || emptyfield) {
                // Prevent dialog from being saved
                e.preventDefault(); // Stops the dialog submission
                return false;
            } else {
                console.log("No duplicates found. Validation passed!");
            }
        } else {
            console.log("Multifield not found. Check the selector or attributes.");
        }
    });
})(jQuery, Coral);