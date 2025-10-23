/*
  Step-by-Step Explanation of Header Section Rendering

  This code dynamically renders the header section of the page based on the user's role, session status, and available actions (such as login, logout, or role-switching).

  1. Define the `renderHeader` Function

     * The `renderHeader` function is responsible for rendering the entire header based on the user's session, role, and whether they are logged in.

  2. Select the Header Div

     * The `headerDiv` variable retrieves the HTML element with the ID `header`, where the header content will be inserted.
       ```javascript
       const headerDiv = document.getElementById("header");
       ```

  3. Check if the Current Page is the Root Page

     * The `window.location.pathname` is checked to see if the current page is the root (`/`). If true, the user's session data (role) is removed from `localStorage`, and the header is rendered without any user-specific elements (just the logo and site title).
       ```javascript
       if (window.location.pathname.endsWith("/")) {
         localStorage.removeItem("userRole");
         headerDiv.innerHTML = `
           <header class="header">
             <div class="logo-section">
               <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
               <span class="logo-title">Hospital CMS</span>
             </div>
           </header>`;
         return;
       }
       ```

  4. Retrieve the User's Role and Token from LocalStorage

     * The `role` (user role like admin, patient, doctor) and `token` (authentication token) are retrieved from `localStorage` to determine the user's current session.
       ```javascript
       const role = localStorage.getItem("userRole");
       const token = localStorage.getItem("token");
       ```

  5. Initialize Header Content

     * The `headerContent` variable is initialized with basic header HTML (logo section), to which additional elements will be added based on the user's role.
       ```javascript
       let headerContent = `<header class="header">
         <div class="logo-section">
           <img src="../assets/images/logo/logo.png" alt="Hospital CRM Logo" class="logo-img">
           <span class="logo-title">Hospital CMS</span>
         </div>
         <nav>`;
       ```

  6. Handle Session Expiry or Invalid Login

     * If a user with a role like `loggedPatient`, `admin`, or `doctor` does not have a valid `token`, the session is considered expired or invalid. The user is logged out, and a message is shown.
       ```javascript
       if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
         localStorage.removeItem("userRole");
         alert("Session expired or invalid login. Please log in again.");
         window.location.href = "/";   or a specific login page
         return;
       }
       ```

  7. Add Role-Specific Header Content

     * Depending on the user's role, different actions or buttons are rendered in the header:
       - **Admin**: Can add a doctor and log out.
       - **Doctor**: Has a home button and log out.
       - **Patient**: Shows login and signup buttons.
       - **LoggedPatient**: Has home, appointments, and logout options.
       ```javascript
       else if (role === "admin") {
         headerContent += `
           <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "doctor") {
         headerContent += `
           <button class="adminBtn"  onclick="selectRole('doctor')">Home</button>
           <a href="#" onclick="logout()">Logout</a>`;
       } else if (role === "patient") {
         headerContent += `
           <button id="patientLogin" class="adminBtn">Login</button>
           <button id="patientSignup" class="adminBtn">Sign Up</button>`;
       } else if (role === "loggedPatient") {
         headerContent += `
           <button id="home" class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
           <button id="patientAppointments" class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
           <a href="#" onclick="logoutPatient()">Logout</a>`;
       }
       ```



  9. Close the Header Section



  10. Render the Header Content

     * Insert the dynamically generated `headerContent` into the `headerDiv` element.
       ```javascript
       headerDiv.innerHTML = headerContent;
       ```

  11. Attach Event Listeners to Header Buttons

     * Call `attachHeaderButtonListeners` to add event listeners to any dynamically created buttons in the header (e.g., login, logout, home).
       ```javascript
       attachHeaderButtonListeners();
       ```


  ### Helper Functions

  13. **attachHeaderButtonListeners**: Adds event listeners to login buttons for "Doctor" and "Admin" roles. If clicked, it opens the respective login modal.

  14. **logout**: Removes user session data and redirects the user to the root page.

  15. **logoutPatient**: Removes the patient's session token and redirects to the patient dashboard.

  16. **Render the Header**: Finally, the `renderHeader()` function is called to initialize the header rendering process when the page loads.
*/
------------

// header.js
// Defines the global header structure and role-based navigation logic.

// --- Global Utility Functions for Logout ---

/**
 * Clears session for Admin/Doctor users and redirects to the homepage.
 * Made available globally via the <button onclick="logout()"> attribute.
 */
function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("userRole");
    // Redirect to the role selection page
    window.location.href = "/";
}

/**
 * Clears the session token for loggedPatient and reverts role to "patient".
 * This allows them to see the Login/Signup buttons again on the patient dashboard.
 */
function logoutPatient() {
    localStorage.removeItem("token");
    localStorage.setItem("userRole", "patient");
    // Redirect to the public patient dashboard page
    window.location.href = "/pages/patientDashboard.html";
}

// Attach these global functions to the window object for HTML onclick to access them
window.logout = logout;
window.logoutPatient = logoutPatient;


// --- Header Rendering Logic ---

/**
 * Attaches event listeners to dynamically created header buttons after rendering.
 * Specifically for buttons that interact with other components (like modals).
 */
const attachHeaderButtonListeners = () => {
    // Listener for Admin: Add Doctor button
    const addDocBtn = document.getElementById("addDocBtn");
    if (addDocBtn) {
        addDocBtn.addEventListener('click', () => {
            // openModal is assumed to be globally available from modals.js
            if (typeof openModal === 'function') {
                openModal('addDoctor');
            } else {
                console.error("Error: openModal function is not available.");
            }
        });
    }
};

/**
 * Renders the appropriate header content based on the current user's role and login state.
 */
const renderHeader = () => {
    const headerDiv = document.getElementById("header");
    if (!headerDiv) {
        console.error("Header placeholder div (#header) not found.");
        return;
    }

    // Check if on the root landing page (index.html)
    if (window.location.pathname.endsWith("/") || window.location.pathname.endsWith("index.html")) {
        // Clear session info on the main entry page
        localStorage.removeItem("userRole");
        localStorage.removeItem("token");
    }

    const role = localStorage.getItem("userRole");
    const token = localStorage.getItem("token");
    let headerContent = "";

    // --- 1. Session Expiration / Invalid Token Check ---
    if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
        localStorage.removeItem("userRole");
        // Using a custom modal/message box instead of alert() per constraints
        const message = "Session expired or invalid login. Please log in again.";
        console.error(message);
        // Assuming a global displayMessage (or similar) from util.js exists, if not, logging is sufficient for now.
        // If no message system is built yet, we proceed to redirect:
        window.location.href = "/";
        return;
    }

    // --- 2. Build Header Content based on Role ---

    // Common Logo and Base Structure
    const logoHtml = `
        <div class="header-logo">
            <a href="/">
                <img src="/assets/images/logo/logo.png" alt="Clinic Logo" class="logo-img">
                <span class="logo-text">ClinicMS</span>
            </a>
        </div>
        <nav class="header-nav">
    `;
    const navEnd = `</nav>`;

    if (role === "admin") {
        headerContent = logoHtml + `
            <button id="addDocBtn" class="adminBtn button">Add Doctor</button>
            <a href="#" class="nav-link" onclick="logout()">Logout</a>
        ` + navEnd;

    } else if (role === "doctor") {
        headerContent = logoHtml + `
            <a href="/templates/doctor/doctorDashboard.html" class="nav-link">Home</a>
            <a href="#" class="nav-link" onclick="logout()">Logout</a>
        ` + navEnd;

    } else if (role === "loggedPatient") {
        headerContent = logoHtml + `
            <a href="/pages/patientDashboard.html" class="nav-link">Home</a>
            <a href="/pages/patientAppointments.html" class="nav-link">Appointments</a>
            <a href="#" class="nav-link" onclick="logoutPatient()">Logout</a>
        ` + navEnd;

    } else { // Default to "patient" role or no role (on index.html)
        headerContent = logoHtml + `
            <!-- The 'patient' role sees login/signup links -->
            <a href="#" class="nav-link" id="loginLink">Login</a>
            <a href="#" class="nav-link primary-link" id="signupLink">Sign Up</a>
        ` + navEnd;
    }

    // Finalize Header Injection
    headerDiv.innerHTML = headerContent;

    // Attach listeners for dynamic elements
    attachHeaderButtonListeners();

    // Special listeners for patient/default view (Login/Signup buttons)
    const loginLink = document.getElementById("loginLink");
    const signupLink = document.getElementById("signupLink");

    if (loginLink) {
        loginLink.addEventListener('click', (e) => {
            e.preventDefault();
            // openModal is assumed to be available
            if (typeof openModal === 'function') {
                 openModal('login');
            } else {
                 console.error("Error: openModal function is not available.");
            }
        });
    }

    if (signupLink) {
        signupLink.addEventListener('click', (e) => {
            e.preventDefault();
            if (typeof openModal === 'function') {
                 openModal('signup');
            } else {
                 console.error("Error: openModal function is not available.");
            }
        });
    }
};

// Run the render function immediately when the script loads (deferred)
document.addEventListener('DOMContentLoaded', renderHeader);

// Export renderHeader to be available if needed by render.js
// Note: This export may not be strictly necessary if render.js just calls it,
// but it's good practice for modularity.
export { renderHeader };

