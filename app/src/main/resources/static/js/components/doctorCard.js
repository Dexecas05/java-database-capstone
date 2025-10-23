/*
Import the overlay function for booking appointments from loggedPatient.js

  Import the deleteDoctor API function to remove doctors (admin role) from docotrServices.js

  Import function to fetch patient details (used during booking) from patientServices.js

  Function to create and return a DOM element for a single doctor card
    Create the main container for the doctor card
    Retrieve the current user role from localStorage
    Create a div to hold doctor information
    Create and set the doctor’s name
    Create and set the doctor's specialization
    Create and set the doctor's email
    Create and list available appointment times
    Append all info elements to the doctor info container
    Create a container for card action buttons
    === ADMIN ROLE ACTIONS ===
      Create a delete button
      Add click handler for delete button
     Get the admin token from localStorage
        Call API to delete the doctor
        Show result and remove card if successful
      Add delete button to actions container
   
    === PATIENT (NOT LOGGED-IN) ROLE ACTIONS ===
      Create a book now button
      Alert patient to log in before booking
      Add button to actions container
  
    === LOGGED-IN PATIENT ROLE ACTIONS === 
      Create a book now button
      Handle booking logic for logged-in patient   
        Redirect if token not available
        Fetch patient data with token
        Show booking overlay UI with doctor and patient info
      Add button to actions container
   
  Append doctor info and action buttons to the car
  Return the complete doctor card element
*/

// doctorCard.js
// Dynamically creates a reusable doctor profile card with role-specific actions.

// NOTE ON IMPORTS: These service and utility functions are assumed to be exported
// from their respective files and will be implemented in later steps.
import { deleteDoctor } from "../../services/doctorServices.js";
import { getPatientData } from "../../services/patientServices.js";
import { openModal, showBookingOverlay } from "./modals.js";

/**
 * Creates a dynamic HTML card element displaying a doctor's information and
 * role-specific action buttons.
 * * @param {object} doctor - The doctor object containing name, specialty, etc.
 * @returns {HTMLElement} The fully constructed doctor card div.
 */
export function createDoctorCard(doctor) {
    const card = document.createElement("div");
    card.classList.add("doctor-card");

    // Fetch the User’s Role
    const role = localStorage.getItem("userRole");

    // -------------------------------------------------------------------
    // 1. Doctor Info Section
    // -------------------------------------------------------------------
    const infoDiv = document.createElement("div");
    infoDiv.classList.add("doctor-info");

    const name = document.createElement("h3");
    name.textContent = doctor.name || "Doctor Name Missing";

    const specialization = document.createElement("p");
    specialization.classList.add("doctor-specialty");
    specialization.textContent = `Specialty: ${doctor.specialty || 'N/A'}`;

    const email = document.createElement("p");
    email.textContent = `Email: ${doctor.email || 'N/A'}`;

    const availability = document.createElement("p");
    availability.classList.add("doctor-availability");
    // Handle availability which is often an array or similar structure
    const availabilityString = Array.isArray(doctor.availability)
        ? doctor.availability.join(", ")
        : (doctor.availability || "Not specified");
    availability.textContent = `Available: ${availabilityString}`;

    infoDiv.appendChild(name);
    infoDiv.appendChild(specialization);
    infoDiv.appendChild(email);
    infoDiv.appendChild(availability);

    // -------------------------------------------------------------------
    // 2. Button Container
    // -------------------------------------------------------------------
    const actionsDiv = document.createElement("div");
    actionsDiv.classList.add("card-actions");

    // -------------------------------------------------------------------
    // 3. Conditional Buttons
    // -------------------------------------------------------------------

    if (role === "admin") {
        // --- Admin: Delete Button ---
        const removeBtn = document.createElement("button");
        removeBtn.textContent = "Delete";
        removeBtn.classList.add("delete-btn");

        removeBtn.addEventListener("click", async (e) => {
            e.preventDefault();
            // TODO: Replace this logging with a custom confirmation modal later (as alerts/confirms are forbidden).

            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Authentication token missing for delete action.");
                return;
            }

            // In a real application, confirmation UI would be shown here.
            try {
                const success = await deleteDoctor(doctor.id, token);

                if (success) {
                    card.remove(); // Remove the card from the DOM
                    console.log(`Doctor ${doctor.name} deleted successfully.`);
                    // Trigger a custom UI notification (e.g., a toast message)
                } else {
                    console.error(`Failed to delete doctor ${doctor.name}.`);
                }
            } catch (error) {
                console.error("Error during doctor deletion:", error);
            }
        });
        actionsDiv.appendChild(removeBtn);

    } else if (role === "patient") {
        // --- Patient (Not Logged In): Book Now (requires login) ---
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("booking-btn");

        bookNow.addEventListener("click", () => {
            console.warn("Patient needs to login first to book an appointment. Triggering Login Modal.");

            // Trigger the generic login modal
            if (typeof openModal === 'function') {
                openModal('login');
            }
        });
        actionsDiv.appendChild(bookNow);

    } else if (role === "loggedPatient") {
        // --- Logged-in Patient: Book Now (allows immediate booking) ---
        const bookNow = document.createElement("button");
        bookNow.textContent = "Book Now";
        bookNow.classList.add("booking-btn", "book-now-btn");

        bookNow.addEventListener("click", async (e) => {
            const token = localStorage.getItem("token");

            if (!token) {
                console.error("Logged Patient token missing. Redirecting to login/home.");
                // logoutPatient is defined globally in header.js
                window.logoutPatient ? window.logoutPatient() : window.location.href = "/pages/patientDashboard.html";
                return;
            }

            try {
                // Fetch patient data first
                const patientData = await getPatientData(token);

                // Show the booking UI overlay (bottom modal)
                if (typeof showBookingOverlay === 'function') {
                    showBookingOverlay(e, doctor, patientData);
                } else {
                    console.error("showBookingOverlay function is not available.");
                }
            } catch (error) {
                console.error("Error fetching patient data for booking:", error);
            }
        });
        actionsDiv.appendChild(bookNow);
    }

    // -------------------------------------------------------------------
    // 4. Final Assembly
    // -------------------------------------------------------------------
    card.appendChild(infoDiv);

    // Only append actionsDiv if there are actions to show
    if (actionsDiv.childElementCount > 0) {
        card.appendChild(actionsDiv);
    }

    return card;
}
