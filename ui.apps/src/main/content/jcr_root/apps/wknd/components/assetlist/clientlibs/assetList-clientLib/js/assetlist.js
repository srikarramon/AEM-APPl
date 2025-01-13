function filterAssets() {
    //alert("hii");
    // Get the selected filter criteria (Name, Modified By, or Status)
    const filterCriteria = document.getElementById("filterCriteria").value;
    
    // Get the entered filter value (convert to lowercase for case-insensitive comparison)
    const filterValue = document.getElementById("filterValue").value.toLowerCase();
    
    // Get all the rows in the asset table body
    const rows = document.querySelectorAll("#assetTableBody tr");

    // Loop through each row to check if it matches the filter
    rows.forEach(row => {
        const cells = row.getElementsByTagName("td");
        let match = false;

        // Check the selected filter criteria and match it with the value entered
        switch (filterCriteria) {
            case "name":
                match = cells[0].textContent.toLowerCase().includes(filterValue); // Name column
                break;
            case "modifiedBy":
                match = cells[3].textContent.toLowerCase().includes(filterValue); // Modified By column
                break;
            case "status":
                match = cells[5].textContent.toLowerCase().includes(filterValue); // Status column
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