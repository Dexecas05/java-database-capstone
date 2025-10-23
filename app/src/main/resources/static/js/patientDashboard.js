/*
// patientDashboard.js
import { getDoctors } from './services/doctorServices.js';
import { openModal } from './components/modals.js';
import { createDoctorCard } from './components/doctorCard.js';
import { filterDoctors } from './services/doctorServices.js';//call the same function to avoid duplication coz the functionality was same
import { patientSignup, patientLogin } from './services/patientServices.js';



document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();
});

document.addEventListener("DOMContentLoaded", () => {
  const btn = document.getElementById("patientSignup");
  if (btn) {
    btn.addEventListener("click", () => openModal("patientSignup"));
  }
});

document.addEventListener("DOMContentLoaded", () => {
  const loginBtn = document.getElementById("patientLogin")
  if (loginBtn) {
    loginBtn.addEventListener("click", () => {
      openModal("patientLogin")
    })
  }
})

function loadDoctorCards() {
  getDoctors()
    .then(doctors => {
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
      });
    })
    .catch(error => {
      console.error("Failed to load doctors:", error);
    });
}
// Filter Input
document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);



function filterDoctorsOnChange() {
  const searchBar = document.getElementById("searchBar").value.trim();
  const filterTime = document.getElementById("filterTime").value;
  const filterSpecialty = document.getElementById("filterSpecialty").value;


  const name = searchBar.length > 0 ? searchBar : null;
  const time = filterTime.length > 0 ? filterTime : null;
  const specialty = filterSpecialty.length > 0 ? filterSpecialty : null;

  filterDoctors(name, time, specialty)
    .then(response => {
      const doctors = response.doctors;
      const contentDiv = document.getElementById("content");
      contentDiv.innerHTML = "";

      if (doctors.length > 0) {
        console.log(doctors);
        doctors.forEach(doctor => {
          const card = createDoctorCard(doctor);
          contentDiv.appendChild(card);
        });
      } else {
        contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        console.log("Nothing");
      }
    })
    .catch(error => {
      console.error("Failed to filter doctors:", error);
      alert("❌ An error occurred while filtering doctors.");
    });
}

window.signupPatient = async function () {
  try {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const phone = document.getElementById("phone").value;
    const address = document.getElementById("address").value;

    const data = { name, email, password, phone, address };
    const { success, message } = await patientSignup(data);
    if (success) {
      alert(message);
      document.getElementById("modal").style.display = "none";
      window.location.reload();
    }
    else alert(message);
  } catch (error) {
    console.error("Signup failed:", error);
    alert("❌ An error occurred while signing up.");
  }
};

window.loginPatient = async function () {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
      email,
      password
    }
    console.log("loginPatient :: ", data)
    const response = await patientLogin(data);
    console.log("Status Code:", response.status);
    console.log("Response OK:", response.ok);
    if (response.ok) {
      const result = await response.json();
      console.log(result);
      selectRole('loggedPatient');
      localStorage.setItem('token', result.token)
      window.location.href = '/pages/loggedPatientDashboard.html';
    } else {
      alert('❌ Invalid credentials!');
    }
  }
  catch (error) {
    alert("❌ Failed to Login : ", error);
    console.log("Error :: loginPatient :: ", error)
  }

}
*/

// patientDashboard.js
// Handles core logic for the Patient dashboard: viewing and filtering doctors, and handling patient authentication (login/signup).

// --- Imports ---
import { createDoctorCard } from "../components/doctorCard.js";
import { openModal } from "../components/modals.js";
import { getDoctors, filterDoctors } from "../services/doctorServices.js";
import { patientLogin, patientSignup } from "../services/patientServices.js";

// --- DOM Elements ---
const contentDiv = document.getElementById("content");
const searchBar = document.getElementById("searchBar");
const filterTime = document.getElementById("filterTime");
const filterSpecialty = document.getElementById("filterSpecialty");


// --- Utility & Render Functions ---

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
        contentDiv.innerHTML = `<p class="no-results-message">No doctors found matching your criteria. Try adjusting the filters.</p>`;
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

/**
 * Handles the change event for search bar and filter dropdowns.
 * Fetches and renders filtered results.
 */
async function filterDoctorsOnChange() {
    const name = searchBar ? searchBar.value.trim() : null;
    const time = filterTime ? filterTime.value : null;
    const specialty = filterSpecialty ? filterSpecialty.value : null;

    // Show loading state during filter
    if (contentDiv) {
        contentDiv.innerHTML = `<div class="loading-spinner"></div>`;
    }

    const filteredList = await filterDoctors(name, time, specialty);
    renderDoctorCards(filteredList);
}


// --- Patient Authentication Handlers (Global) ---

/**
 * Handles the form submission for patient signup via modal.
 * This function needs to be global so the dynamically created modal form can call it.
 */
window.signupPatient = async function () {
    const name = document.getElementById('signupName').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const mobile = document.getElementById('signupMobile').value;
    const address = document.getElementById('signupAddress').value;

    const signupData = { name, email, password, mobile, address };

    const result = await patientSignup(signupData);

    if (result.success) {
        console.log("Signup successful! Opening login modal.");
        const modal = document.getElementById('modal');
        if (modal) modal.classList.add('hidden');

        // Open login modal immediately for next step
        openModal('patientLogin');
    } else {
        console.error("Signup failed: " + result.message);
        // In a real app, this would show a user-facing error message in the modal
    }
};

/**
 * Handles the form submission for patient login via modal.
 * This function needs to be global so the dynamically created modal form can call it.
 */
window.loginPatient = async function () {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;

    const loginData = { email, password };

    try {
        const response = await patientLogin(loginData);

        if (response.ok) {
            const data = await response.json();
            // Assuming the response includes the JWT token and patient ID
            const token = data.token;
            const userId = data.userId;

            localStorage.setItem("token", token);
            localStorage.setItem("userRole", "loggedPatient");
            localStorage.setItem("userId", userId);

            // Redirect to the newly logged-in state of the patient dashboard
            window.location.href = "/patientDashboard.html";
        } else {
            const errorData = await response.json();
            console.error("Login failed: " + (errorData.message || "Invalid credentials."));
            // In a real app, this would show a user-facing error message in the modal
        }
    } catch (error) {
        console.error("Login network or parsing error:", error);
    }
};


// --- Initialization ---

window.addEventListener('DOMContentLoaded', () => {
    // 1. Initial Data Load
    loadDoctorCards();

    // 2. Filter Listeners
    if (searchBar) {
        searchBar.addEventListener("input", filterDoctorsOnChange);
    }
    if (filterTime) {
        filterTime.addEventListener("change", filterDoctorsOnChange);
    }
    if (filterSpecialty) {
        filterSpecialty.addEventListener("change", filterDoctorsOnChange);
    }

    // 3. Modal Trigger Listeners (For non-logged-in "patient" role buttons, usually defined in header.js)
    const signupBtn = document.getElementById("patientSignup");
    const loginBtn = document.getElementById("patientLogin");

    // We check for these buttons because they are dynamically rendered by header.js
    if (signupBtn) {
        signupBtn.addEventListener("click", () => openModal("patientSignup"));
    }
    if (loginBtn) {
        loginBtn.addEventListener("click", () => openModal("patientLogin"));
    }
});
