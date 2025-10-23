/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

// adminDashboard.js
// Handles core logic for the Admin dashboard: loading, filtering, and adding new doctor profiles.

// --- Imports ---
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../components/doctorCard.js";

const contentDiv = document.getElementById("content");

// --- Utility Functions ---

/**
 * Renders the provided list of doctor objects into the content area.
 * @param {Array<Object>} doctors - Array of doctor objects.
 */
function renderDoctorCards(doctors) {
    if (!contentDiv) return;

    // Clear existing content
    contentDiv.innerHTML = "";

    if (doctors && doctors.length > 0) {
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } else {
        // Display a message if no doctors are found
        contentDiv.innerHTML = `
            <div class="no-results">
                <h2>No doctors found matching your criteria.</h2>
                <p>Try broadening your search or filter options.</p>
            </div>`;
    }
}

/**
 * Fetches all doctors and renders them on the dashboard.
 */
async function loadDoctorCards() {
    // Show a loading state while fetching
    if (contentDiv) {
        contentDiv.innerHTML = `<div class="loading-spinner"></div>`;
    }

    const doctors = await getDoctors();
    renderDoctorCards(doctors);
}


// --- Search and Filter Logic ---

/**
 * Handles the change event for search bar and filter dropdowns.
 * Fetches and renders filtered results.
 */
async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar").value;
    const time = document.getElementById("filterTime").value;
    const specialty = document.getElementById("filterSpecialty").value;

    // Check for a minimum length before filtering on name to optimize API calls
    if (name.length > 0 && name.length < 3) {
         // Optionally wait for more input or show current list
         return;
    }

    const filteredList = await filterDoctors(name, time, specialty);
    renderDoctorCards(filteredList);
}


// --- Add Doctor Logic (Global Handler) ---

/**
 * Handles the form submission for adding a new doctor via modal.
 * This function needs to be global so the dynamically created modal form can call it.
 * @param {Event} e - The form submission event.
 */
async function adminAddDoctor(e) {
    e.preventDefault();

    const token = localStorage.getItem("token");
    if (!token) {
        console.error("Admin token not found. Session may be expired.");
        // In a future step, replace this alert with a custom message box UI
        alert("Session expired or invalid login. Please log in again.");
        window.location.href = "/";
        return;
    }

    // Collect data from the form fields dynamically placed in the modal
    const name = document.getElementById('newDocName').value;
    const specialty = document.getElementById('newDocSpecialty').value;
    const email = document.getElementById('newDocEmail').value;
    const password = document.getElementById('newDocPassword').value;
    const mobile = document.getElementById('newDocMobile').value;

    // Collect availability checkboxes (assuming IDs like 'avail_mon_am', 'avail_mon_pm', etc.)
    const availabilityCheckboxes = document.querySelectorAll('#addDoctorForm input[type="checkbox"]:checked');
    const availability = Array.from(availabilityCheckboxes).map(cb => cb.value);

    const newDoctor = {
        name,
        specialty,
        email,
        password,
        mobile,
        availability // Array of time slots
    };

    const result = await saveDoctor(newDoctor, token);

    if (result.success) {
        // In a future step, replace this alert with a custom success message box UI
        alert(result.message);

        // Close modal
        const modal = document.getElementById('modal');
        if (modal) modal.classList.add('hidden');

        // Refresh the list of doctors on the dashboard
        loadDoctorCards();

    } else {
        // In a future step, replace this alert with a custom error message box UI
        alert("Error adding doctor: " + result.message);
    }
}

// Make the function globally accessible for the modal form to submit to
window.adminAddDoctor = adminAddDoctor;


// --- Event Listeners and Initial Load ---

window.addEventListener('DOMContentLoaded', () => {
    // 1. Initial Data Load
    loadDoctorCards();

    // 2. Filter Listeners
    const searchBar = document.getElementById("searchBar");
    const filterTime = document.getElementById("filterTime");
    const filterSpecialty = document.getElementById("filterSpecialty");

    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }
    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }
    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }

    // 3. Add Doctor Modal Trigger
    const addDocBtn = document.getElementById('addDocBtn');
    if (addDocBtn) {
        // The header component should have already attached the listener,
        // but we ensure it works here in case it was missed or for direct use.
        addDocBtn.onclick = null; // Clear potential old listeners
        addDocBtn.addEventListener('click', () => {
            openModal('addDoctor');
        });
    }
});

