// Initialize the variable at the top of the file
let lastCheckedHeaderCheckbox = null;

document.addEventListener('DOMContentLoaded', () => {
    const rows = document.querySelectorAll('.asset-row');

    rows.forEach(row => {
        const status = row.dataset.status;
        const approveButton = row.querySelector('.approve-btn');
        const approveButtonES = row.querySelector('.asset-approve-reject-es .approve-btn');  // ES approve button
        const approveButtonEN = row.querySelector('.asset-approve-reject-en .approve-btn');  // EN approve button

        const readyButtonES = row.querySelector('.asset-ready-es .ready-btn-es');  // ES ready button
        const readyButtonEN = row.querySelector('.asset-ready-en .ready-btn-en');  // EN ready button

        // Initially disable the Ready buttons
        if (readyButtonES) readyButtonES.disabled = true;
        if (readyButtonEN) readyButtonEN.disabled = true;

        const esApproved = row.dataset.esapproved;
        const enApproved = row.dataset.enapproved;

        const lastModifiedDate = row.dataset.lastmodified || "N/A"; 
        const lastApprovedDate = row.dataset.lastapproved; 

        const readySpanES = row.querySelector('.asset-ready-es span');  // ES Ready span
        const readySpanEN = row.querySelector('.asset-ready-en span');  // EN Ready span

        const isEnReady = row.dataset.enReady === "true"; // EN ready status
        const isEsReady = row.dataset.esReady === "true"; // ES ready status

        console.log(esApproved);
        console.log(enApproved);
        const readyCellEN = row.querySelector('.asset-ready-en');
        const redyButtonEN = readyCellEN.querySelector('.approve-btn'); // EN approve button
        //const readySpanEN = readyCellEN.querySelector('span'); // EN ready span

        if (esApproved.trim().toLowerCase() === 'certified' && approveButtonES) {
            console.log("inside es approved");
            approveButtonES.disabled = true;
            approveButtonES.classList.add('disabled-button');
            if (readyButtonES) {
                readyButtonES.disabled = false;
                readyButtonES.classList.remove('disabled-button');
            }
        }

        // Disable the ES approve button if it has been certified
        if (enApproved === 'certified' && approveButtonEN) {
            approveButtonEN.disabled = true;
            approveButtonEN.classList.add('disabled-button');

             if (readyButtonEN) {
                readyButtonEN.disabled = false;
                readyButtonEN.classList.remove('disabled-button');
            }
        }
       if (isEnReady) {
            if (readyButtonEN) {
                readyButtonEN.style.display = 'none'; // Hide the EN Ready button
            }
            if (readySpanEN) {
                readySpanEN.style.display = 'inline'; // Show the EN Ready span
            }
        }

        // Handle ES ready status
        if (isEsReady) {
            if (readyButtonES) {
                readyButtonES.style.display = 'none'; // Hide the ES Ready button
            }
            if (readySpanES) {
                readySpanES.style.display = 'inline'; // Show the ES Ready span
            }
        }
    });
});

function filterAssets() {

    const filterCriteria = document.getElementById("filterCriteria").value;
    const filterValue = document.getElementById("filterValue").value.toLowerCase();
    
    // Get all the rows in the asset table body
    const rows = document.querySelectorAll("#assetTableBody tr");
    //const rows = document.querySelectorAll("#assetTableBody .asset-row");  
    const now = new Date(); // Current date

    // Loop through each row to check if it matches the filter
    rows.forEach(row => {
       // const assetName = row.querySelector(".asset-name").textContent.toLowerCase();
        const cells = row.getElementsByTagName("td");
        let match = false;
        const pathCell = row.querySelector(".asset-path");
        //const path = pathCell ? pathCell.textContent.trim() : "";
        let isVisible = false;
        const lastModifiedCell = row.querySelector(".asset-last-modified");
        const path = pathCell ? pathCell.textContent.trim() : "";
        const lastModified = lastModifiedCell ? lastModifiedCell.textContent.trim() : "";


        // Check the selected filter criteria and match it with the value entered
        switch (filterCriteria) {
             case "encertified":
                // Check if the EN Certified button is disabled
                const enCertifiedButton = row.querySelector(".asset-approve-reject-en .approve-btn");
                match = enCertifiedButton && enCertifiedButton.classList.contains("disabled-button") && enCertifiedButton.hasAttribute("disabled");
                break;

            case "escertified":
                // Check if the ES Certified button is disabled
                const esCertifiedButton = row.querySelector(".asset-approve-reject-es .approve-btn");
                match = esCertifiedButton && esCertifiedButton.classList.contains("disabled-button") && esCertifiedButton.hasAttribute("disabled");
                break;

            case "enready":
                // Check if EN Ready button is hidden and span is visible
                const enReadyButton = row.querySelector(".asset-ready-en .ready-btn-en");
                const enReadySpan = row.querySelector(".asset-ready-en span");
                match = enReadyButton && enReadySpan &&
                        enReadyButton.style.display === "none" &&
                        enReadySpan.style.display === "inline";
                break;

            case "esready":
                // Check if ES Ready button is hidden and span is visible
                const esReadyButton = row.querySelector(".asset-ready-es .ready-btn-es");
                const esReadySpan = row.querySelector(".asset-ready-es span");
                match = esReadyButton && esReadySpan &&
                        esReadyButton.style.display === "none" &&
                        esReadySpan.style.display === "inline";
                break;

            case "name":
                match = cells[0].textContent.toLowerCase().includes(filterValue); // Name column
                break;
            case "modifiedBy":
                match = cells[3].textContent.toLowerCase().includes(filterValue); // Modified By column
                break;
            case "status":
                match = cells[5].textContent.toLowerCase().includes(filterValue); // Status column
                break;
            case "images":
                // Check for image file extensions
                match = /\.(jpeg|jpg|png|svg)$/i.test(assetName);
                break;

            case "pdfs":
                // Check for PDF files
                match = /\.pdf$/i.test(assetName);
                break;

            case "jsonFiles":
                // Check for JSON files
                match = /\.json$/i.test(assetName);
                break;
            case "contentFragments":
               // match = !/\.\w+$/i.test(path); // No extension, likely a content fragment
               if (!/\.\w+$/i.test(path)) { // No file extension
                    const modifiedDate = parseISODate(lastModified);
                    if (modifiedDate) {
                        const timeDiff = now - modifiedDate;
                        const daysDiff = timeDiff / (1000 * 60 * 60 * 24); // Convert ms to days
                        match = daysDiff <= 30; // Modified within the last 30 days
                    }
                }
                break;

            default:
                match = false;
        }

        // Toggle row visibility based on match
        if (match) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

function sortTable(columnIndex, iconElement) {
    const table = document.getElementById("assetTable");
    const tbody = table.querySelector("tbody");
    const rows = Array.from(tbody.querySelectorAll("tr"));
    const isAscending = iconElement.classList.contains("ascending");

    // Remove sorting classes from all other icons
    const icons = table.querySelectorAll(".filter-icon");
    icons.forEach(icon => icon.classList.remove("ascending", "descending"));

    // Apply sorting logic
    rows.sort((rowA, rowB) => {
        const cellA = rowA.children[columnIndex].innerText.trim();
        const cellB = rowB.children[columnIndex].innerText.trim();

        if (isAscending) {
            return cellA > cellB ? -1 : cellA < cellB ? 1 : 0; // Descending
        } else {
            return cellA < cellB ? -1 : cellA > cellB ? 1 : 0; // Ascending
        }
    });

    // Update the class for the clicked icon
    iconElement.classList.toggle("ascending", !isAscending);
    iconElement.classList.toggle("descending", isAscending);

    // Append sorted rows back to the table body
    rows.forEach(row => tbody.appendChild(row));
}

function updateRowCount() {
    const rowCount = document.querySelectorAll('#assetTableBody .asset-row').length;
    document.getElementById('rowCount').textContent = `Total Rows: ${rowCount}`;
}

function clearFilters() {
    // Reset the filter dropdown
    document.getElementById("filterCriteria").value = "name";

    // Clear the filter input
    document.getElementById("filterValue").value = "";

    // Trigger the filterAssets function to reset the table
    filterAssets();
}

// Utility function to parse ISO date strings
function parseISODate(isoDateStr) {
    try {
        return new Date(isoDateStr);
    } catch (error) {
        console.error("Error parsing date:", isoDateStr);
        return null;
    }
}

function updateStatus(button, status) {
    // Get the parent row of the button
    const row = button.closest("tr");
    // Ensure the row exists
    if (!row) {
        console.error("Row not found for the button clicked.");
        return;
    }
    // Retrieve the data-path attribute from the row
    const path = row.getAttribute("data-path");
    if (!path) {
        console.error("Path is empty or undefined.");
        return;
    }
    // Log the path for debugging
    console.log("Path:", path);

    // Update the status in the appropriate cell
    const column = button.dataset.column || "unknown"; // Determine which column (EN/ES) button is clicked
    const statusCell = row.querySelector(`.asset-status[data-column="${column}"]`); // Assuming unique data attributes per column
    if (statusCell) {
        statusCell.textContent = status.charAt(0).toUpperCase() + status.slice(1);
    }

if (status === 'en-certified' || status === 'es-certified') {
        // Get the ready button for the corresponding language
        const readyButton = status === 'en-certified' ?
            row.querySelector('.asset-ready-en .ready-btn-en') :
            row.querySelector('.asset-ready-es .ready-btn-es');
        
        // Disable the certify button
        button.disabled = true;
        button.classList.add('disabled-button');

        // Enable the corresponding ready button
        if (readyButton) {
            readyButton.disabled = false;
            readyButton.classList.remove('disabled-button');
        }
    }
    // Call backend function to save the status
    saveStatusToBackend(button, path, status);
}

function saveStatusToBackend(button, path, status) {
    console.log("Preparing to save status for:", path, "with status:", status);

    // Fetch CSRF token (if required)
    fetch("/libs/granite/csrf/token.json")
        .then(response => response.json())
        .then(data => {
            const csrfToken = data.token;

            // Construct the query string for GET request
            const queryString = new URLSearchParams({
                path: path,
                status: status
            }).toString();

            // Make the GET request to the servlet
            fetch(`/bin/saveAssetStatus?${queryString}`, {
                method: "GET",
                headers: {
                    "CSRF-Token": csrfToken // Optional for GET, depending on your server's requirements
                }
            })
                .then(response => {
                    if (!response.ok) {
                        console.error("Response status:", response.status, response.statusText);
                        throw new Error("Failed to save status");
                    }
                    return response.text();
                })
                .then(data => {
                    console.log("Status saved successfully:", data);

                    // Disable the clicked button
                    button.disabled = true;
                    button.classList.add("disabled-button");

                    // Disable the reject button in the same row
                    const row = button.closest("tr");
                    const rejectButton = row.querySelector(".reject-btn");
                    if (rejectButton) {
                        rejectButton.disabled = true;
                        rejectButton.classList.add("disabled-button");
                    }

                    alert(`Status for asset ${path} has been updated to: ${status}`);
                })
                .catch(error => {
                    console.error("Error saving status:", error);
                    alert("Failed to save status. Please try again.");
                });
        })
        .catch(error => {
            console.error("Failed to fetch CSRF token:", error);
        });
}

function markReady(button, status) {
    // Get the parent row of the button
    const row = button.closest("tr");

    // Determine the target column and buttons based on the status
    const targetColumnClass = status === "enReady" ? ".asset-ready-en" : ".asset-ready-es";
    const otherColumnClass = status === "enReady" ? ".asset-ready-es" : ".asset-ready-en";
     // Target column where the button was clicked
    const targetColumn = row.querySelector(targetColumnClass);
    const otherColumn = row.querySelector(otherColumnClass);
    if (targetColumn) {
        // Hide the approve button in the target column
        const approveButton = targetColumn.querySelector("button");
        if (approveButton) {
            approveButton.style.display = "none"; // Hide the button
        }

        // Display "Ready" text in the target column
        let readyText = targetColumn.querySelector("span");
        if (readyText) {
            readyText.style.display = "inline"; // Show the "Ready" span
        }
    }



    // Retrieve the asset path
    const path = row.getAttribute("data-path");

    // Log for debugging
    console.log(`Marking asset as ready: Path = ${path}, Status = ${status}`);

    // Call the servlet to update the status
    fetch("/bin/markAssetReady", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: new URLSearchParams({
            path: path, 
            status: status // Dynamically set the status (enReady or esReady)
        })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to update asset ready status.");
            }
            return response.text();
        })
        .then(data => {
            console.log("Ready status updated successfully:", data);
        })
        .catch(error => {
            console.error("Error updating ready status:", error);
            alert("Failed to mark the asset as ready. Please try again.");
        });
}


function selectNonCertifiedENESRows(checkbox, certifyType) {
    clearFilters();
    // Get whether the checkbox is checked
    const isChecked = checkbox.checked;

    // Find all rows in the table
    const rows = document.querySelectorAll('.asset-row');

     // Get all header checkboxes
    const headerCheckboxes = document.querySelectorAll('.bulk-select-checkbox');

    // Uncheck all checkboxes except the current one
    headerCheckboxes.forEach(headerCheckbox => {
        if (headerCheckbox !== checkbox) {
            headerCheckbox.checked = false;
        }
    });

    rows.forEach(row => {
        let certifyButton;
        let contentRecordCheckbox;

        // Based on certifyType, select the correct column
        if (certifyType === 'encertify') {
            certifyButton = row.querySelector('.asset-approve-reject-en .approve-btn');
            contentRecordCheckbox = row.querySelector('.asset-checkbox-input');
        } else if (certifyType === 'escertify') {
            certifyButton = row.querySelector('.asset-approve-reject-es .approve-btn');
            contentRecordCheckbox = row.querySelector('.asset-checkbox-input');
        }

        if (certifyButton && !certifyButton.disabled) {
            // For EN Certify or ES Certify, if the button is not disabled, show the row and check the checkbox
            row.style.display = isChecked ? '' : '';
            if (contentRecordCheckbox) {
                contentRecordCheckbox.checked = isChecked;
            }
        } else {
            // Hide the row if the button is disabled and uncheck the checkbox
            row.style.display = isChecked ? 'none' : '';
            if (contentRecordCheckbox) {
                contentRecordCheckbox.checked = false;
            }
        }
    });
}


function selectReadyRows(checkbox, readyType) {
    // Get whether the checkbox is checked
    const isChecked = checkbox.checked;

    // Find all rows in the table
    const rows = document.querySelectorAll('.asset-row');

     // Get all header checkboxes
    const headerCheckboxes = document.querySelectorAll('.bulk-select-checkbox');

    // Uncheck all checkboxes except the current one
    headerCheckboxes.forEach(headerCheckbox => {
        if (headerCheckbox !== checkbox) {
            headerCheckbox.checked = false;
        }
    });

    rows.forEach(row => {
        let readyButton;
        let contentRecordCheckbox;

        // Based on readyType, select the correct column
        if (readyType === 'enready') {
            readyButton = row.querySelector('.asset-ready-en .ready-btn-en');
            contentRecordCheckbox = row.querySelector('.asset-checkbox-input');
        } else if (readyType === 'esready') {
            readyButton = row.querySelector('.asset-ready-es .ready-btn-es');
            contentRecordCheckbox = row.querySelector('.asset-checkbox-input');
        }

        if (readyButton && !readyButton.disabled && window.getComputedStyle(readyButton).display !== 'none') {
            // For EN Ready or ES Ready, if the button is not disabled, show the row and check the checkbox
            row.style.display = isChecked ? '' : '';
            if (contentRecordCheckbox) {
                contentRecordCheckbox.checked = isChecked;
            }
        } else {
            // Hide the row if the button is disabled and uncheck the checkbox
            row.style.display = isChecked ? 'none' : '';
            if (contentRecordCheckbox) {
                contentRecordCheckbox.checked = false;
            }
        }
    });
}

function uncertify(button, status) {
    // Get the current row containing the clicked button
    const row = button.closest('tr');

    if (row) {
        // Find the "EN Ready" button in the same row
        const enReadyButton = row.querySelector('.ready-btn-en');
        
        // Enable the "EN Ready" button
        if (enReadyButton) {
            enReadyButton.disabled = false;
            enReadyButton.classList.remove('disabled');
            console.log(`EN Ready button enabled for row with status: ${status}`);
        } else {
            console.error('EN Ready button not found in the row.');
        }
    } else {
        console.error('Could not find the parent row for the clicked button.');
    }
}