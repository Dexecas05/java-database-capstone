/*
  Import the openModal function to handle showing login popups/modals
  Import the base API URL from the config file
  Define constants for the admin and doctor login API endpoints using the base URL

  Use the window.onload event to ensure DOM elements are available after page load
  Inside this function:
    - Select the "adminLogin" and "doctorLogin" buttons using getElementById
    - If the admin login button exists:
        - Add a click event listener that calls openModal('adminLogin') to show the admin login modal
    - If the doctor login button exists:
        - Add a click event listener that calls openModal('doctorLogin') to show the doctor login modal


  Define a function named adminLoginHandler on the global window object
  This function will be triggered when the admin submits their login credentials

  Step 1: Get the entered username and password from the input fields
  Step 2: Create an admin object with these credentials

  Step 3: Use fetch() to send a POST request to the ADMIN_API endpoint
    - Set method to POST
    - Add headers with 'Content-Type: application/json'
    - Convert the admin object to JSON and send in the body

  Step 4: If the response is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('admin') to proceed with admin-specific behavior

  Step 5: If login fails or credentials are invalid:
    - Show an alert with an error message

  Step 6: Wrap everything in a try-catch to handle network or server errors
    - Show a generic error message if something goes wrong


  Define a function named doctorLoginHandler on the global window object
  This function will be triggered when a doctor submits their login credentials

  Step 1: Get the entered email and password from the input fields
  Step 2: Create a doctor object with these credentials

  Step 3: Use fetch() to send a POST request to the DOCTOR_API endpoint
    - Include headers and request body similar to admin login

  Step 4: If login is successful:
    - Parse the JSON response to get the token
    - Store the token in localStorage
    - Call selectRole('doctor') to proceed with doctor-specific behavior

  Step 5: If login fails:
    - Show an alert for invalid credentials

  Step 6: Wrap in a try-catch block to handle errors gracefully
    - Log the error to the console
    - Show a generic error message
*/

// index.js
// Handles role selection on the landing page, and authentication requests for Admin and Doctor roles.

// --- Imports ---
// openModal is used to dynamically open the correct login form UI.
// API_BASE_URL is the root endpoint for all backend calls.
import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";

// --- API Endpoints ---
const ADMIN_API = API_BASE_URL + '/admin/login';
const DOCTOR_API = API_BASE_URL + '/doctor/login';

// --- Global Helper Function: Post-Login Handler ---

/**
 * Helper function called after successful authentication.
 * Stores the user role and token, then redirects to the appropriate dashboard.
 * @param {string} role - The user's confirmed role ("admin" or "doctor").
 * @param {string} token - The JWT received from the backend.
 */
function selectRole(role, token) {
    if (token) {
        localStorage.setItem("token", token);
    }
    localStorage.setItem("userRole", role);

    // Redirect to the respective dashboard
    let redirectPath = "/";
    if (role === "admin") {
        redirectPath = "/templates/admin/adminDashboard.html";
    } else if (role === "doctor") {
        redirectPath = "/templates/doctor/doctorDashboard.html";
    }

    // Clear the modal and redirect
    const modal = document.getElementById("modal");
    if (modal) modal.classList.add("hidden");

    window.location.href = redirectPath;
}
window.selectRole = selectRole; // Make available globally

// --- Login Handlers (Called by Modal Form Submission) ---

/**
 * Handles the login request for the Admin role.
 * Made available globally for modal forms to access via `onsubmit`.
 * @param {Event} e - The form submission event.
 */
async function adminLoginHandler(e) {
    e.preventDefault(); // Stop default form submission

    // Read values from the hidden modal's form fields
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    const admin = { username, password };

    try {
        const response = await fetch(ADMIN_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(admin)
        });

        if (response.ok) {
            const data = await response.json();
            // Assuming the token is returned in the response body (e.g., data.token)
            const token = data.token;
            if (token) {
                selectRole("admin", token);
            } else {
                console.error("Admin Login Failed: Token not received in response.", data);
                // In a future step, replace this with a custom message box UI
            }
        } else {
            console.error("Admin Login Failed: Invalid credentials!", response.status);
            // In a future step, replace this with a custom message box UI
        }
    } catch (error) {
        console.error("An unexpected error occurred during Admin login:", error);
        // In a future step, replace this with a custom error message box UI
    }
}
window.adminLoginHandler = adminLoginHandler; // Make available globally

/**
 * Handles the login request for the Doctor role.
 * Made available globally for modal forms to access via `onsubmit`.
 * @param {Event} e - The form submission event.
 */
async function doctorLoginHandler(e) {
    e.preventDefault(); // Stop default form submission

    // Read values from the hidden modal's form fields
    const email = document.getElementById('doctorEmail').value;
    const password = document.getElementById('doctorPassword').value;

    const doctor = { email, password };

    try {
        const response = await fetch(DOCTOR_API, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctor)
        });

        if (response.ok) {
            const data = await response.json();
            // Assuming the token is returned in the response body (e.g., data.token)
            const token = data.token;
            if (token) {
                selectRole("doctor", token);
            } else {
                console.error("Doctor Login Failed: Token not received in response.", data);
            }
        } else {
            console.error("Doctor Login Failed: Invalid credentials!", response.status);
        }
    } catch (error) {
        console.error("An unexpected error occurred during Doctor login:", error);
    }
}
window.doctorLoginHandler = doctorLoginHandler; // Make available globally


// --- Initial Role Button Listeners (per prompt request) ---

// Ensure the script attaches listeners to the role selection buttons
// on the index.html page to trigger the modal opening.
window.onload = function () {
    const adminBtn = document.getElementById('adminRoleBtn'); // Corrected ID from index.html
    const doctorBtn = document.getElementById('doctorRoleBtn'); // Corrected ID from index.html
    const patientBtn = document.getElementById('patientRoleBtn'); // Corrected ID from index.html

    if (adminBtn) {
        // Overriding the inline onclick for modularity
        adminBtn.onclick = null;
        adminBtn.addEventListener('click', () => {
            // This function is defined in modals.js and is assumed to be global.
            openModal('adminLogin');
            // Temporarily set the role for header rendering context, cleared on redirect
            localStorage.setItem("userRole", "admin");
        });
    }

    if (doctorBtn) {
        doctorBtn.onclick = null;
        doctorBtn.addEventListener('click', () => {
            openModal('doctorLogin');
            localStorage.setItem("userRole", "doctor");
        });
    }

    if (patientBtn) {
        patientBtn.onclick = null;
        patientBtn.addEventListener('click', () => {
             // For patient, we set the role to "patient" and redirect immediately
             // to the public patient dashboard where login/signup modals are available.
             localStorage.setItem("userRole", "patient");
             window.location.href = "/pages/patientDashboard.html";
        });
    }
};

