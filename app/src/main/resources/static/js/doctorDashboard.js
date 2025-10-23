/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

// doctorDashboard.js
// Handles core logic for the Doctor dashboard: fetching, filtering, and displaying patient appointments.

// --- Imports ---
import { getAllAppointments } from "../services/appointmentRecordService.js";
import { createPatientRow } from "../components/patientRows.js";


// --- Global Variables and Initialization ---

// Helper function to get today's date in YYYY-MM-DD format for date inputs
const getTodayDateString = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

// Elements
const patientTableBody = document.getElementById("patientTableBody");
const datePicker = document.getElementById("datePicker");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");

// State
let selectedDate = getTodayDateString();
let token = localStorage.getItem("token");
let userId = localStorage.getItem("userId"); // Assuming the doctor's ID is stored here
let patientName = null;


// --- Main Rendering Logic ---

/**
 * Renders the table body content, either showing appointment rows or a message.
 * @param {string} contentHtml - The HTML string to set in the table body.
 */
function renderTableContent(contentHtml) {
    if (patientTableBody) {
        patientTableBody.innerHTML = contentHtml;
    }
}

/**
 * Fetches appointments based on current state (date, name filter) and renders the table.
 */
async function loadAppointments() {
    if (!token || !userId) {
        console.error("Authentication required: Token or User ID missing.");
        // Redirect logic should be handled by header.js, but a fallback message is useful
        renderTableContent(`<tr><td colspan="5" class="noPatientRecord">Authentication failed. Please log in again.</td></tr>`);
        return;
    }

    try {
        // Show loading state
        renderTableContent(`<tr><td colspan="5" class="noPatientRecord">Loading appointments...</td></tr>`);

        // Fetch data
        const appointments = await getAllAppointments(selectedDate, patientName, token, userId);

        // Clear table content before rendering results
        renderTableContent("");

        if (appointments && appointments.length > 0) {
            appointments.forEach(appointment => {
                // The appointment object must contain patient details (patientID, name, contact info)
                const row = createPatientRow(appointment);
                patientTableBody.appendChild(row);
            });
        } else {
            // No appointments found
            renderTableContent(`<tr><td colspan="5" class="noPatientRecord">No appointments found for ${selectedDate}.</td></tr>`);
        }
    } catch (error) {
        console.error("Error loading appointments:", error);
        renderTableContent(`<tr><td colspan="5" class="noPatientRecord">An error occurred while fetching appointments.</td></tr>`);
    }
}


// --- Event Handlers ---

/**
 * Handles input on the search bar (filtering by patient name).
 */
function handleSearchInput() {
    patientName = searchBar.value.trim() || null;
    loadAppointments();
}

/**
 * Resets the selected date to today and refreshes the list.
 */
function handleTodayButtonClick() {
    selectedDate = getTodayDateString();
    if (datePicker) {
        datePicker.value = selectedDate;
    }
    loadAppointments();
}

/**
 * Handles the change event from the date picker.
 */
function handleDatePickerChange() {
    if (datePicker) {
        selectedDate = datePicker.value;
        loadAppointments();
    }
}


// --- Initialization ---

window.addEventListener('DOMContentLoaded', () => {
    // 1. Initialize Date Picker
    if (datePicker) {
        datePicker.value = selectedDate; // Set default to today
        datePicker.addEventListener('change', handleDatePickerChange);
    }

    // 2. Set up Button Listeners
    if (todayButton) {
        todayButton.addEventListener('click', handleTodayButtonClick);
    }
    if (searchBar) {
        searchBar.addEventListener('input', handleSearchInput);
    }

    // 3. Initial Load of Appointments (for today by default)
    loadAppointments();
});
