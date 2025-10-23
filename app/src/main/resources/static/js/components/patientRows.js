// patientRows.js

/*
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");
  console.log("CreatePatientRow :: ", doctorId)
  tr.innerHTML = `
      <td class="patient-id">${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td><img src="../assets/images/addPrescriptionIcon/addPrescription.png" alt="addPrescriptionIcon" class="prescription-btn" data-id="${patient.id}"></img></td>
    `;

  // Attach event listeners
  tr.querySelector(".patient-id").addEventListener("click", () => {
    window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
  });

  tr.querySelector(".prescription-btn").addEventListener("click", () => {
    window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}`;
  });

  return tr;
}
*/

/**
 * Generates a table row (<tr>) for a single patient appointment, used in the Doctor Dashboard.
 * @param {Object} patient - The patient data associated with the appointment.
 * @param {string} appointmentId - The ID of the specific appointment.
 * @param {string} doctorId - The ID of the current doctor.
 * @returns {HTMLTableRowElement} The created table row element.
 */
export function createPatientRow(patient, appointmentId, doctorId) {
  const tr = document.createElement("tr");

  // Use a modern SVG icon for the prescription button instead of a local image path.
  // This uses a pen/edit icon to suggest the action of 'adding/writing' a prescription.
  const prescriptionIcon = `
    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feather feather-edit">
        <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
        <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
    </svg>
  `;

  tr.innerHTML = `
      <td class="patient-id" data-id="${patient.id}">${patient.id}</td>
      <td>${patient.name}</td>
      <td>${patient.phone}</td>
      <td>${patient.email}</td>
      <td>
          <button class="table-action-btn prescription-btn" title="Add Prescription">
              ${prescriptionIcon}
          </button>
      </td>
    `;

  // 1. Event listener for viewing the patient record (via patient ID click)
  const patientIdCell = tr.querySelector(".patient-id");
  if (patientIdCell) {
    // Style this cell to indicate it's clickable (via style.css)
    patientIdCell.style.cursor = 'pointer';
    patientIdCell.style.fontWeight = '600';

    patientIdCell.addEventListener("click", () => {
      window.location.href = `/pages/patientRecord.html?id=${patient.id}&doctorId=${doctorId}`;
    });
  }

  // 2. Event listener for adding a prescription (via button click)
  const prescriptionButton = tr.querySelector(".prescription-btn");
  if (prescriptionButton) {
    prescriptionButton.addEventListener("click", (e) => {
        // Stop event from bubbling up to row/table handlers if any
        e.stopPropagation();
        window.location.href = `/pages/addPrescription.html?appointmentId=${appointmentId}&patientName=${patient.name}`;
    });
  }

  return tr;
}
