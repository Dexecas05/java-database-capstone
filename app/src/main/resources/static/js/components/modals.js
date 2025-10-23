// modals.js
/*
export function openModal(type) {
  let modalContent = '';
  if (type === 'addDoctor') {
    modalContent = `
         <h2>Add Doctor</h2>
         <input type="text" id="doctorName" placeholder="Doctor Name" class="input-field">
         <select id="specialization" class="input-field select-dropdown">
             <option value="">Specialization</option>
                        <option value="cardiologist">Cardiologist</option>
                        <option value="dermatologist">Dermatologist</option>
                        <option value="neurologist">Neurologist</option>
                        <option value="pediatrician">Pediatrician</option>
                        <option value="orthopedic">Orthopedic</option>
                        <option value="gynecologist">Gynecologist</option>
                        <option value="psychiatrist">Psychiatrist</option>
                        <option value="dentist">Dentist</option>
                        <option value="ophthalmologist">Ophthalmologist</option>
                        <option value="ent">ENT Specialist</option>
                        <option value="urologist">Urologist</option>
                        <option value="oncologist">Oncologist</option>
                        <option value="gastroenterologist">Gastroenterologist</option>
                        <option value="general">General Physician</option>

        </select>
        <input type="email" id="doctorEmail" placeholder="Email" class="input-field">
        <input type="password" id="doctorPassword" placeholder="Password" class="input-field">
        <input type="text" id="doctorPhone" placeholder="Mobile No." class="input-field">
        <div class="availability-container">
        <label class="availabilityLabel">Select Availability:</label>
          <div class="checkbox-group">
              <label><input type="checkbox" name="availability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
              <label><input type="checkbox" name="availability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
              <label><input type="checkbox" name="availability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
              <label><input type="checkbox" name="availability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
          </div>
        </div>
        <button class="dashboard-btn" id="saveDoctorBtn">Save</button>
      `;
  } else if (type === 'patientLogin') {
    modalContent = `
        <h2>Patient Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="loginBtn">Login</button>
      `;
  }
  else if (type === "patientSignup") {
    modalContent = `
      <h2>Patient Signup</h2>
      <input type="text" id="name" placeholder="Name" class="input-field">
      <input type="email" id="email" placeholder="Email" class="input-field">
      <input type="password" id="password" placeholder="Password" class="input-field">
      <input type="text" id="phone" placeholder="Phone" class="input-field">
      <input type="text" id="address" placeholder="Address" class="input-field">
      <button class="dashboard-btn" id="signupBtn">Signup</button>
    `;

  } else if (type === 'adminLogin') {
    modalContent = `
        <h2>Admin Login</h2>
        <input type="text" id="username" name="username" placeholder="Username" class="input-field">
        <input type="password" id="password" name="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="adminLoginBtn" >Login</button>
      `;
  } else if (type === 'doctorLogin') {
    modalContent = `
        <h2>Doctor Login</h2>
        <input type="text" id="email" placeholder="Email" class="input-field">
        <input type="password" id="password" placeholder="Password" class="input-field">
        <button class="dashboard-btn" id="doctorLoginBtn" >Login</button>
      `;
  }

  document.getElementById('modal-body').innerHTML = modalContent;
  document.getElementById('modal').style.display = 'block';

  document.getElementById('closeModal').onclick = () => {
    document.getElementById('modal').style.display = 'none';
  };

  if (type === "patientSignup") {
    document.getElementById("signupBtn").addEventListener("click", signupPatient);
  }

  if (type === "patientLogin") {
    document.getElementById("loginBtn").addEventListener("click", loginPatient);
  }

  if (type === 'addDoctor') {
    document.getElementById('saveDoctorBtn').addEventListener('click', adminAddDoctor);
  }

  if (type === 'adminLogin') {
    document.getElementById('adminLoginBtn').addEventListener('click', adminLoginHandler);
  }

  if (type === 'doctorLogin') {
    document.getElementById('doctorLoginBtn').addEventListener('click', doctorLoginHandler);
  }
}
*/

// Access the global modal elements
const modal = document.getElementById('modal');
const modalBody = document.getElementById('modal-body');

// Helper to close the modal
function closeModal() {
    if (modal) {
        modal.classList.remove('open');
        modalBody.innerHTML = ''; // Clear content
    }
}

/**
 * Dynamically generates the content for and opens a specific modal type.
 * Note: Global functions (e.g., loginPatient, adminAddDoctor) must be defined
 * in their respective dashboard/logic files and exposed to the window object.
 * @param {string} type - The type of modal to open (e.g., 'addDoctor', 'patientLogin').
 */
export function openModal(type) {
  let modalContent = '';

  // 1. Define Modal Content
  if (type === 'addDoctor') {
    // Admin action: Add Doctor
    modalContent = `
         <h2>Add New Doctor</h2>
         <form id="addDoctorForm">
             <div class="input-group">
                 <input type="text" id="addDoctorName" placeholder="Doctor Name" class="input-field" required>
             </div>
             <div class="input-group">
                 <select id="addDoctorSpecialization" class="input-field select-dropdown" required>
                     <option value="" disabled selected>Select Specialization</option>
                     <option value="cardiologist">Cardiologist</option>
                     <option value="dermatologist">Dermatologist</option>
                     <option value="neurologist">Neurologist</option>
                     <option value="pediatrician">Pediatrician</option>
                     <option value="orthopedic">Orthopedic</option>
                     <option value="gynecologist">Gynecologist</option>
                     <option value="psychiatrist">Psychiatrist</option>
                     <option value="dentist">Dentist</option>
                     <option value="ophthalmologist">Ophthalmologist</option>
                     <option value="ent">ENT Specialist</option>
                     <option value="urologist">Urologist</option>
                     <option value="oncologist">Oncologist</option>
                     <option value="gastroenterologist">Gastroenterologist</option>
                     <option value="general">General Physician</option>
                 </select>
             </div>
             <div class="input-group">
                 <input type="email" id="addDoctorEmail" placeholder="Email" class="input-field" required>
             </div>
             <div class="input-group">
                 <input type="password" id="addDoctorPassword" placeholder="Password" class="input-field" required>
             </div>
             <div class="input-group">
                 <input type="text" id="addDoctorPhone" placeholder="Mobile No." class="input-field">
             </div>
             <div class="availability-container">
                 <label class="availabilityLabel">Select Availability:</label>
                 <div class="checkbox-group">
                     <label><input type="checkbox" name="addDoctorAvailability" value="09:00-10:00"> 9:00 AM - 10:00 AM</label>
                     <label><input type="checkbox" name="addDoctorAvailability" value="10:00-11:00"> 10:00 AM - 11:00 AM</label>
                     <label><input type="checkbox" name="addDoctorAvailability" value="11:00-12:00"> 11:00 AM - 12:00 PM</label>
                     <label><input type="checkbox" name="addDoctorAvailability" value="12:00-13:00"> 12:00 PM - 1:00 PM</label>
                 </div>
             </div>
             <button type="submit" class="dashboard-btn" id="saveDoctorBtn">Add Doctor</button>
         </form>
      `;
  } else if (type === 'patientLogin') {
    // Patient action: Login - IDs fixed to match patientDashboard.js
    modalContent = `
        <h2>Patient Login</h2>
        <form id="patientLoginForm">
            <div class="input-group">
                <input type="email" id="loginEmail" placeholder="Email" class="input-field" required>
            </div>
            <div class="input-group">
                <input type="password" id="loginPassword" placeholder="Password" class="input-field" required>
            </div>
            <button type="submit" class="dashboard-btn" id="loginBtn">Login</button>
        </form>
      `;
  }
  else if (type === "patientSignup") {
    // Patient action: Signup - IDs fixed to match patientDashboard.js
    modalContent = `
      <h2>Patient Signup</h2>
      <form id="patientSignupForm">
          <div class="input-group">
              <input type="text" id="signupName" placeholder="Full Name" class="input-field" required>
          </div>
          <div class="input-group">
              <input type="email" id="signupEmail" placeholder="Email Address" class="input-field" required>
          </div>
          <div class="input-group">
              <input type="password" id="signupPassword" placeholder="Password" class="input-field" required>
          </div>
          <div class="input-group">
              <input type="text" id="signupMobile" placeholder="Mobile Phone" class="input-field">
          </div>
          <div class="input-group">
              <input type="text" id="signupAddress" placeholder="Address" class="input-field">
          </div>
          <button type="submit" class="dashboard-btn" id="signupBtn">Create Account</button>
      </form>
    `;
  } else if (type === 'adminLogin') {
    // Admin action: Login - IDs prefixed
    modalContent = `
        <h2>Admin Login</h2>
        <form id="adminLoginForm">
            <div class="input-group">
                <input type="text" id="adminUsername" placeholder="Username" class="input-field" required>
            </div>
            <div class="input-group">
                <input type="password" id="adminPassword" placeholder="Password" class="input-field" required>
            </div>
            <button type="submit" class="dashboard-btn" id="adminLoginBtn" >Login</button>
        </form>
      `;
  } else if (type === 'doctorLogin') {
    // Doctor action: Login - IDs prefixed
    modalContent = `
        <h2>Doctor Login</h2>
        <form id="doctorLoginForm">
            <div class="input-group">
                <input type="email" id="doctorLoginEmail" placeholder="Email" class="input-field" required>
            </div>
            <div class="input-group">
                <input type="password" id="doctorLoginPassword" placeholder="Password" class="input-field" required>
            </div>
            <button type="submit" class="dashboard-btn" id="doctorLoginBtn" >Login</button>
        </form>
      `;
  } else if (type === 'bookAppointment') {
    // Patient action: Book Appointment Modal (New)
    modalContent = `
        <h2>Book Appointment</h2>
        <form id="bookAppointmentForm">
            <div class="input-group">
                <label for="bookDate">Date</label>
                <input type="date" id="bookDate" class="input-field" required>
            </div>
            <div class="input-group">
                <label for="bookTime">Time Slot</label>
                <select id="bookTime" class="input-field select-dropdown" required>
                    <option value="" disabled selected>Select an available time</option>
                    <!-- Options populated dynamically by calling function -->
                </select>
            </div>
            <div class="input-group">
                <label for="bookReason">Reason for Visit</label>
                <textarea id="bookReason" class="input-field" rows="3" placeholder="Briefly describe your reason for consultation..."></textarea>
            </div>
            <button type="submit" class="dashboard-btn" id="confirmBookingBtn">Confirm Booking</button>
        </form>
    `;
  }

  // 2. Insert Content and Open Modal
  if (!modalBody) {
      console.error("Modal body element not found.");
      return;
  }

  // Clear previous content and insert new HTML with close button
  modalBody.innerHTML = `
      <span class="close" id="closeModal">&times;</span>
      ${modalContent}
  `;

  // Use CSS class to display modal
  if (modal) {
      modal.classList.add('open');
      // Close modal if user clicks outside the modal content
      modal.onclick = (e) => {
          if (e.target === modal) {
              closeModal();
          }
      };
  }

  // Set up close button listener
  const closeBtn = document.getElementById('closeModal');
  if (closeBtn) {
      closeBtn.onclick = closeModal;
  }

  // 3. Bind Event Handlers to Forms (using form submission for better UX)

  if (type === "patientSignup") {
      const form = document.getElementById("patientSignupForm");
      if (form && typeof window.signupPatient === 'function') {
          form.addEventListener("submit", (e) => {
              e.preventDefault();
              window.signupPatient();
          });
      }
  }

  if (type === "patientLogin") {
      const form = document.getElementById("patientLoginForm");
      if (form && typeof window.loginPatient === 'function') {
          form.addEventListener("submit", (e) => {
              e.preventDefault();
              window.loginPatient();
          });
      }
  }

  if (type === 'addDoctor') {
      const form = document.getElementById('addDoctorForm');
      if (form && typeof window.adminAddDoctor === 'function') {
          form.addEventListener('submit', (e) => {
              e.preventDefault();
              window.adminAddDoctor();
          });
      }
  }

  if (type === 'adminLogin') {
      const form = document.getElementById('adminLoginForm');
      if (form && typeof window.adminLoginHandler === 'function') {
          form.addEventListener('submit', (e) => {
              e.preventDefault();
              window.adminLoginHandler();
          });
      }
  }

  if (type === 'doctorLogin') {
      const form = document.getElementById('doctorLoginForm');
      if (form && typeof window.doctorLoginHandler === 'function') {
          form.addEventListener('submit', (e) => {
              e.preventDefault();
              window.doctorLoginHandler();
          });
      }
  }

  if (type === 'bookAppointment') {
      const form = document.getElementById('bookAppointmentForm');
      if (form && typeof window.confirmBooking === 'function') {
          form.addEventListener('submit', (e) => {
              e.preventDefault();
              window.confirmBooking();
          });
      }
  }

  // Expose the close function globally so other files can close the modal manually
  window.closeModal = closeModal;
}
